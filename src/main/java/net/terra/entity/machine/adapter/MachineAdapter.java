package net.terra.entity.machine.adapter;

import net.terra.entity.machine.Machine;
import net.terra.provider.database.adapter.ResultAdapter;

import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static net.terra.util.Util.deserializeLocation;

public final class MachineAdapter extends ResultAdapter<Machine> {

    @Override
    public Machine adaptResult(ResultSet result) throws SQLException {
        UUID id = UUID.fromString(result.getString("id"));
        UUID ownerId = UUID.fromString(result.getString("owner_id"));
        Location placedAt = deserializeLocation(result.getString("placed_at"));
        int level = result.getInt("level");

        return new Machine(id, ownerId, placedAt, level);
    }

}
