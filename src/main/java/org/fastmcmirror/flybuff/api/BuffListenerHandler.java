package org.fastmcmirror.flybuff.api;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface BuffListenerHandler {
    public ItemStack handle(Event event, String buff_name, ItemStack item, String param);

    public String getIdentifier();

    public String getAuthor();

    public String getVersion();

    public default List<String> getDependencies() {
        return new ArrayList<>();
    }

    public default String getName() {
        return getIdentifier();
    }
}
