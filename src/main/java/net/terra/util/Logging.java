package net.terra.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import net.terra.Plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Logging {

    @RequiredArgsConstructor
    public enum Type {
        DEFAULT(ChatColor.WHITE),
        INFO(ChatColor.YELLOW),
        WARNING(ChatColor.DARK_AQUA),
        SEVERE(ChatColor.RED);

        public final ChatColor color;
    }

    @Getter @Setter
    private static String prefix = Plugin.getInstance().getName();

    private static String format(String prefix, String message) {
        return String.format("[%s] %s", prefix, message);
    }

    public static Exception except(Type type, String message) {
        return new Exception(type.color + format(prefix, message));
    }

    public static Exception except(Type type, String suffix, String message) {
        return new Exception(type.color + format(prefix, "[" + suffix + "] " + message));
    }

    public static void log(Type type, String message) {
        Bukkit.getConsoleSender().sendMessage(type.color + format(prefix, message));
    }

    public static void log(Type type, String suffix, String message) {
        Bukkit.getConsoleSender().sendMessage(type.color + format(prefix, "[" + suffix + "] " + message));
    }

    public static void log(String message) {
        log(Type.DEFAULT, message);
    }

    public static void log(String prefix, String message) {
        log(Type.DEFAULT, prefix, message);
    }

    public static void info(String message) {
        log(Type.INFO, message);
    }

    public static void info(String prefix, String message) {
        log(Type.INFO, prefix, message);
    }

    public static void warning(String message) {
        log(Type.WARNING, message);
    }

    public static void warning(String prefix, String message) {
        log(Type.WARNING, prefix, message);
    }

    public static void severe(String message) {
        log(Type.SEVERE, message);
    }

    public static void severe(String prefix, String message) {
        log(Type.SEVERE, prefix, message);
    }

}
