package com.nexora.hop.platformfoundation;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

/**
 * TD-DB-004: applies {@code db/final-hardening/schema.sql} directly through JDBC instead of via
 * {@code spring.sql.init.schema-locations}. That mechanism (Spring's {@code ScriptUtils}) splits a
 * script into individual statements by scanning for {@code ;}, and does not understand
 * PostgreSQL's {@code $$ ... $$} dollar-quoting used by {@code DO} blocks -- it mis-splits inside
 * the block's own internal statements and PostgreSQL then rejects the fragment with "unterminated
 * dollar quote". Sending the whole file as a single {@link Statement#execute} call instead avoids
 * client-side splitting entirely: PostgreSQL's own multi-statement simple-query support parses
 * {@code $$}-quoting correctly no matter how many {@code ;} appear inside it.
 * <p>
 * Runs as an {@link ApplicationRunner} (after {@code context.refresh()}, which is when
 * {@code spring.sql.init}'s {@code DataSourceScriptDatabaseInitializer} runs as an
 * {@code InitializingBean}), so every module's own tables already exist by the time this walks
 * {@code information_schema} for tenant_id columns.
 */
@Component
@Profile("local")
class FinalHardeningSchemaInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    FinalHardeningSchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String sql = StreamUtils.copyToString(
                new ClassPathResource("db/final-hardening/schema.sql").getInputStream(), StandardCharsets.UTF_8);
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
