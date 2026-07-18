package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.adapter.out.adapter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.ExternalMessageEnvelope;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAcknowledgement;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAdapterException;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationAdapterPort;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.NormalizedClinicalMessage;

/**
 * Local deterministic implementation of {@link IntegrationAdapterPort} (the "local deterministic
 * passthrough adapter" named by BCM-PLT-004's generation-plan.yaml). Explicitly not a real
 * HL7v2/FHIR/ASTM/DICOM protocol parser — see TD-BE-013. Deterministic, contrived behaviour for
 * local development and tests, mirroring {@code LocalDeterministicFiscalAdapter}:
 * <ul>
 *   <li>{@link #receiveMessage} rejects a blank payload; otherwise computes an opaque SHA-256
 *       reference and never stores or echoes the raw payload elsewhere.</li>
 *   <li>{@link #normalizeMessage} rejects a payload containing the literal marker
 *       {@code "INVALID"} (case-insensitive) to model a normalization failure deterministically;
 *       otherwise extracts {@code key=value;key2=value2} pairs into canonical fields, falling
 *       back to a single {@code payloadLength} field.</li>
 * </ul>
 */
@Component
public class LocalDeterministicPassthroughIntegrationAdapter implements IntegrationAdapterPort {

    private static final String PROVIDER_ID = "local-passthrough";

    @Override
    public ExternalMessageEnvelope receiveMessage(String rawPayload, String protocolHint) {
        if (rawPayload == null || rawPayload.isBlank()) {
            throw new IntegrationAdapterException("Raw payload must not be blank.", "MISSING_RAW_PAYLOAD");
        }
        return new ExternalMessageEnvelope(protocolHint, PROVIDER_ID + "-" + sha256(rawPayload), Instant.now());
    }

    @Override
    public NormalizedClinicalMessage normalizeMessage(ExternalMessageEnvelope envelope, String rawPayload) {
        if (rawPayload == null || rawPayload.toUpperCase(java.util.Locale.ROOT).contains("INVALID")) {
            throw new IntegrationAdapterException(
                    "Local passthrough adapter could not normalize the payload.", "INTEGRATION_NORMALIZATION_FAILED");
        }
        Map<String, String> canonicalFields = extractFields(rawPayload);
        String messageType = (envelope.sourceProtocol() == null ? "generic" : envelope.sourceProtocol()) + ".message";
        return new NormalizedClinicalMessage(messageType, canonicalFields, "unrouted");
    }

    @Override
    public IntegrationAcknowledgement acknowledgeMessage(String externalMessageId, String status) {
        String canonicalErrorCode = IntegrationAcknowledgement.STATUS_REJECTED.equals(status)
                ? "INTEGRATION_NORMALIZATION_FAILED"
                : null;
        return new IntegrationAcknowledgement(externalMessageId, status, canonicalErrorCode);
    }

    private static Map<String, String> extractFields(String rawPayload) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (String pair : rawPayload.split(";")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && !keyValue[0].isBlank()) {
                fields.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        if (fields.isEmpty()) {
            fields.put("payloadLength", String.valueOf(rawPayload.length()));
        }
        return fields;
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8))).substring(0, 32);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 must be available on the JVM.", exception);
        }
    }
}
