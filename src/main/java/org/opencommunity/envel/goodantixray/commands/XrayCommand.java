package org.opencommunity.envel.goodantixray.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.opencommunity.envel.goodantixray.Main;
import org.opencommunity.envel.goodantixray.managers.Sanction;
import org.opencommunity.envel.goodantixray.managers.XrayManager;
import org.opencommunity.envel.goodantixray.managers.XrayStats;
import utils.Lang;

public class XrayCommand implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length > 0 && sender.hasPermission("gax.use")) {
            if (args[0].equalsIgnoreCase("reload")) {
                Sanction.loadSanction();
                Lang.loadMsg();
                sender.sendMessage(Lang.prefix + Lang.pluginReloaded);
            } else if (args[0].equalsIgnoreCase("check")) {
                if (args.length > 1) {
                    final Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        final XrayStats stats = XrayManager.getXrayStats(target);
                        sender.sendMessage(Lang.prefix + Lang.checkMsg.replaceAll("%player%", target.getName()).replaceAll("%XrayLvl%", String.valueOf(stats.getXrayLvl())));
                    } else {
                        sender.sendMessage(Lang.prefix + Lang.playerNotFound);
                    }
                } else {
                    sender.sendMessage(Lang.prefix + Lang.cmdUsage + "/gax check <name>");
                }
            } else if (args[0].equalsIgnoreCase("gui") && sender.hasPermission("gax.gui")) {
                if (sender instanceof Player) {
                    final Player p = (Player) sender;
                    XrayManager.xrayGui.openXrayGUI(p);
                } else {
                    sender.sendMessage(Lang.prefix + Lang.cmdPlayerOnly);
                }
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (args.length > 1) {
                    final Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        final XrayStats stats = XrayManager.getXrayStats(target);
                        stats.resetStats();
                        sender.sendMessage(Lang.prefix + Lang.xrayLvlReset.replaceAll("%player%", target.getName()));
                    } else {
                        sender.sendMessage(Lang.prefix + Lang.playerNotFound);
                    }
                } else {
                    sender.sendMessage(Lang.prefix + Lang.cmdUsage + "/gax reset <name>");
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args.length > 2) {
                    final Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            final int value = Integer.parseInt(args[2]);
                            XrayManager.getXrayStats(target).setXrayLvl(value);
                            sender.sendMessage(Lang.prefix + Lang.xrayLvlSet.replaceAll("%player%", target.getName()).replaceAll("%value%", String.valueOf(value)));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(Lang.prefix + Lang.cmdUsage + "/gax set <name> <value>");
                        }
                    } else {
                        sender.sendMessage(Lang.prefix + Lang.playerNotFound);
                    }
                } else {
                    sender.sendMessage(Lang.prefix + Lang.cmdUsage + "/gax set <name> <value>");
                }
            } else {
                sender.sendMessage(Lang.prefix + Lang.cmdUsage + "/gax <reload,check,gui,set,reset>");
            }
        } else if (sender.hasPermission("gax.use")) {
            sender.sendMessage(Lang.prefix + Lang.cmdUsage + "/gax <reload,check,gui,set,reset>");
        } else {
            sender.sendMessage(Lang.prefix + "Version: " + Main.instance.getDescription().getVersion() + " by Envel");
        }
        return false;
    }
}
