package com.nexora.hop.platformfoundation.cashsales;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class CashSalesLocalDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void cashSalesSchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_schema = 'cash_sales'
                   and table_name in (
                       'cash_sessions',
                       'sales',
                       'sale_lines',
                       'payment_allocations',
                       'invoice_requests',
                       'invoice_tax_lines')
                """, Integer.class);

        assertThat(tableCount).isEqualTo(6);
    }
}
