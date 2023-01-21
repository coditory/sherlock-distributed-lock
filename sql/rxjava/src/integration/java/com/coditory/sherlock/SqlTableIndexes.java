package com.coditory.sherlock;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqlTableIndexes {
    public static List<String> listTableIndexes(Connection connection, String tableName) throws SQLException {
        Set<String> indexes = new HashSet<>();
        DatabaseMetaData metaData = connection.getMetaData();
        List<String> schemaList = getSchemaList(metaData);
        for (int i = 0; i < schemaList.size(); i++) {
            ResultSet indexValues = null;
            try {
                indexValues = metaData.getIndexInfo(null, schemaList.get(i), tableName, false, false);
                while (indexValues.next()) {
                    String dbIndexName = indexValues.getString("INDEX_NAME");
                    if (dbIndexName != null) {
                        indexes.add(dbIndexName);
                    }
                }
            } finally {
                if (indexValues != null) indexValues.close();
            }
        }
        List<String> result = new ArrayList<>(indexes);
        Collections.sort(result);
        return result;
    }

    static private List<String> getSchemaList(DatabaseMetaData metaData) throws SQLException {
        List<String> schemaList = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = metaData.getSchemas();
            while (rs.next()) {
                String tableSchema = rs.getString(1);
                if (tableSchema != null) {
                    schemaList.add(tableSchema);
                }
            }
            if (schemaList.isEmpty()) {
                schemaList.add(null);
            }
        } finally {
            if (rs != null) rs.close();
        }
        return schemaList;
    }
}
