package com.coditory.sherlock;

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

    SqlLockQueries(String tableName) {
        this(tableName, BindingParameterMapper.JDBC_MAPPER);
    }

    SqlLockQueries(String tableName, BindingParameterMapper parameterMapper) {
        SqlLockNamedQueriesTemplate queriesTemplate = new SqlLockNamedQueriesTemplate(tableName, parameterMapper);
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

    String createLocksTable() {
        return createLocksTableSql;
    }

    String createLocksIndex() {
        return createLocksIndexSql;
    }

    String checkTableExits() {
        return checkTableExitsSql;
    }

    String deleteAll() {
        return deleteAllSql;
    }

    String deleteAcquiredByIdAndOwnerId() {
        return deleteAcquiredByIdAndOwnerIdSql;
    }

    String deleteAcquiredById() {
        return deleteAcquiredByIdSql;
    }

    String updateLockById() {
        return updateLockByIdSql;
    }

    String updateAcquiredOrReleasedLock() {
        return updateAcquiredOrReleasedLockSql;
    }

    String updateReleasedLock() {
        return updateReleasedLockSql;
    }

    String insertLock() {
        return insertLockSql;
    }
}
