package net.terra.entity.machine;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Getter;

import net.terra.Plugin;
import net.terra.entity.machine.adapter.MachineAdapter;

import org.bukkit.Location;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.terra.util.Util.*;

public final class MachineController {

    @Getter(AccessLevel.PRIVATE)
    private static final List<Machine> machine = Lists.newArrayList();

    @Getter(AccessLevel.PRIVATE)
    private static final Plugin plugin = Plugin.getInstance();

    static {
        plugin.getPool().executeStatement("CREATE TABLE IF NOT EXISTS terramachine (" +
                "id VARCHAR(36) NOT NULL PRIMARY KEY," +
                "owner_id VARCHAR(36) NOT NULL," +
                "placed_at VARCHAR," +
                "level TINYINT NOT NULL DEFAULT 0)"
        );
    }

    public static Machine getMachineById(UUID id) {
        return plugin.getPool().executeQuery("SELECT * FROM terramachine WHERE id = '" + id.toString() + "'", MachineAdapter.class);
    }

    public static Machine getMachinePlacedAt(Location location) {
        return plugin.getPool().executeQuery("SELECT * FROM terramachine WHERE placed_at = '" + serializeLocation(location) + "'", MachineAdapter.class);
    }

    public static boolean updateMachine(Machine machine) {
        return plugin.getPool().executeStatement("UPDATE terramachine SET placed_at = ?, level = ? WHERE id = ?", (driver) -> {
            try {
                if(machine.getPlacedAt() == null) {
                    driver.setNull(1, Types.VARCHAR);
                } else {
                    driver.setString(1, serializeLocation(machine.getPlacedAt()));
                }

                driver.setInt(2, machine.getLevel());
                driver.setString(3, machine.getId().toString());
            } catch (SQLException ignored) {}
        });
    }

    public static boolean createMachine(Machine machine) {
        return plugin.getPool().executeStatement("INSERT INTO terramachine (id, owner_id, placed_at, level) VALUES (?, ?, ?, ?)", (driver) -> {
            try {
                driver.setString(1, machine.getId().toString());
                driver.setString(2, machine.getOwnerId().toString());
                driver.setString(3, serializeLocation(machine.getPlacedAt()));
                driver.setInt(4, machine.getLevel());
            } catch(SQLException ignored) {}
        });
    }

    private static Optional<Machine> predicateMachine(Predicate<Machine> filter) {
        return getMachine().stream()
                .findFirst()
                .filter(filter);
    }

    private static List<Machine> collectMachine(Predicate<Machine> filter) {
        return getMachine().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

}
