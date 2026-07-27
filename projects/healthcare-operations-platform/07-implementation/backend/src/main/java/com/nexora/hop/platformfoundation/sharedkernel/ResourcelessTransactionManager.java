package com.nexora.hop.platformfoundation.sharedkernel;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * No-op {@link org.springframework.transaction.PlatformTransactionManager} used as the fallback
 * transaction manager for Spring profiles that have no real transactional resource (no JDBC
 * {@code DataSource}). It participates in Spring's transaction synchronization protocol so
 * {@code @Transactional} services behave consistently and fail closed under every active profile,
 * without managing any actual resource; a failure inside the transactional method still propagates
 * to the caller as usual. See technical debt TD-BE-006.
 */
public class ResourcelessTransactionManager extends AbstractPlatformTransactionManager {

    private static final long serialVersionUID = 1L;

    @Override
    protected Object doGetTransaction() {
        return new Object();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        // No resource to begin a transaction against.
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        // No resource to commit.
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        // No resource to roll back against; in-memory repositories are not transaction-aware, so
        // the caller still observes the failure through the propagated exception.
    }
}
