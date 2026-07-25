package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackage;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackageRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersionRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.InvalidMarketplaceCommandException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceConflictException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceEntityNotFoundException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Compiles the generatable outputs of BCM-PLT-011's packagecatalog capability (AGG-030
 * MarketplacePackage/PackageVersion): submission, publication, certification, retirement and
 * published-catalog listing (RN-MKT-001, RN-MKT-004, INV-MKT-001).
 */
@Service
public class PackageCatalogService {

    private final MarketplacePackageRepository packageRepository;
    private final PackageVersionRepository versionRepository;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PackageCatalogService(
            MarketplacePackageRepository packageRepository, PackageVersionRepository versionRepository,
            AuditRecorder auditRecorder) {
        this(packageRepository, versionRepository, auditRecorder, Clock.systemUTC());
    }

    PackageCatalogService(
            MarketplacePackageRepository packageRepository, PackageVersionRepository versionRepository,
            AuditRecorder auditRecorder, Clock clock) {
        this.packageRepository = packageRepository;
        this.versionRepository = versionRepository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** RN-MKT-001: a package must map to at least one Business Capability Package or extension point. */
    public MarketplacePackage submitPackage(
            String code, String name, String category, List<String> capabilityMappings, String initialVersion,
            String actorId) {
        String submittedCode = requiredText(code, "Package code is required.");
        if (packageRepository.findByCode(submittedCode).isPresent()) {
            throw new MarketplaceConflictException(
                    "Package code " + submittedCode + " already exists.", MarketplaceErrorCodes.PACKAGE_VERSION_CONFLICT);
        }
        String submittedName = requiredText(name, "Package name is required.");
        String submittedCategory = requiredText(category, "Package category is required.");
        if (capabilityMappings == null || capabilityMappings.isEmpty()) {
            throw new InvalidMarketplaceCommandException(
                    "A marketplace package must map to at least one capability package or extension point.",
                    MarketplaceErrorCodes.PACKAGE_CAPABILITY_MAPPING_REQUIRED);
        }
        String version = requiredText(initialVersion, "Initial version is required.");
        String actor = requiredText(actorId, "Actor id is required.");

        LocalDateTime now = LocalDateTime.now(clock);
        AuditMetadata audit = new AuditMetadata(actor, now, actor, now);
        MarketplacePackage created = packageRepository.save(new MarketplacePackage(
                newId(), submittedCode, submittedName, submittedCategory, List.copyOf(capabilityMappings),
                MarketplacePackage.STATUS_SUBMITTED, audit));
        versionRepository.save(new PackageVersion(
                newId(), created.packageId(), version, PackageVersion.STATUS_DRAFT, false, false, false, false,
                null, audit));
        auditRecorder.recordSystemEvent(
                "platform", "MarketplacePackageSubmitted", "MarketplacePackage", created.packageId(),
                "{\"code\":\"%s\",\"version\":\"%s\"}".formatted(submittedCode, version));
        return created;
    }

    public List<MarketplacePackage> listPublishedPackages() {
        return packageRepository.findByStatus(MarketplacePackage.STATUS_PUBLISHED);
    }

    public MarketplacePackage getPackage(String packageId) {
        return requirePackage(packageId);
    }

    /** RN-MKT-004: publication requires the target version to already have passed certification. */
    public MarketplacePackage publishPackage(String packageId, String version, String actorId) {
        MarketplacePackage current = requirePackage(packageId);
        PackageVersion targetVersion = requireVersion(packageId, version);
        if (!PackageVersion.STATUS_CERTIFIED.equals(targetVersion.lifecycleStatus())) {
            throw new MarketplaceConflictException(
                    "Version " + version + " of package " + packageId + " must be certified before publication.",
                    MarketplaceErrorCodes.PACKAGE_VERSION_CONFLICT);
        }
        versionRepository.save(withVersionStatus(targetVersion, PackageVersion.STATUS_PUBLISHED, actorId));
        MarketplacePackage published = packageRepository.save(new MarketplacePackage(
                current.packageId(), current.code(), current.name(), current.category(),
                current.capabilityMappings(), MarketplacePackage.STATUS_PUBLISHED, touched(current.audit(), actorId)));
        auditRecorder.recordSystemEvent(
                "platform", "MarketplacePackagePublished", "MarketplacePackage", published.packageId(),
                "{\"version\":\"%s\"}".formatted(version));
        return published;
    }

    public PackageVersion getPackageVersion(String packageId, String version) {
        return requireVersion(packageId, version);
    }

    /**
     * INV-MKT-001: a version cannot be published unless compatibility, security review, support
     * model and telemetry model are all approved (RN-MKT-004).
     */
    public PackageVersion certifyPackageVersion(
            String packageId, String version, boolean compatibilityApproved, boolean securityReviewApproved,
            boolean supportModelApproved, boolean telemetryModelApproved, String actorId) {
        return certifyPackageVersion(packageId, version, compatibilityApproved, securityReviewApproved,
                supportModelApproved, telemetryModelApproved, null, actorId);
    }

    /**
     * {@code compatibilityMetadataText} is the optional delimited declared-compatibility metadata
     * consumed by {@code CompatibilityEvaluator} (COM-MOD-017-BE-002); {@code null} leaves any
     * previously declared metadata unchanged, matching {@code certifyPackageVersion}'s general
     * pattern of a privileged operator progressively completing a version's certification record.
     */
    public PackageVersion certifyPackageVersion(
            String packageId, String version, boolean compatibilityApproved, boolean securityReviewApproved,
            boolean supportModelApproved, boolean telemetryModelApproved, String compatibilityMetadataText,
            String actorId) {
        requirePackage(packageId);
        PackageVersion current = requireVersion(packageId, version);
        String metadataText = compatibilityMetadataText != null
                ? compatibilityMetadataText : current.compatibilityMetadataText();
        PackageVersion updated = new PackageVersion(
                current.versionId(), current.packageId(), current.version(),
                current.lifecycleStatus(), compatibilityApproved, securityReviewApproved, supportModelApproved,
                telemetryModelApproved, metadataText, touched(current.audit(), actorId));
        if (!updated.isReadyForPublication()) {
            PackageVersion saved = versionRepository.save(updated);
            auditRecorder.recordSystemEvent(
                    "platform", "MarketplacePackageVersionCertificationIncomplete", "PackageVersion",
                    saved.versionId(), "{}");
            return saved;
        }
        PackageVersion certified = versionRepository.save(new PackageVersion(
                updated.versionId(), updated.packageId(), updated.version(), PackageVersion.STATUS_CERTIFIED,
                true, true, true, true, metadataText, updated.audit()));
        auditRecorder.recordSystemEvent(
                "platform", "MarketplacePackageVersionCertified", "PackageVersion", certified.versionId(), "{}");
        return certified;
    }

    public PackageVersion retirePackageVersion(String packageId, String version, String actorId) {
        requirePackage(packageId);
        PackageVersion current = requireVersion(packageId, version);
        PackageVersion retired = versionRepository.save(
                withVersionStatus(current, PackageVersion.STATUS_RETIRED, actorId));
        auditRecorder.recordSystemEvent(
                "platform", "MarketplacePackageVersionRetired", "PackageVersion", retired.versionId(), "{}");
        return retired;
    }

    private PackageVersion withVersionStatus(PackageVersion source, String status, String actorId) {
        return new PackageVersion(
                source.versionId(), source.packageId(), source.version(), status, source.compatibilityApproved(),
                source.securityReviewApproved(), source.supportModelApproved(), source.telemetryModelApproved(),
                source.compatibilityMetadataText(), touched(source.audit(), actorId));
    }

    private MarketplacePackage requirePackage(String packageId) {
        return packageRepository.findById(requiredText(packageId, "Package id is required."))
                .orElseThrow(() -> new MarketplaceEntityNotFoundException(
                        "Marketplace package was not found.", MarketplaceErrorCodes.PACKAGE_NOT_FOUND));
    }

    private PackageVersion requireVersion(String packageId, String version) {
        return versionRepository.findByPackageIdAndVersion(
                        requiredText(packageId, "Package id is required."),
                        requiredText(version, "Version is required."))
                .orElseThrow(() -> new MarketplaceEntityNotFoundException(
                        "Package version was not found.", MarketplaceErrorCodes.PACKAGE_VERSION_NOT_FOUND));
    }

    private AuditMetadata touched(AuditMetadata audit, String actorId) {
        return new AuditMetadata(
                audit.createdBy(), audit.createdAt(), requiredText(actorId, "Actor id is required."),
                LocalDateTime.now(clock));
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidMarketplaceCommandException(message, MarketplaceErrorCodes.MARKETPLACE_COMMAND_INVALID);
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
