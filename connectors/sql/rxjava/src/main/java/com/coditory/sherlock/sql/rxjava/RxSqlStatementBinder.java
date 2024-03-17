package com.coditory.sherlock.sql.rxjava;

import com.coditory.sherlock.sql.BindingMapper;
import io.r2dbc.spi.Statement;
import io.reactivex.Single;

import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.sql.SqlLockNamedQueriesTemplate.ParameterNames.*;

final class RxSqlStatementBinder {
    private final Statement statement;
    private final BindingMapper bindingMapper;
    private int index = 0;

    RxSqlStatementBinder(Statement statement, BindingMapper bindingMapper) {
        expectNonNull(statement, "statement");
        expectNonNull(bindingMapper, "bindingMapper");
        this.statement = statement;
        this.bindingMapper = bindingMapper;
    }

    RxSqlStatementBinder bindLockId(String value) {
        return bind(LOCK_ID, value, String.class);
    }

    RxSqlStatementBinder bindOwnerId(String value) {
        return bind(OWNER_ID, value, String.class);
    }

    RxSqlStatementBinder bindNow(Instant value) {
        return bind(NOW, value, Instant.class);
    }

    RxSqlStatementBinder bindExpiresAt(Instant value) {
        return bind(EXPIRES_AT, value, Instant.class);
    }

    private RxSqlStatementBinder bind(String name, Object value, Class<?> type) {
        Object key = bindingMapper.mapBinding(index, name).getBindingKey();
        index++;
        if (key instanceof Integer) {
            int intKey = (Integer) key;
            if (value != null) {
                statement.bind(intKey, value);
            } else {
                statement.bindNull(intKey, type);
            }
        } else {
            String stringKey = key.toString();
            if (value != null) {
                statement.bind(stringKey, value);
            } else {
                statement.bindNull(stringKey, type);
            }
        }
        return this;
    }

    Single<Long> executeAndGetUpdated() {
        return Single.fromPublisher(statement.execute())
                .flatMap(r -> Single.fromPublisher(r.getRowsUpdated()));
    }
}
