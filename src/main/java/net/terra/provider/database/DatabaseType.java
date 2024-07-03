package net.terra.provider.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum DatabaseType {

    MYSQL("mysql"),
    SQL("sql");

    private String name;

    public static DatabaseType of(String name) {
        for(DatabaseType type : values())
            if(type.getName().equalsIgnoreCase(name))
                return type;
        return null;
    }

}
