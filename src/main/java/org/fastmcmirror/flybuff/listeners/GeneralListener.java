package org.fastmcmirror.flybuff.listeners;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.fastmcmirror.flybuff.FlyBuff;
import org.fastmcmirror.flybuff.api.BuffListenerHandler;
import org.fastmcmirror.flybuff.api.FlyBuffAPI;
import org.fastmcmirror.flybuff.module.ModuleLoader;

import java.util.function.Consumer;

public class GeneralListener implements Listener {
    private static void handle(ItemStack item, Event event, Consumer<ItemStack> consumer) {
        if (!FlyBuffAPI.hasInstalledBuff(item)) return;
        for (String buff : FlyBuffAPI.getAllInstalledBuff(item)) {
            for (String line : FlyBuff.instance.getConfig().getStringList("buffs." + buff + ".listener")) {
                String[] args = line.split("\\|\\|");
                String listener_name = args[0];
                if (event.getClass().getName().equals(listener_name)) {
                    String line_2 = args[1];
                    for (String module : ModuleLoader.buffListenerHandlers.keySet()) {
                        if (line_2.startsWith("[" + module + "] ")) {
                            String param = line_2.substring(("[" + module + "] ").length());
                            BuffListenerHandler handler = ModuleLoader.buffListenerHandlers.get(module);
                            consumer.accept(handler.handle(event, buff, item, param));
                        }
                    }
                }
            }
        }
    }

    public void onCallEvent(Event event) {
        if (event instanceof EntityEvent) {
            EntityEvent entityEvent = (EntityEvent) event;
            if (entityEvent.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) entityEvent.getEntity();
                ItemStack item = entity.getEquipment().getItemInMainHand();
                if (entityEvent instanceof EntityDamageByEntityEvent){
                    EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) entityEvent;
                    if (entityDamageByEntityEvent.getDamager() instanceof LivingEntity){
                        LivingEntity damager = (LivingEntity) entityDamageByEntityEvent.getDamager();
                        item = damager.getEquipment().getItemInMainHand();
                    }
                }
                if (item==null || item.getType().equals(Material.AIR) || item.getAmount()<1) return;
                handle(item, event, itemStack -> entity.getEquipment().setItemInMainHand(itemStack));
            }
        } else if (event instanceof PlayerEvent) {
            PlayerEvent playerEvent = (PlayerEvent) event;
            ItemStack item = playerEvent.getPlayer().getInventory().getItemInMainHand();
            if (item==null || item.getType().equals(Material.AIR) || item.getAmount()<1) return;
            handle(item, event, itemStack -> playerEvent.getPlayer().getInventory().setItemInMainHand(itemStack));
        }
    }
}
