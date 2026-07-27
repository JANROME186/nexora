package com.nexora.hop.platformfoundation.sharedkernel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Guarantees a {@link PlatformTransactionManager} bean is always available so profile-agnostic
 * {@code @Transactional} orchestration (e.g. {@code PatientRegistrationService#commit}) works
 * under every active Spring profile, not only the {@code "local"} JDBC profile that already gets a
 * real {@code DataSourceTransactionManager} from Spring Boot autoconfiguration once a
 * {@code DataSource} bean is present.
 * <p>
 * Scoped to {@code "!local"} rather than guarded with {@code @ConditionalOnMissingBean}: this is a
 * regular (non-autoconfiguration) {@code @Configuration} class, so its {@code @Bean} methods are
 * registered before Spring Boot's deferred autoconfiguration classes run. A
 * {@code @ConditionalOnMissingBean} guard here would therefore win the race against the
 * {@code "local"} profile's real JDBC transaction manager and silently shadow it, defeating the
 * point of TD-BE-006 (real rollback under the JDBC profile). The explicit profile split mirrors
 * every JDBC/in-memory repository pair in this codebase (e.g. {@code JdbcDoctorRepository} /
 * {@code InMemoryDoctorRepository}).
 */
@Configuration
public class TransactionManagerConfig {

    @Bean
    @Profile("!local")
    public PlatformTransactionManager resourcelessTransactionManager() {
        return new ResourcelessTransactionManager();
    }
}
