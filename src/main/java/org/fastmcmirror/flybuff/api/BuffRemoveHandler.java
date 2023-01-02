package org.fastmcmirror.flybuff.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface BuffRemoveHandler {
    public BuffCallback checkCondition(Player player, ItemStack target, ItemStack gem, String buff_name, String param);

    public ItemStack handle(Player player, ItemStack target, ItemStack gem, String buff_name, String param);

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
