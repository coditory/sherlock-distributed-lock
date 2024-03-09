package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

final class SqlLockQueries {
    private final String createLocksTableSql;
    private final String createLocksIndexSql;
    private final String checkTableExitsSql;
    private final String deleteAllSql;
    private final String deleteAcquiredByIdAndOwnerIdSql;
    private final String deleteAcquiredByIdSql;
    private final String updateLockByIdSql;
    private final String updateAcquiredOrReleasedLockSql;
    private final String updateReleasedLockSql;
    private final String insertLockSql;

    SqlLockQueries(@NotNull String tableName) {
        this(tableName, BindingMapper.JDBC_MAPPER);
    }

    SqlLockQueries(@NotNull String tableName, @NotNull BindingMapper bindingMapper) {
        expectNonEmpty(tableName, "tableName");
        expectNonNull(bindingMapper, "bindingMapper");
        SqlLockNamedQueriesTemplate queriesTemplate = new SqlLockNamedQueriesTemplate(tableName, bindingMapper);
        createLocksTableSql = queriesTemplate.createLocksTable();
        createLocksIndexSql = queriesTemplate.createLocksIndex();
        checkTableExitsSql = queriesTemplate.checkTableExits();
        deleteAllSql = queriesTemplate.deleteAll();
        deleteAcquiredByIdAndOwnerIdSql = queriesTemplate.deleteAcquiredByIdAndOwnerId();
        deleteAcquiredByIdSql = queriesTemplate.deleteAcquiredById();
        updateLockByIdSql = queriesTemplate.updateLockById();
        updateAcquiredOrReleasedLockSql = queriesTemplate.updateAcquiredOrReleasedLock();
        updateReleasedLockSql = queriesTemplate.updateReleasedLock();
        insertLockSql = queriesTemplate.insertLock();
    }

    @NotNull
    String createLocksTable() {
        return createLocksTableSql;
    }

    @NotNull
    String createLocksIndex() {
        return createLocksIndexSql;
    }

    @NotNull
    String checkTableExits() {
        return checkTableExitsSql;
    }

    @NotNull
    String deleteAll() {
        return deleteAllSql;
    }

    @NotNull
    String deleteAcquiredByIdAndOwnerId() {
        return deleteAcquiredByIdAndOwnerIdSql;
    }

    @NotNull
    String deleteAcquiredById() {
        return deleteAcquiredByIdSql;
    }

    @NotNull
    String updateLockById() {
        return updateLockByIdSql;
    }

    @NotNull
    String updateAcquiredOrReleasedLock() {
        return updateAcquiredOrReleasedLockSql;
    }

    @NotNull
    String updateReleasedLock() {
        return updateReleasedLockSql;
    }

    @NotNull
    String insertLock() {
        return insertLockSql;
    }
}
