package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationManifest;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.InvalidMigrationCommandException;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationErrorCodes;

class ManifestParserTest {

    private final ManifestParser parser = new ManifestParser();

    @Test
    void parsesAWellFormedManifest() {
        MigrationManifest manifest = parser.parse("""
                source_system_name: LegacyLIS
                exporting_organization: Legacy Labs
                export_datetime: "2026-01-01T00:00:00Z"
                export_timezone: UTC
                exported_by: exporter@legacy.com
                contact_email: exporter@legacy.com
                files: [patients.csv, doctors.csv]
                entity_counts: {patients.csv: 2, doctors.csv: 1}
                checksum_algorithm: sha256
                checksums: {patients.csv: "abc", doctors.csv: "def"}
                declared_formats: [csv]
                declared_encoding: UTF-8
                """);

        assertThat(manifest.sourceSystemName()).isEqualTo("LegacyLIS");
        assertThat(manifest.files()).containsExactly("patients.csv", "doctors.csv");
        assertThat(manifest.entityCounts()).containsEntry("patients.csv", 2);
    }

    @Test
    void rejectsABlankManifest() {
        assertThatThrownBy(() -> parser.parse(""))
                .isInstanceOf(InvalidMigrationCommandException.class)
                .extracting("code").isEqualTo(MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
    }

    @Test
    void rejectsAManifestMissingARequiredField() {
        assertThatThrownBy(() -> parser.parse("source_system_name: LegacyLIS"))
                .isInstanceOf(InvalidMigrationCommandException.class)
                .extracting("code").isEqualTo(MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
    }

    @Test
    void rejectsUnparsableYaml() {
        assertThatThrownBy(() -> parser.parse("not: valid: yaml: [")).isInstanceOf(InvalidMigrationCommandException.class);
    }

    @Test
    void verifyChecksumsPassesWhenContentMatchesTheDeclaredHash() throws Exception {
        String content = "id,name\n1,Alice\n";
        String checksum = sha256Hex(content);
        MigrationManifest manifest = manifestFor("patients.csv", checksum);

        parser.verifyChecksums(manifest, Map.of("patients.csv", content.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void verifyChecksumsRejectsAMismatchedHash() {
        MigrationManifest manifest = manifestFor("patients.csv", "0".repeat(64));

        assertThatThrownBy(() -> parser.verifyChecksums(
                manifest, Map.of("patients.csv", "id,name\n1,Alice\n".getBytes(StandardCharsets.UTF_8))))
                .isInstanceOf(InvalidMigrationCommandException.class)
                .extracting("code").isEqualTo(MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
    }

    @Test
    void verifyChecksumsRejectsAMissingDeclaredFile() {
        MigrationManifest manifest = manifestFor("patients.csv", "0".repeat(64));

        assertThatThrownBy(() -> parser.verifyChecksums(manifest, Map.of()))
                .isInstanceOf(InvalidMigrationCommandException.class);
    }

    private static MigrationManifest manifestFor(String fileName, String checksum) {
        return new MigrationManifest(
                "LegacyLIS", null, "Legacy Labs", java.time.Instant.parse("2026-01-01T00:00:00Z"), "UTC",
                "exporter@legacy.com", "exporter@legacy.com", java.util.List.of(fileName), Map.of(fileName, 1),
                "sha256", Map.of(fileName, checksum), java.util.List.of("csv"), "UTF-8");
    }

    private static String sha256Hex(String content) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(content.getBytes(StandardCharsets.UTF_8)));
    }
}
