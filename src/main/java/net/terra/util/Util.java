package net.terra.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class Util {

    public static boolean isBlockEqual(Location location, Location other) {
        return location.getBlockX() == other.getBlockX()
                && location.getBlockY() == other.getBlockY()
                && location.getBlockZ() == other.getBlockZ()
                && isWorldEqual(location, other);
    }

    public static boolean isLocationEqual(Location location, Location other) {
        return location.getX() == other.getX()
                && location.getY() == other.getY()
                && location.getZ() == other.getZ()
                && isWorldEqual(location, other);
    }

    public static boolean isWorldEqual(Location location, Location other) {
        return location.getWorld().getName().equals(other.getWorld().getName());
    }

    public static String serializeLocation(Location location) {
        if(location == null) return null;
        return String.format("%d:%d:%d:%s", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
    }

    public static Location deserializeLocation(String location) {
        if(location == null || location.isEmpty()) return null;

        String[] parts = location.split(":");
        if(parts.length != 4) return null;

        double x = Integer.parseInt(parts[0]);
        double y = Integer.parseInt(parts[1]);
        double z = Integer.parseInt(parts[2]);
        World world = Bukkit.getWorld(parts[3]);

        return new Location(world, x, y, z);
    }

}
