package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.adapter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationAdapterException;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationDomainCommandPort;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationErrorCodes;

/**
 * Local deterministic implementation of {@link MigrationDomainCommandPort}, mirroring
 * {@code LocalDeterministicPassthroughIntegrationAdapter}/{@code LocalDeterministicFiscalAdapter}.
 * Never touches a business aggregate (INV-MIG-003); it only computes a stable, idempotent command
 * identifier per {@code (migrationJobId, entityCategory)}.
 * <ul>
 *   <li>{@link #invokeImportCommand} rejects a category whose name contains the literal marker
 *       {@code "FAIL"} (case-insensitive) to model a deterministic adapter failure, exercising the
 *       bounded-retry/checkpoint-resume path in tests without random behaviour.</li>
 *   <li>Otherwise it returns {@code local-import-command-<sha256(migrationJobId|entityCategory)>},
 *       the same value on every call for the same inputs (idempotent re-invocation, RN-004).</li>
 * </ul>
 */
@Component
public class LocalDeterministicMigrationDomainCommandAdapter implements MigrationDomainCommandPort {

    private static final String COMMAND_ID_PREFIX = "local-import-command-";

    @Override
    public String invokeImportCommand(String migrationJobId, String entityCategory, int recordCount) {
        if (entityCategory != null && entityCategory.toUpperCase(Locale.ROOT).contains("FAIL")) {
            throw new MigrationAdapterException(
                    "Local deterministic migration adapter could not invoke the import command for category '"
                            + entityCategory + "'.",
                    MigrationErrorCodes.MIGRATION_DOMAIN_COMMAND_FAILED);
        }
        return COMMAND_ID_PREFIX + sha256(migrationJobId + "|" + entityCategory).substring(0, 24);
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 must be available on the JVM.", exception);
        }
    }
}
