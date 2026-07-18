package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    void xlsxRowCountingIsNotYetSupported() {
        assertThat(parser.countRows("xlsx", new byte[0])).isEqualTo(ImportFileParser.ROWS_NOT_COUNTED);
    }

    @Test
    void unknownFormatIsNotCounted() {
        assertThat(parser.countRows("unknown", new byte[0])).isEqualTo(ImportFileParser.ROWS_NOT_COUNTED);
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
