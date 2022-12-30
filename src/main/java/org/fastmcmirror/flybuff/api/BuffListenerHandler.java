package org.fastmcmirror.flybuff.api;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public interface BuffListenerHandler {
    public ItemStack handle(Event event, String buff_name, ItemStack item, String param);

    public String getIdentifier();
}
