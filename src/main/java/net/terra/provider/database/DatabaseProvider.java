package net.terra.provider.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import net.terra.Plugin;
import net.terra.provider.Provider;
import net.terra.provider.database.impl.MySQLDatabase;
import net.terra.provider.database.impl.SQLDatabase;
import net.terra.util.Logging;

import org.bukkit.Bukkit;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public final class DatabaseProvider implements Provider {

    public boolean initProvider(Plugin plugin) {
        if(plugin.getPool() != null) return false;

        long startedAt = new Date().getTime();
        Database pool = null;

        Logging.log("Disco", "Inicializando conexão com o banco de dados");

        try {
            DatabaseType type = DatabaseType.of(Optional
                    .ofNullable(plugin.getConfig().getString("database.provider"))
                    .orElseThrow(() -> new RuntimeException("O tipo do banco de dados não foi fornecido [database.type]")));

            switch(Objects.requireNonNull(type)) {
                case MYSQL:
                    String host = Optional
                            .ofNullable(plugin.getConfig().getString("database.connection.host"))
                            .orElseThrow(() -> new RuntimeException("O endereço do banco de dados não foi fornecido [database.provider.host]"));

                    String database = Optional
                            .ofNullable(plugin.getConfig().getString("database.connection.database"))
                            .orElseThrow(() -> new RuntimeException("O nome do banco de dados não foi fornecido [database.provider.database]"));

                    String user = Optional
                            .ofNullable(plugin.getConfig().getString("database.connection.user"))
                            .orElseThrow(() -> new RuntimeException("O usuário do banco de dados não foi fornecido [database.provider.user]"));

                    String password = Optional
                            .ofNullable(plugin.getConfig().getString("database.connection.password"))
                            .orElseThrow(() -> new RuntimeException("A senha do banco de dados não foi fornecida [database.provider.password]"));

                    Logging.info("Disco", "Tentando conexão com banco de dados 'MySQL'");
                    pool = MySQLDatabase.buildWith(host, database, user, password);
                    break;

                case SQL:
                    Logging.info("Disco", "Conexão 'SQL' fornecida");
                    pool = SQLDatabase.buildWith();
                    break;

                default:
                    throw new RuntimeException("O tipo de banco de dados fornecido é inexistente [opcões: mysql, sql]");
            }

            Logging.warning("Disco", "(" + (new Date().getTime() - startedAt) + "ms) Conexão com o banco de dados estabelecida");
        } catch(RuntimeException exception) {
            Logging.severe("Erro", exception.getMessage());
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        plugin.setPool(pool);
        return pool != null;
    }

}
