package org.fastmcmirror.flybuff.task;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.fastmcmirror.flybuff.FlyBuff;
import org.fastmcmirror.flybuff.api.BuffTickHandler;
import org.fastmcmirror.flybuff.api.FlyBuffAPI;
import org.fastmcmirror.flybuff.api.ItemSlot;
import org.fastmcmirror.flybuff.module.ModuleLoader;

public class BuffTickTask extends BukkitRunnable {
    private static void handleBuffs(Player player, ItemStack item, ItemSlot slot) {
        if (item == null || item.getType().equals(Material.AIR) || item.getAmount()<1) return;
        for (String buff : FlyBuffAPI.getAllInstalledBuff(item)) {
            for (String line : FlyBuff.instance.getConfig().getStringList("buffs." + buff + ".tick")) {
                for (String module : ModuleLoader.buffTickHandlers.keySet()) {
                    if (line.startsWith("[" + module + "] ")) {
                        String param = line.substring(("[" + module + "] ").length());
                        BuffTickHandler handler = ModuleLoader.buffTickHandlers.get(module);
                        handler.handle(player, item, buff, slot, param);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inventory = player.getInventory();
            handleBuffs(player, inventory.getItemInMainHand(), ItemSlot.MAIN_HAND);
            handleBuffs(player, inventory.getItemInOffHand(), ItemSlot.OFF_HAND);
            handleBuffs(player, inventory.getHelmet(), ItemSlot.HELMET);
            handleBuffs(player, inventory.getChestplate(), ItemSlot.CHESTPLATE);
            handleBuffs(player, inventory.getLeggings(), ItemSlot.LEGGINGS);
            handleBuffs(player, inventory.getBoots(), ItemSlot.BOOTS);
        }
    }
}
