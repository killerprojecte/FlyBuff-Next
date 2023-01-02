package org.fastmcmirror.flybuff.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.fastmcmirror.flybuff.FlyBuff;
import org.fastmcmirror.flybuff.api.BuffCallback;
import org.fastmcmirror.flybuff.api.BuffRemoveHandler;
import org.fastmcmirror.flybuff.api.FlyBuffAPI;
import org.fastmcmirror.flybuff.module.ModuleLoader;
import org.fastmcmirror.flybuff.utils.Color;

public class RemoveGUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof RemoveGUI)) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getHolder() == null) return;
        if (!(event.getClickedInventory().getHolder() instanceof RemoveGUI)) return;
        RemoveGUI holder = (RemoveGUI) event.getInventory().getHolder();
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() >= 45) {
            if (event.getSlot() == 45) {
                holder.backPage();
            } else if (event.getSlot() == 49) {
                player.closeInventory();
            } else if (event.getSlot() == 53) {
                holder.nextPage();
            }
            return;
        }
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Color.color("&c&l背包已满无法拆解"));
            return;
        }
        String id = FlyBuffAPI.getGemBuff(event.getCurrentItem());
        for (String line : FlyBuff.instance.getConfiguration().getStringList("buffs." + id + ".remove")) {
            for (String module : ModuleLoader.buffRemoveHandlers.keySet()) {
                if (line.startsWith("[" + module + "] ")) {
                    String param = line.substring(("[" + module + "] ").length());
                    BuffRemoveHandler handler = ModuleLoader.buffRemoveHandlers.get(module);
                    BuffCallback callback = handler.checkCondition(player, player.getInventory().getItemInMainHand(),
                            event.getCurrentItem(), id, param);
                    if (!callback.isStatus()) {
                        if (callback.isHasReason()) {
                            player.sendMessage(Color.color(callback.getReason()));
                        }
                        player.sendMessage(Color.color("&c&l拆卸失败!"));
                        player.closeInventory();
                        return;
                    }
                }
            }
        }
        ItemStack item = FlyBuffAPI.removeBuff(player.getInventory().getItemInMainHand(), id);
        ItemStack gem = FlyBuffAPI.getGem(id);
        for (String line : FlyBuff.instance.getConfiguration().getStringList("buffs." + id + ".remove")) {
            for (String module : ModuleLoader.buffRemoveHandlers.keySet()) {
                if (line.startsWith("[" + module + "] ")) {
                    String param = line.substring(("[" + module + "] ").length());
                    BuffRemoveHandler handler = ModuleLoader.buffRemoveHandlers.get(module);
                    item = handler.handle(player, item, gem, id, param);
                }
            }
        }
        player.getInventory().setItemInMainHand(item);
        player.getInventory().addItem(gem);
        player.sendMessage(Color.color("&a拆卸成功!"));
        player.closeInventory();
    }
}
