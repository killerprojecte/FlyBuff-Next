package org.fastmcmirror.flybuff.utils;

import net.md_5.bungee.api.ChatColor;

public class Color {
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
