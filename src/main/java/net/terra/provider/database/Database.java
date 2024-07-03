package net.terra.provider.database;

import net.terra.provider.database.adapter.ResultAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Consumer;

public interface Database {

    boolean executeStatement(String query, Consumer<PreparedStatement> driver);
    boolean executeStatement(String query);

    <T> T executeQuery(String query, Class<? extends ResultAdapter<T>> adapter);

    Connection getConnection();

}
