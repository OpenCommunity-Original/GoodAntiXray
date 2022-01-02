package utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.opencommunity.envel.goodantixray.Main;

public class Lang
{
    private static FileConfiguration config;
    public static Object prefix;
    public static String notify;
    public static String checkMsg;
    public static String playerNotFound;
    public static String pluginReloaded;
    public static String cmdUsage;
    public static String cmdPlayerOnly;
    public static String xrayLvlReset;
    public static String xrayLvlSet;
    public static String primaryColor;
    public static String secondaryColor;
    public static String guiTitle;
    
    public static void loadMsg() {
        Main.instance.reloadConfig();
        Lang.config = Main.instance.getConfig();
        Lang.notify = getMsgFromConfig("notify");
        Lang.checkMsg = getMsgFromConfig("check");
        Lang.playerNotFound = getMsgFromConfig("playerNotFound");
        Lang.pluginReloaded = getMsgFromConfig("pluginReloaded");
        Lang.cmdUsage = getMsgFromConfig("cmdUsage");
        Lang.cmdPlayerOnly = getMsgFromConfig("cmdPlayerOnly");
        Lang.xrayLvlReset = getMsgFromConfig("xrayLvlReset");
        Lang.xrayLvlSet = getMsgFromConfig("xrayLvlSet");
        Lang.primaryColor = (isCorrectColorCode(Lang.config.getString("Prefix.primaryColor")) ? Lang.config.getString("Prefix.primaryColor").replaceAll("&", "§") : "§b§l");
        Lang.secondaryColor = (isCorrectColorCode(Lang.config.getString("Prefix.secondaryColor")) ? Lang.config.getString("Prefix.secondaryColor").replaceAll("&", "§") : "§f");
        Lang.prefix = Lang.primaryColor + "  GAX " + Lang.secondaryColor;
    }
    
    private static String getMsgFromConfig(final String msg) {
        return Lang.config.getString("Messages." + msg).replaceAll("&", "§");
    }
    
    private static boolean isCorrectColorCode(final String str) {
        return str.length() == 2 && str.charAt(0) == '&';
    }
}
