package com.nexora.hop.platformfoundation;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class PlatformFoundationLocalDatabaseTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void localPostgresConnectionIsAvailableAndSchemasExist() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Integer schemaCount = jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.schemata
                where schema_name in ('organization', 'identity', 'audit')
                """, Integer.class);

        assertThat(schemaCount).isEqualTo(3);
    }
}
