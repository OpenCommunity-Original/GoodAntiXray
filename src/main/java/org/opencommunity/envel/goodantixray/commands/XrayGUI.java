package org.opencommunity.envel.goodantixray.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.opencommunity.envel.goodantixray.Main;
import org.opencommunity.envel.goodantixray.managers.XrayManager;
import org.opencommunity.envel.goodantixray.managers.XrayStats;
import utils.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class XrayGUI {
    private final Inventory inventory;
    private int minLvl;
    private ArrayList<String> rightClickCmds;
    private ArrayList<String> leftClickCmds;
    private ArrayList<String> lore;

    public XrayGUI() {
        this.inventory = Bukkit.createInventory(null, 54, Lang.prefix + Lang.guiTitle);
        this.loadXrayGUIConfig();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, this.updateGUI(), 20L, 20L);
    }

    private void loadXrayGUIConfig() {
        final FileConfiguration config = Main.instance.getConfig();
        this.minLvl = config.getInt("GUI.minLvlToDisplay");
        this.rightClickCmds = (ArrayList<String>) config.getStringList("GUI.onClick.right");
        this.leftClickCmds = (ArrayList<String>) config.getStringList("GUI.onClick.left");
        this.lore = (ArrayList<String>) config.getStringList("GUI.skullLore");
        for (int i = 0; i < this.lore.size(); ++i) {
            this.lore.set(i, this.lore.get(i).replaceAll("&", "§"));
        }
    }

    public void openXrayGUI(final Player p) {
        p.openInventory(this.inventory);
    }

    private Runnable updateGUI() {
        return new Runnable() {
            @Override
            public void run() {
                if (XrayGUI.this.inventory.getViewers().size() > 0) {
                    List<XrayStats> toDisplay = XrayManager.statsMap.values().stream().filter(stat -> stat.getXrayLvl() > XrayGUI.this.minLvl).collect(Collectors.toList());
                    toDisplay.sort((stat1, stat2) -> Integer.compare(stat1.getXrayLvl(), stat2.getXrayLvl()));
                    if (toDisplay.size() > 53) {
                        toDisplay = toDisplay.subList(0, 53);
                    }
                    Collections.reverse(toDisplay);
                    XrayGUI.this.inventory.clear();
                    for (final XrayStats stats : toDisplay) {
                        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                        final SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                        //headMeta.setOwner(stats.getPlayer().getName());
                        headMeta.setDisplayName("§6" + stats.getPlayer().getName());
                        headMeta.setLore(XrayGUI.this.lore);
                        final ArrayList<String> tmpLore = (ArrayList<String>) headMeta.getLore();
                        for (int i = 0; i < tmpLore.size(); ++i) {
                            tmpLore.set(i, tmpLore.get(i).replaceAll("%target%", stats.getPlayer().getName()).replaceAll("%level%", String.valueOf(stats.getXrayLvl())));
                        }
                        headMeta.setLore(tmpLore);
                        head.setItemMeta(headMeta);
                        XrayGUI.this.inventory.addItem(head);
                    }
                }
            }
        };
    }

    public void onRightClick(final Player p, final Player target) {
        if (target != null) {
            for (final String cmd : this.rightClickCmds) {
                p.performCommand(cmd.replaceAll("%player%", p.getName()).replaceAll("%target%", target.getName()));
            }
        } else {
            p.sendMessage(Lang.prefix + Lang.playerNotFound);
        }
    }

    public void onLeftClick(final Player p, final Player target) {
        if (target != null) {
            for (final String cmd : this.leftClickCmds) {
                p.performCommand(cmd.replaceAll("%player%", p.getName()).replaceAll("%target%", target.getName()));
            }
        } else {
            p.sendMessage(Lang.prefix + Lang.playerNotFound);
        }
    }
}
