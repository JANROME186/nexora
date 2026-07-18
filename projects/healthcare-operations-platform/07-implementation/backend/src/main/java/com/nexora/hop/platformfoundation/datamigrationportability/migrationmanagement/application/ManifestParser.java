package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationManifest;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.InvalidMigrationCommandException;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationErrorCodes;

/**
 * Parses and verifies {@code manifest.yaml} per the HOP Open Data Ingestion Standard
 * (NXF-ODI-STD-001) using SnakeYAML (already on the classpath transitively; no new dependency
 * introduced for this parser). Implements CUS-MIG-010-01's manifest/checksum verification
 * (RN-001, INV-MIG-001).
 */
@Component
public class ManifestParser {

    private static final List<String> REQUIRED_FIELDS = List.of(
            "source_system_name", "exporting_organization", "export_datetime", "export_timezone",
            "exported_by", "contact_email", "files", "entity_counts", "checksum_algorithm", "checksums",
            "declared_formats", "declared_encoding");

    /** @throws InvalidMigrationCommandException if the manifest is missing, malformed or incomplete */
    public MigrationManifest parse(String manifestYamlText) {
        if (manifestYamlText == null || manifestYamlText.isBlank()) {
            throw new InvalidMigrationCommandException(
                    "manifest.yaml is required.", MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
        }
        Map<String, Object> parsed;
        try {
            parsed = new Yaml().load(manifestYamlText);
        } catch (RuntimeException exception) {
            throw new InvalidMigrationCommandException(
                    "manifest.yaml could not be parsed as YAML: " + exception.getMessage(),
                    MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
        }
        if (parsed == null) {
            throw new InvalidMigrationCommandException(
                    "manifest.yaml is empty.", MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
        }
        for (String field : REQUIRED_FIELDS) {
            if (!parsed.containsKey(field) || parsed.get(field) == null) {
                throw new InvalidMigrationCommandException(
                        "manifest.yaml is missing required field '" + field + "'.",
                        MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
            }
        }

        return new MigrationManifest(
                text(parsed, "source_system_name"),
                parsed.containsKey("source_system_version") ? text(parsed, "source_system_version") : null,
                text(parsed, "exporting_organization"),
                parseInstant(text(parsed, "export_datetime")),
                text(parsed, "export_timezone"),
                text(parsed, "exported_by"),
                text(parsed, "contact_email"),
                stringList(parsed.get("files")),
                intMap(parsed.get("entity_counts")),
                text(parsed, "checksum_algorithm"),
                stringMap(parsed.get("checksums")),
                stringList(parsed.get("declared_formats")),
                text(parsed, "declared_encoding"));
    }

    /**
     * RN-001/INV-MIG-001: every file the manifest declares must checksum-verify against the bytes
     * actually present in the uploaded package before ingestion proceeds any further.
     *
     * @throws InvalidMigrationCommandException if any declared file is missing or its checksum
     *         does not match
     */
    public void verifyChecksums(MigrationManifest manifest, Map<String, byte[]> fileContentsByName) {
        for (String fileName : manifest.files()) {
            byte[] content = fileContentsByName.get(fileName);
            if (content == null) {
                throw new InvalidMigrationCommandException(
                        "manifest.yaml declares file '" + fileName + "' but it was not found in the package.",
                        MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
            }
            String expected = manifest.checksums().get(fileName);
            if (expected == null || expected.isBlank()) {
                throw new InvalidMigrationCommandException(
                        "manifest.yaml does not declare a checksum for file '" + fileName + "'.",
                        MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
            }
            String actual = digest(manifest.checksumAlgorithm(), content);
            if (!actual.equalsIgnoreCase(expected)) {
                throw new InvalidMigrationCommandException(
                        "Checksum mismatch for file '" + fileName + "': manifest declared " + expected
                                + " but the package content hashes to " + actual + ".",
                        MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
            }
        }
    }

    private static String digest(String algorithm, byte[] content) {
        String javaAlgorithm = switch (algorithm == null ? "" : algorithm.toLowerCase(java.util.Locale.ROOT)) {
            case "sha256", "sha-256" -> "SHA-256";
            case "sha512", "sha-512" -> "SHA-512";
            case "md5" -> "MD5";
            default -> throw new InvalidMigrationCommandException(
                    "Unsupported checksum_algorithm '" + algorithm + "'.",
                    MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
        };
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(javaAlgorithm);
            return java.util.HexFormat.of().formatHex(messageDigest.digest(content));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(javaAlgorithm + " must be available on the JVM.", exception);
        }
    }

    private static Instant parseInstant(String value) {
        try {
            return Instant.parse(value);
        } catch (RuntimeException exception) {
            throw new InvalidMigrationCommandException(
                    "manifest.yaml export_datetime must be an ISO-8601 instant.",
                    MigrationErrorCodes.MIGRATION_MANIFEST_INVALID_OR_MISSING);
        }
    }

    private static String text(Map<String, Object> parsed, String key) {
        Object value = parsed.get(key);
        return value == null ? null : String.valueOf(value).trim();
    }

    @SuppressWarnings("unchecked")
    private static List<String> stringList(Object value) {
        if (value == null) {
            return List.of();
        }
        return ((List<Object>) value).stream().map(String::valueOf).toList();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> stringMap(Object value) {
        Map<String, String> result = new LinkedHashMap<>();
        if (value == null) {
            return result;
        }
        ((Map<Object, Object>) value).forEach((key, mapValue) -> result.put(String.valueOf(key), String.valueOf(mapValue)));
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Integer> intMap(Object value) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (value == null) {
            return result;
        }
        ((Map<Object, Object>) value).forEach((key, mapValue) -> result.put(
                String.valueOf(key), Integer.valueOf(String.valueOf(mapValue))));
        return result;
    }
}
