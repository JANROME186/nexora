package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

class ImportFileParserTest {

    private final ImportFileParser parser = new ImportFileParser(JsonMapper.builder().build());

    @Test
    void countsCsvDataRowsExcludingTheHeader() {
        int rows = parser.countRows("csv", "id,name\n1,Alice\n2,Bob\n".getBytes(StandardCharsets.UTF_8));
        assertThat(rows).isEqualTo(2);
    }

    @Test
    void countsJsonArrayElements() {
        int rows = parser.countRows("json", "[{\"id\":1},{\"id\":2},{\"id\":3}]".getBytes(StandardCharsets.UTF_8));
        assertThat(rows).isEqualTo(3);
    }

    @Test
    void countsASingleJsonObjectAsOneRow() {
        int rows = parser.countRows("json", "{\"id\":1}".getBytes(StandardCharsets.UTF_8));
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void countsNonBlankNdjsonLines() {
        int rows = parser.countRows("ndjson", "{\"id\":1}\n{\"id\":2}\n\n".getBytes(StandardCharsets.UTF_8));
        assertThat(rows).isEqualTo(2);
    }

    @Test
    void countsXlsxDataRowsExcludingTheHeader() throws Exception {
        byte[] workbookBytes = xlsxWorkbook("id", "name", new String[] {"1", "Alice"}, new String[] {"2", "Bob"});
        assertThat(parser.countRows("xlsx", workbookBytes)).isEqualTo(2);
    }

    @Test
    void countsXlsxDataRowsSkippingTrailingBlankRows() throws Exception {
        byte[] workbookBytes = xlsxWorkbook("id", "name", new String[] {"1", "Alice"});
        assertThat(parser.countRows("XLSX", workbookBytes)).isEqualTo(1);
    }

    @Test
    void unknownFormatIsNotCounted() {
        assertThat(parser.countRows("unknown", new byte[0])).isEqualTo(ImportFileParser.ROWS_NOT_COUNTED);
    }

    private static byte[] xlsxWorkbook(String headerA, String headerB, String[]... dataRows) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("data");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue(headerA);
            header.createCell(1).setCellValue(headerB);
            int rowIndex = 1;
            for (String[] dataRow : dataRows) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(dataRow[0]);
                row.createCell(1).setCellValue(dataRow[1]);
            }
            // An extra fully-blank row (e.g. left by spreadsheet software) must not count as data.
            sheet.createRow(rowIndex);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            workbook.write(buffer);
            return buffer.toByteArray();
        }
    }

    @Test
    void extractsEveryEntryFromAZipBundle() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(buffer)) {
            zipOutputStream.putNextEntry(new ZipEntry("manifest.yaml"));
            zipOutputStream.write("source_system_name: Legacy".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
            zipOutputStream.putNextEntry(new ZipEntry("patients.csv"));
            zipOutputStream.write("id,name\n1,Alice\n".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        }

        var files = parser.extractZipBundle(buffer.toByteArray());

        assertThat(files).containsKeys("manifest.yaml", "patients.csv");
        assertThat(new String(files.get("patients.csv"), StandardCharsets.UTF_8)).contains("Alice");
    }
}
