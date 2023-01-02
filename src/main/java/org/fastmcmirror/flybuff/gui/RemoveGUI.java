package org.fastmcmirror.flybuff.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.fastmcmirror.flybuff.FlyBuff;
import org.fastmcmirror.flybuff.api.FlyBuffAPI;
import org.fastmcmirror.flybuff.utils.Color;
import org.fastmcmirror.flybuff.utils.ItemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveGUI implements InventoryHolder {
    public int page = 1;
    public List<String> buffs;
    private Inventory inventory;

    public RemoveGUI(Player player) {
        inventory = Bukkit.createInventory(this, 6 * 9, Color.color("&7&l宝石拆卸台"));
        buffs = FlyBuffAPI.getAllInstalledBuff(player.getItemInHand()).stream().filter(
                it -> FlyBuff.instance.getConfiguration().getBoolean("buffs." + it + ".enable-remove")
        ).collect(Collectors.toList());
        initUI();
        reloadItems();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void initUI() {
        ItemStack exit = ItemUtil.buildItem(Material.BARRIER, "&c关闭");
        ItemStack back = ItemUtil.buildItem(Material.ARROW, "&7上一页");
        ItemStack next = ItemUtil.buildItem(Material.ARROW, "&7下一页");
        inventory.setItem(45, back);
        inventory.setItem(49, exit);
        inventory.setItem(53, next);
    }

    public void reloadItems() {
        int amount = 0;
        for (int i = (page - 1) * 45; i < buffs.size(); i++) {
            inventory.setItem(amount, addLore(FlyBuffAPI.getGem(buffs.get(i)), "&8[&7点击移除&8]"));
            if ((amount + 1) % 45 == 0) return;
            amount++;
        }
    }

    public void clearItems() {
        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, null);
        }
    }

    public void nextPage() {
        page++;
        double a = page - 1;
        double b = buffs.size() / 45.0;
        if (a >= b) {
            page--;
            return;
        }
        clearItems();
        reloadItems();
    }

    private ItemStack addLore(ItemStack item, String... lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> list = new ArrayList<>();
        if (meta.hasLore()) {
            list.addAll(meta.getLore());
        }
        list.addAll(Arrays.stream(lore).map(it -> Color.color(it)).collect(Collectors.toList()));
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }

    public void backPage() {
        page--;
        if (page - 1 < 0) {
            page++;
            return;
        }
        clearItems();
        reloadItems();
    }
}
