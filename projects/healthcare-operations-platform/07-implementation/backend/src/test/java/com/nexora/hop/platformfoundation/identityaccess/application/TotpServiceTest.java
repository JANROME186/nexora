package com.nexora.hop.platformfoundation.identityaccess.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class TotpServiceTest {

    private static final char[] BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();

    private final TotpService service = new TotpService();

    @Test
    void generatesABase32SecretOfPlausibleLength() {
        String secret = service.generateSecret();

        assertThat(secret).isNotBlank();
        assertThat(secret).matches("[A-Z2-7]+");
        assertThat(secret.length()).isGreaterThanOrEqualTo(32);
    }

    @Test
    void generatesDifferentSecretsEachTime() {
        assertThat(service.generateSecret()).isNotEqualTo(service.generateSecret());
    }

    @Test
    void verifiesACodeComputedIndependentlyPerRfc6238ForTheCurrentTimeStep() {
        String secret = service.generateSecret();
        String code = rfc6238Code(secret, Instant.now().getEpochSecond() / 30);

        assertThat(service.verifyCode(secret, code)).isTrue();
    }

    @Test
    void rejectsAnIncorrectCode() {
        String secret = service.generateSecret();
        String correctCode = rfc6238Code(secret, Instant.now().getEpochSecond() / 30);
        String wrongCode = "000000".equals(correctCode) ? "111111" : "000000";

        assertThat(service.verifyCode(secret, wrongCode)).isFalse();
    }

    @Test
    void rejectsACodeGeneratedForADifferentSecret() {
        String secret = service.generateSecret();
        String otherSecret = service.generateSecret();
        String codeForOtherSecret = rfc6238Code(otherSecret, Instant.now().getEpochSecond() / 30);

        assertThat(service.verifyCode(secret, codeForOtherSecret)).isFalse();
    }

    @Test
    void rejectsACodeOutsideTheAllowedTimeStepDrift() {
        String secret = service.generateSecret();
        long farFutureStep = Instant.now().getEpochSecond() / 30 + 10;
        String farFutureCode = rfc6238Code(secret, farFutureStep);

        assertThat(service.verifyCode(secret, farFutureCode)).isFalse();
    }

    @Test
    void handlesBlankAndNullInputsSafely() {
        assertThat(service.verifyCode(null, "123456")).isFalse();
        assertThat(service.verifyCode("", "123456")).isFalse();
        assertThat(service.verifyCode("SECRET", null)).isFalse();
        assertThat(service.verifyCode("SECRET", "")).isFalse();
    }

    /**
     * Independent RFC 6238 reference implementation used only to derive an expected code for
     * assertions, so this test does not depend on {@link TotpService}'s internals.
     */
    private static String rfc6238Code(String base32Secret, long timeStep) {
        try {
            byte[] key = base32Decode(base32Secret);
            byte[] stepBytes = new byte[8];
            long value = timeStep;
            for (int i = 7; i >= 0; i--) {
                stepBytes[i] = (byte) (value & 0xff);
                value >>= 8;
            }
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(stepBytes);
            int offset = hash[hash.length - 1] & 0x0f;
            int binary =
                    ((hash[offset] & 0x7f) << 24)
                            | ((hash[offset + 1] & 0xff) << 16)
                            | ((hash[offset + 2] & 0xff) << 8)
                            | (hash[offset + 3] & 0xff);
            int otp = binary % 1_000_000;
            return String.format(Locale.ROOT, "%06d", otp);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] base32Decode(String base32) {
        String cleaned = base32.trim().toUpperCase(Locale.ROOT).replace("=", "");
        int buffer = 0;
        int bitsLeft = 0;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        for (char c : cleaned.toCharArray()) {
            int value = new String(BASE32_ALPHABET).indexOf(c);
            if (value < 0) {
                continue;
            }
            buffer = (buffer << 5) | value;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out.write((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }
        return out.toByteArray();
    }
}
