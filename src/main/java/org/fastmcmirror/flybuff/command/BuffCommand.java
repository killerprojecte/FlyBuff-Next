package org.fastmcmirror.flybuff.command;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fastmcmirror.flybuff.FlyBuff;
import org.fastmcmirror.flybuff.api.FlyBuffAPI;
import org.fastmcmirror.flybuff.gui.RemoveGUI;
import org.fastmcmirror.flybuff.module.ModuleLoader;
import org.fastmcmirror.flybuff.utils.Color;

public class BuffCommand implements CommandExecutor {
    private static void sendHelp(CommandSender sender) {
        sender.sendMessage(Color.color("&b&lFlyBuff&8-&a&lNext &ev" + FlyBuff.instance.getDescription().getVersion() + " - &7命令帮助"));
        sender.sendMessage(Color.color("&8&m                                   "));
        if (sender.isOp()) {
            sender.sendMessage(Color.color("&7/flybuff reload &e————— 重载插件"));
            sender.sendMessage(Color.color("&7/flybuff get <Buff> &e————— 获取配置中的宝石"));
            sender.sendMessage(Color.color("&7/flybuff set <Buff> &e————— 将宝石设置到配置中"));
        }
        sender.sendMessage(Color.color("&7/flybuff removeui &e————— 打开宝石移除界面"));
        sender.sendMessage(Color.color("&8&m                                   "));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                if (!sender.isOp()) {
                    sendHelp(sender);
                    return false;
                }
                FlyBuff.instance.reloadConfig();
                ModuleLoader.unload();
                ModuleLoader.load();
                sender.sendMessage(Color.color("&a插件已重载!"));
            } else if (args[0].equals("removeui")) {
                if (!(sender instanceof Player)) return false;
                Player player = (Player) sender;
                RemoveGUI gui = new RemoveGUI(player);
                player.openInventory(gui.getInventory());
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 2) {
            if (args[0].equals("get")) {
                if (!sender.isOp()) {
                    sendHelp(sender);
                    return false;
                }
                if (!(sender instanceof Player)) return false;
                Player player = (Player) sender;
                player.getInventory().addItem(FlyBuffAPI.getGem(args[1]));
            } else if (args[0].equals("set")) {
                if (!sender.isOp()) {
                    sendHelp(sender);
                    return false;
                }
                if (!(sender instanceof Player)) return false;
                Player player = (Player) sender;
                FlyBuff.instance.getConfiguration().set("buffs." + args[1] + ".gem", NBTItem.convertItemtoNBT(player.getInventory().getItemInMainHand()).toString());
                FlyBuff.instance.saveConfig();
            } else {
                sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }
        return false;
    }
}
