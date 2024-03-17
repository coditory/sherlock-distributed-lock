package com.coditory.sherlock.sql.reactor;

import com.coditory.sherlock.sql.BindingMapper;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.sql.SqlLockNamedQueriesTemplate.ParameterNames.*;

final class SqlStatementBinder {
    private final Statement statement;
    private final BindingMapper bindingMapper;
    private int index = 0;

    SqlStatementBinder(Statement statement, BindingMapper bindingMapper) {
        expectNonNull(statement, "statement");
        expectNonNull(bindingMapper, "bindingMapper");
        this.statement = statement;
        this.bindingMapper = bindingMapper;
    }

    SqlStatementBinder bindLockId(String value) {
        return bind(LOCK_ID, value, String.class);
    }

    SqlStatementBinder bindOwnerId(String value) {
        return bind(OWNER_ID, value, String.class);
    }

    SqlStatementBinder bindNow(Instant value) {
        return bind(NOW, value, Instant.class);
    }

    SqlStatementBinder bindExpiresAt(Instant value) {
        return bind(EXPIRES_AT, value, Instant.class);
    }

    private SqlStatementBinder bind(String name, Object value, Class<?> type) {
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

    Mono<Long> executeAndGetUpdated() {
        return Mono.from(statement.execute())
                .flatMap(r -> Mono.from(r.getRowsUpdated()));
    }
}
