package org.fastmcmirror.flybuff.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.fastmcmirror.flybuff.FlyBuff;
import org.fastmcmirror.flybuff.api.BuffCallback;
import org.fastmcmirror.flybuff.api.BuffInstallHandler;
import org.fastmcmirror.flybuff.api.FlyBuffAPI;
import org.fastmcmirror.flybuff.module.ModuleLoader;
import org.fastmcmirror.flybuff.utils.Color;

import java.util.stream.Collectors;

public class WorkbenchListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack gem = event.getCursor();
        Player player = (Player) event.getWhoClicked();
        if (event.getClick().equals(ClickType.LEFT)
                && event.getClickedInventory() != null &&
                event.getClickedInventory().getType()
                        .equals(InventoryType.valueOf(FlyBuff.instance.getConfiguration().getString("workspace")))
                && gem != null && !gem.getType().equals(Material.AIR) && event.getCurrentItem() != null
                && !event.getCurrentItem().getType().equals(Material.AIR)) {
            if (!FlyBuff.instance.getConfiguration().getStringList("whitelist").stream().map(String::toUpperCase).collect(Collectors.toList())
                    .contains(event.getCurrentItem().getType().toString())) {
                player.sendMessage(Color.color("&c&l此物品不在白名单内 无法镶嵌宝石"));
                return;
            }
            String id = FlyBuffAPI.getGemBuff(gem);
            if (id == null) return;
            ItemStack item = event.getCurrentItem();
            if (FlyBuffAPI.getAllInstalledBuff(item).contains(id)) {
                player.sendMessage(Color.color("&c&l此物品已镶嵌相同的宝石"));
                return;
            }
            for (String line : FlyBuff.instance.getConfiguration().getStringList("buffs." + id + ".install")) {
                for (String module : ModuleLoader.buffInstallHandlers.keySet()) {
                    if (line.startsWith("[" + module + "] ")) {
                        String param = line.substring(("[" + module + "] ").length());
                        BuffInstallHandler handler = ModuleLoader.buffInstallHandlers.get(module);
                        BuffCallback callback = handler.checkCondition(player, item,
                                gem, id, param);
                        if (!callback.isStatus()) {
                            if (callback.isHasReason()) {
                                player.sendMessage(Color.color(callback.getReason()));
                            }
                            player.sendMessage(Color.color("&c&l镶嵌失败!"));
                            player.closeInventory();
                            return;
                        }
                    }
                }
            }
            item = FlyBuffAPI.addBuff(item, id);
            gem.setAmount(gem.getAmount() - 1);
            for (String line : FlyBuff.instance.getConfiguration().getStringList("buffs." + id + ".install")) {
                for (String module : ModuleLoader.buffInstallHandlers.keySet()) {
                    if (line.startsWith("[" + module + "] ")) {
                        String param = line.substring(("[" + module + "] ").length());
                        BuffInstallHandler handler = ModuleLoader.buffInstallHandlers.get(module);
                        item = handler.handle(player, item, gem, id, param);
                    }
                }
            }
            event.setCurrentItem(item);
            player.sendMessage(Color.color("&a&l镶嵌成功!"));
        }
    }
}
