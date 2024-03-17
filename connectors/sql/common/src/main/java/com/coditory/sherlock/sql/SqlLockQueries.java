package com.coditory.sherlock.sql;

import org.jetbrains.annotations.NotNull;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class SqlLockQueries {
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

    public SqlLockQueries(@NotNull String tableName) {
        this(tableName, BindingMapper.JDBC_MAPPER);
    }

    public SqlLockQueries(@NotNull String tableName, @NotNull BindingMapper bindingMapper) {
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
    public String createLocksTable() {
        return createLocksTableSql;
    }

    @NotNull
    public String createLocksIndex() {
        return createLocksIndexSql;
    }

    @NotNull
    public String checkTableExits() {
        return checkTableExitsSql;
    }

    @NotNull
    public String deleteAll() {
        return deleteAllSql;
    }

    @NotNull
    public String deleteAcquiredByIdAndOwnerId() {
        return deleteAcquiredByIdAndOwnerIdSql;
    }

    @NotNull
    public String deleteAcquiredById() {
        return deleteAcquiredByIdSql;
    }

    @NotNull
    public String updateLockById() {
        return updateLockByIdSql;
    }

    @NotNull
    public String updateAcquiredOrReleasedLock() {
        return updateAcquiredOrReleasedLockSql;
    }

    @NotNull
    public String updateReleasedLock() {
        return updateReleasedLockSql;
    }

    @NotNull
    public String insertLock() {
        return insertLockSql;
    }
}
