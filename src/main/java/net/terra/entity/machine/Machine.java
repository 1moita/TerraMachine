package net.terra.entity.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Location;

import java.util.UUID;

import static net.terra.util.Util.serializeLocation;

@Getter @Setter
@AllArgsConstructor
public final class Machine {

    private final UUID id;
    private final UUID ownerId;

    private Location placedAt;

    private int level;

    public String toString() {
        return String.format("machine[id=%s,owner_id=%s,placed_at=%s,level=%d]", id.toString(), ownerId.toString(), serializeLocation(placedAt), level);
    }

}
