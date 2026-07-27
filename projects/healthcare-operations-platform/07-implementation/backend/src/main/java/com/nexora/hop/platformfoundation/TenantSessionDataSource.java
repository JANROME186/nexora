package com.nexora.hop.platformfoundation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

import com.nexora.hop.platformfoundation.sharedkernel.security.CurrentTenantContext;

/**
 * TD-DB-004: stamps every physical connection borrowed from the pool with the requesting
 * request's tenant id (and platform-bypass flag) as Postgres session GUCs, so the native
 * row-level-security policy created by {@code db/final-hardening/schema.sql} can enforce tenant
 * isolation at the database layer regardless of which {@code JdbcXxxRepository} query borrowed
 * the connection. This is genuine cross-cutting platform infrastructure (every JDBC-backed module
 * shares one pooled {@link DataSource}), not a single capability concern, so it lives at the
 * application root package alongside {@link SecurityHeadersFilter}.
 * <p>
 * Values come from {@link CurrentTenantContext}, a request-scoped {@code ThreadLocal} set by
 * {@code HopAuthorizationInterceptor} before any repository code runs and cleared in {@code
 * afterCompletion}.
 * <p>
 * Setting the GUCs alone is not enough: the local docker-compose Postgres bootstrap user ({@code
 * hop}) is a superuser and owns every protected table, and superusers always bypass row-level
 * security. Postgres also refuses to ever strip {@code SUPERUSER} from the bootstrap role, so
 * within an authenticated request this class additionally {@code SET ROLE hop_app} -- a plain,
 * unprivileged, non-owner role created by {@code db/final-hardening/schema.sql} -- which drops the
 * connection's effective privileges (including RLS bypass) for the remainder of the session.
 * {@code hop} may switch to any role via {@code SET ROLE} without an explicit grant because
 * superusers are implicitly members of every role. Outside a request (background jobs, {@code
 * schema.sql} bootstrapping, repository tests that call {@code JdbcTemplate} directly) the
 * connection is explicitly reset back to {@code hop} so schema DDL keeps working.
 */
final class TenantSessionDataSource extends DelegatingDataSource {

    TenantSessionDataSource(DataSource delegate) {
        super(delegate);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        applyTenantContext(connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        applyTenantContext(connection);
        return connection;
    }

    private static void applyTenantContext(Connection connection) throws SQLException {
        if (CurrentTenantContext.current().isEmpty()) {
            resetRole(connection);
            return;
        }
        String tenantId = CurrentTenantContext.current().orElseThrow();
        boolean bypass = CurrentTenantContext.isPlatformBypass();
        setConfig(connection, "app.current_tenant_id", tenantId);
        setConfig(connection, "app.rls_bypass", bypass ? "true" : "false");
        setRole(connection, "hop_app");
    }

    private static void setConfig(Connection connection, String setting, String value) throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement("select set_config(?, ?, false)")) {
            statement.setString(1, setting);
            statement.setString(2, value);
            statement.execute();
        }
    }

    private static void setRole(Connection connection, String role) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET ROLE " + role);
        }
    }

    private static void resetRole(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("RESET ROLE");
        }
    }
}
