package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Row-level parsing for open data ingestion bundles (CUS-MIG-010-01), using the open-source
 * libraries the capability package names explicitly: Apache Commons CSV for {@code csv},
 * Jackson (already the platform's JSON library) for {@code json}/{@code ndjson}, Apache POI for
 * {@code xlsx} (closes TD-BE-013) and {@code java.util.zip} for {@code zip_bundle} extraction.
 */
@Component
public class ImportFileParser {

    /** Returned only for a declared format this parser has no row-level counter for at all. */
    public static final int ROWS_NOT_COUNTED = -1;

    private final ObjectMapper objectMapper;

    public ImportFileParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** Extracts every entry of a {@code zip_bundle} package into an in-memory name-to-bytes map. */
    public Map<String, byte[]> extractZipBundle(byte[] zipBytes) {
        Map<String, byte[]> files = new LinkedHashMap<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                zipInputStream.transferTo(buffer);
                files.put(entry.getName(), buffer.toByteArray());
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Package could not be read as a zip bundle.", exception);
        }
        return files;
    }

    /**
     * Counts data rows/records in a single entity file, or returns {@link #ROWS_NOT_COUNTED} for a
     * declared format this parser has no row-level counter for.
     */
    public int countRows(String declaredFormat, byte[] content) {
        String format = declaredFormat == null ? "" : declaredFormat.toLowerCase(java.util.Locale.ROOT);
        return switch (format) {
            case "csv" -> countCsvRows(content);
            case "json" -> countJsonRows(content);
            case "ndjson" -> countNdjsonRows(content);
            case "xlsx" -> countXlsxRows(content);
            default -> ROWS_NOT_COUNTED;
        };
    }

    private int countCsvRows(byte[] content) {
        try (CSVParser parser = CSVParser.parse(
                new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get())) {
            int count = 0;
            for (var ignored : parser) {
                count++;
            }
            return count;
        } catch (IOException exception) {
            throw new IllegalArgumentException("File could not be parsed as CSV.", exception);
        }
    }

    private int countJsonRows(byte[] content) {
        JsonNode node = objectMapper.readTree(content);
        if (node.isArray()) {
            return node.size();
        }
        return node.isMissingNode() || node.isNull() ? 0 : 1;
    }

    private int countNdjsonRows(byte[] content) {
        String text = new String(content, StandardCharsets.UTF_8);
        return (int) text.lines().filter(line -> !line.isBlank()).count();
    }

    /**
     * Counts data rows on the first worksheet, excluding the header row, mirroring
     * {@link #countCsvRows}'s header-skipping semantics (CUS-MIG-010-01, closes TD-BE-013).
     */
    private int countXlsxRows(byte[] content) {
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(content))) {
            if (workbook.getNumberOfSheets() == 0) {
                return 0;
            }
            Sheet sheet = workbook.getSheetAt(0);
            int count = 0;
            for (Row row : sheet) {
                if (row.getRowNum() == 0 || isBlankRow(row)) {
                    continue;
                }
                count++;
            }
            return count;
        } catch (IOException | RuntimeException exception) {
            throw new IllegalArgumentException("File could not be parsed as XLSX.", exception);
        }
    }

    private static boolean isBlankRow(Row row) {
        for (org.apache.poi.ss.usermodel.Cell cell : row) {
            if (cell.getCellType() != org.apache.poi.ss.usermodel.CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
