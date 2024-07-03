package net.terra.provider.database.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.terra.Plugin;
import net.terra.provider.database.Database;
import net.terra.provider.database.adapter.ResultAdapter;
import net.terra.util.Logging;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.function.Consumer;

@RequiredArgsConstructor @Getter
public final class SQLDatabase implements Database {

    public Connection getConnection() {
        File folder = new File(Plugin.getInstance().getDataFolder(), "database");
        if(!folder.exists()) folder.mkdir();

        File file = new File(folder, "terra.db");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(IOException exception) {
                Logging.severe("Disco", "A conexão com o banco de dados 'SQL' falhou.");
                throw new RuntimeException(exception);
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch (SQLException | ClassNotFoundException exception) {
            Logging.severe("Disco", "A conexão com o banco de dados 'SQL' falhou");
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean executeStatement(String query, Consumer<PreparedStatement> driver) {
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            driver.accept(statement);

            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            Logging.severe("Disco", "Houve um problema com a conexão atual com o banco de dados");
            Logging.severe("Disco", exception.getMessage());

            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public <T> T executeQuery(String query, Class<? extends ResultAdapter<T>> adapter) {
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                ResultAdapter<T> clazz = adapter.newInstance();
                return clazz.adaptResult(result);
            } else {
                return null;
            }
        } catch(SQLException | InstantiationException | IllegalAccessException exception) {
            Logging.severe("Disco", "Houve um problema com a conexão atual com o banco de dados");
            Logging.severe("Disco", exception.getMessage());

            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean executeStatement(String query) {
        return executeStatement(query, (ignored) -> {});
    }

    public static SQLDatabase buildWith() {
        return new SQLDatabase();
    }

}
