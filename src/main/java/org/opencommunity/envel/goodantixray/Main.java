package org.opencommunity.envel.goodantixray;

import org.bukkit.plugin.java.JavaPlugin;
import org.opencommunity.envel.goodantixray.commands.XrayCommand;
import org.opencommunity.envel.goodantixray.listeners.BlockListener;
import org.opencommunity.envel.goodantixray.managers.Sanction;
import utils.Lang;

public class Main extends JavaPlugin {
    public static Main instance;

    public void onEnable() {
        Main.instance = this;
        this.saveDefaultConfig();
        Lang.loadMsg();
        Sanction.loadSanction();
        this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
        this.getCommand("GoodAntiXray").setExecutor(new XrayCommand());
    }

    public void onDisable() {
    }
}
