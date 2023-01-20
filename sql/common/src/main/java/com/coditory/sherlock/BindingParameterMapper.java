package com.coditory.sherlock;

// TODO: Rename to BindingType?
public interface BindingParameterMapper {
    BindingParameterMapper ORDERED_QUESTION_MARK = (param) -> new BindingParameterMapping("?", param.getIndex());
    BindingParameterMapper INDEXED_QUESTION_MARK = (param) -> {
        int oneBasedIndex = param.getIndex() + 1;
        String value = "$" + oneBasedIndex;
        return new BindingParameterMapping(value, value);
    };
    BindingParameterMapper AT_NAME_MARK = (param) -> {
        String queryKey = "@" + param.getName();
        return new BindingParameterMapping(queryKey, param.getName());
    };
    BindingParameterMapper MYSQL_MAPPER = ORDERED_QUESTION_MARK;
    BindingParameterMapper POSTGRES_MAPPER = INDEXED_QUESTION_MARK;
    BindingParameterMapper H2_MAPPER = INDEXED_QUESTION_MARK;
    BindingParameterMapper MSSQL_MAPPER = AT_NAME_MARK;
    BindingParameterMapper SPANNER_MAPPER = AT_NAME_MARK;
    BindingParameterMapper JDBC_MAPPER = ORDERED_QUESTION_MARK;

    default BindingParameterMapping mapBinding(int index, String name) {
        return mapBinding(new BindingParameter(index, name));
    }

    BindingParameterMapping mapBinding(BindingParameter bindingParameter);
}
