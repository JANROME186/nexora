package com.nexora.hop.platformfoundation;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TD-DB-004: wraps the {@code local} profile's real Postgres {@link DataSource} with {@link
 * TenantSessionDataSource} so native row-level-security policies (see {@code
 * db/final-hardening/schema.sql}) receive the authenticated request's tenant id on every borrowed
 * connection. Implemented as a {@link BeanPostProcessor} (rather than a second {@code @Primary
 * @Bean DataSource} method that takes the autoconfigured one as a constructor parameter) because
 * the latter creates a circular dependency: Spring Boot's own {@code
 * DataSourceScriptDatabaseInitializer} looks up "the" {@code DataSource} bean by type, and once a
 * second, {@code @Primary}-marked {@code DataSource} bean exists, by-type parameter resolution for
 * that second bean's own factory method can end up resolving back to itself. Post-processing the
 * already-created singleton instead avoids that entirely, and works regardless of the
 * autoconfigured bean's name. {@code @Profile("local")} mirrors every {@code JdbcXxxRepository}/
 * {@code InMemoryXxxRepository} pair in this codebase: only the {@code local} profile has a real
 * database to protect.
 */
@Configuration
class TenantSessionDataSourceConfig {

    @Component
    @Profile("local")
    static class TenantSessionDataSourceBeanPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof DataSource dataSource && !(bean instanceof TenantSessionDataSource)) {
                return new TenantSessionDataSource(dataSource);
            }
            return bean;
        }
    }
}
