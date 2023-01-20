package com.coditory.sherlock;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

interface SqlConnection extends AutoCloseable {
    PreparedStatement prepareStatement(String sql) throws SQLException;

    Statement createStatement() throws SQLException;

    @Override
    void close() throws SQLException;
}
