package org.fastmcmirror.flybuff.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ItemUtil {
    public static ItemStack buildItem(Material type, String display) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.color(display));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack buildItem(Material type, String display, String... lores) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.color(display));
        meta.setLore(Arrays.stream(lores).map(Color::color).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack buildItem(Material type, int data, String display, String... lores) {
        ItemStack item = new ItemStack(type, 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.color(display));
        meta.setLore(Arrays.stream(lores).map(Color::color).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return item;
    }
}
