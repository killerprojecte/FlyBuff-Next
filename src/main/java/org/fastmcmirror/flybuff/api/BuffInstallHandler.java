package org.fastmcmirror.flybuff.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface BuffInstallHandler {
    public BuffCallback checkCondition(Player player, ItemStack target, ItemStack gem, String buff_name, String param);

    public ItemStack handle(Player player, ItemStack target, ItemStack gem, String buff_name, String param);

    public String getIdentifier();
}
