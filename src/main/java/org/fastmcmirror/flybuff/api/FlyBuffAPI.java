package org.fastmcmirror.flybuff.api;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.fastmcmirror.flybuff.FlyBuff;

import java.util.ArrayList;
import java.util.List;

public class FlyBuffAPI {
    public static boolean hasInstalledBuff(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasTag("FlyBuff");
    }

    public static List<String> getAllInstalledBuff(ItemStack item) {
        List<String> list = new ArrayList<>();
        if (hasInstalledBuff(item)) {
            NBTItem nbtItem = new NBTItem(item);
            list.addAll(nbtItem.getStringList("FlyBuff").toListCopy());
        }
        return list;
    }

    public static ItemStack removeBuff(ItemStack item, String key) {
        if (hasInstalledBuff(item)) {
            NBTItem nbtItem = new NBTItem(item);
            List<String> list = nbtItem.getStringList("FlyBuff");
            list.remove(key);
            return nbtItem.getItem();
        }
        return item;
    }

    public static ItemStack addBuff(ItemStack item, String key) {
        NBTItem nbtItem = new NBTItem(item);
        List<String> list = nbtItem.getStringList("FlyBuff");
        list.add(key);
        return nbtItem.getItem();
    }

    public static ItemStack getGem(String buff) {
        NBTContainer container = new NBTContainer(FlyBuff.instance.getConfiguration().getString("buffs." + buff + ".gem"));
        NBTItem item = new NBTItem(NBTItem.convertNBTtoItem(container));
        item.setString("FlyBuffGem", buff);
        return item.getItem();
    }

    public static String getGemBuff(ItemStack gem) {
        NBTItem item = new NBTItem(gem);
        if (item.hasTag("FlyBuffGem")) {
            return item.getString("FlyBuffGem");
        }
        return null;
    }
}
