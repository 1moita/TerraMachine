package net.terra.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ChatColor {

    public static String colorize(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> colorize(List<String> text) {
        return text.stream().map(ChatColor::colorize).collect(Collectors.toList());
    }

    public static List<String> colorize(String... text) {
        return colorize(Arrays.asList(text));
    }

}
