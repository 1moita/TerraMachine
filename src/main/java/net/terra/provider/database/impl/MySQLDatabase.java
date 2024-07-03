package net.terra.provider.database.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.terra.Plugin;
import net.terra.provider.database.Database;
import net.terra.provider.database.adapter.ResultAdapter;
import net.terra.util.Logging;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor @Getter
public final class MySQLDatabase implements Database {

    private final String host;
    private final String database;
    private final String user;
    private final String password;

    public Connection getConnection() {
        String url = "jdbc:mysql//" + this.host + "/" + this.database;

        try {
            return DriverManager.getConnection(url, this.user, this.password);
        } catch(SQLException exception) {
            Logging.severe("Disco", "A conexão com o banco de dados 'MySQL' falhou");
            Bukkit.getPluginManager().disablePlugin(Plugin.getInstance());

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
        } catch(SQLException exception) {
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

    public static MySQLDatabase buildWith(String host, String database, String user, String password) {
        MySQLDatabase instance = new MySQLDatabase(host, database, user, password);
        if(instance.getConnection() == null)
            throw new RuntimeException("Não foi possível estabelecer uma conexão com o banco de dados");

        return instance;
    }

}
