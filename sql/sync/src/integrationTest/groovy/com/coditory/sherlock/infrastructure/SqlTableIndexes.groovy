package com.coditory.sherlock.infrastructure

import groovy.transform.CompileStatic

import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet

@CompileStatic
class SqlTableIndexes {
    static List<String> listTableIndexes(Connection connection, String tableName) {
        Set<String> indexes = new HashSet<>()
        DatabaseMetaData metaData = connection.getMetaData()
        List<String> schemaList = getSchemaList(metaData)
        for (int i = 0; i < schemaList.size(); i++) {
            ResultSet indexValues
            try {
                indexValues = metaData.getIndexInfo(null, schemaList.get(i), tableName, false, false)
                while (indexValues.next()) {
                    String dbIndexName = indexValues.getString("INDEX_NAME")
                    if (dbIndexName != null) {
                        indexes.add(dbIndexName)
                    }
                }
            } finally {
                if (indexValues != null) indexValues.close()
            }
        }
        return indexes.toList().sort()
    }

    static private List<String> getSchemaList(DatabaseMetaData metaData) {
        List<String> schemaList = new ArrayList<>()
        ResultSet rs
        try {
            rs = metaData.getSchemas()
            while (rs.next()) {
                String tableSchema = rs.getString(1)
                if (tableSchema != null) {
                    schemaList.add(tableSchema)
                }
            }
            if (schemaList.isEmpty()) {
                schemaList.add(null)
            }
        } finally {
            if (rs) rs.close()
        }
        return schemaList
    }
}
