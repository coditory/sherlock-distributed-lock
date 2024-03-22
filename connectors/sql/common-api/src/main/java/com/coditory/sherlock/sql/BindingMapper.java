package com.coditory.sherlock.sql;

import org.jetbrains.annotations.NotNull;

import static com.coditory.sherlock.Preconditions.expect;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public interface BindingMapper {
    BindingMapper ORDERED_QUESTION_MARK = (param) -> new BindingParameterMapping("?", param.getIndex());
    BindingMapper INDEXED_QUESTION_MARK = (param) -> {
        int oneBasedIndex = param.getIndex() + 1;
        String value = "$" + oneBasedIndex;
        return new BindingParameterMapping(value, value);
    };
    BindingMapper AT_NAME_MARK = (param) -> {
        String queryKey = "@" + param.getName();
        return new BindingParameterMapping(queryKey, param.getName());
    };
    BindingMapper MYSQL_MAPPER = ORDERED_QUESTION_MARK;
    BindingMapper POSTGRES_MAPPER = INDEXED_QUESTION_MARK;
    BindingMapper H2_MAPPER = INDEXED_QUESTION_MARK;
    BindingMapper MSSQL_MAPPER = AT_NAME_MARK;
    BindingMapper SPANNER_MAPPER = AT_NAME_MARK;
    BindingMapper JDBC_MAPPER = ORDERED_QUESTION_MARK;

    @NotNull
    default BindingParameterMapping mapBinding(int index, @NotNull String name) {
        expect(index >= 0, "Expected index >= 0. Got: %d", index);
        expectNonNull(name, "name");
        return mapBinding(new BindingParameter(index, name));
    }

    @NotNull
    BindingParameterMapping mapBinding(@NotNull BindingParameter bindingParameter);
}
