package com.nexora.hop.platformfoundation.sharedkernel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Encodes small, simple structured values (string lists, string-to-string maps, string-to-integer
 * count maps) as delimited plain text for storage in a single {@code text} column, avoiding a new
 * JSON-parsing dependency footprint in the persistence layer. Consistent with the codebase's
 * existing preference for hand-built text over a serialization library in adapters (e.g.
 * {@code BillingRequestManagementService}'s hand-interpolated audit metadata JSON). Not intended
 * for values that may themselves contain the delimiter characters.
 */
public final class DelimitedTextCodec {

    /** ASCII record separator (0x1E): delimits list/map entries. */
    private static final String ENTRY_DELIMITER = "\u001E";
    /** ASCII unit separator (0x1F): delimits a map entry's key from its value. */
    private static final String KEY_VALUE_DELIMITER = "\u001F";

    private DelimitedTextCodec() {
    }

    public static String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return String.join(ENTRY_DELIMITER, values);
    }

    public static List<String> splitList(String stored) {
        if (stored == null || stored.isEmpty()) {
            return List.of();
        }
        return List.of(stored.split(ENTRY_DELIMITER, -1));
    }

    public static String joinStringMap(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        map.forEach((key, value) -> {
            if (!builder.isEmpty()) {
                builder.append(ENTRY_DELIMITER);
            }
            builder.append(key).append(KEY_VALUE_DELIMITER).append(value == null ? "" : value);
        });
        return builder.toString();
    }

    public static Map<String, String> splitStringMap(String stored) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String entry : splitList(stored)) {
            int separatorIndex = entry.indexOf(KEY_VALUE_DELIMITER);
            if (separatorIndex >= 0) {
                result.put(entry.substring(0, separatorIndex), entry.substring(separatorIndex + 1));
            }
        }
        return result;
    }

    public static String joinIntMap(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        Map<String, String> asStrings = new LinkedHashMap<>();
        map.forEach((key, value) -> asStrings.put(key, String.valueOf(value)));
        return joinStringMap(asStrings);
    }

    public static Map<String, Integer> splitIntMap(String stored) {
        Map<String, Integer> result = new LinkedHashMap<>();
        splitStringMap(stored).forEach((key, value) -> result.put(key, parseIntOrZero(value)));
        return result;
    }

    private static int parseIntOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
