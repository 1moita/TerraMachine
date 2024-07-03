package net.terra.provider.database.adapter;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ResultAdapter<T> {

    public abstract T adaptResult(ResultSet result) throws SQLException;

}
