package org.opencommunity.envel.goodantixray.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.opencommunity.envel.goodantixray.Main;
import utils.Lang;

import java.util.ArrayList;
import java.util.HashSet;

public class Sanction {
    private static ArrayList<Sanction> list;
    private final int lvl;
    private final boolean notify;
    private ArrayList<String> commands;
    private final HashSet<Player> done;

    static {
        Sanction.list = new ArrayList<Sanction>();
    }

    public Sanction(final int lvl, final ArrayList<String> commands, final boolean notify) {
        this.commands = new ArrayList<String>();
        this.done = new HashSet<Player>();
        this.lvl = lvl;
        this.commands = commands;
        this.notify = notify;
    }

    public static void searchAndApplySanction(final XrayStats stats) {
        for (final Sanction sanction : Sanction.list) {
            if (stats.getXrayLvl() >= sanction.lvl && !sanction.isAlreadyDone(stats.getPlayer())) {
                if (sanction.notify) {
                    Bukkit.broadcast(Lang.prefix + Lang.notify.replaceAll("%player%", stats.getPlayer().getName()), "gax.alerts");
                }
                for (final String cmd : sanction.commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player%", stats.getPlayer().getName()));
                }
                sanction.done.add(stats.getPlayer());
            }
        }
    }

    public static void resetSanction(final XrayStats stats) {
        for (final Sanction sanction : Sanction.list) {
            sanction.done.remove(stats.getPlayer());
        }
    }

    public static void loadSanction() {
        final FileConfiguration config = Main.instance.getConfig();
        Sanction.list.clear();
        for (final String key : config.getConfigurationSection("Sanction").getKeys(false)) {
            final int lvl = Integer.valueOf(key);
            final boolean notify = config.getBoolean("Sanction." + key + ".notify");
            final ArrayList<String> commands = (ArrayList<String>) config.getStringList("Sanction." + key + ".commands");
            Sanction.list.add(new Sanction(lvl, commands, notify));
        }
    }

    private boolean isAlreadyDone(final Player p) {
        return this.done.contains(p);
    }
}
