package org.fastmcmirror.flybuff.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface BuffTickHandler {
    public ItemStack handle(Player player, ItemStack item, String buff_name, ItemSlot slot, String param);

    public String getIdentifier();
}
