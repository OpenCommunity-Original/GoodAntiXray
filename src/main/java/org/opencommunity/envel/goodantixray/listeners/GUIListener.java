package org.opencommunity.envel.goodantixray.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.opencommunity.envel.goodantixray.managers.XrayManager;
import utils.Lang;

public class GUIListener implements Listener {
    @EventHandler
    public void onGUIClick(final InventoryClickEvent e) {
        if (e.getView().getTitle().equals(Lang.prefix + "GUI")) {
            final ItemStack item = e.getCurrentItem();
            if (item != null && item.hasItemMeta()) {
                final Player target = Bukkit.getPlayer(item.getItemMeta().getDisplayName().substring(2));
                if (e.isLeftClick()) {
                    XrayManager.xrayGui.onLeftClick((Player) e.getWhoClicked(), target);
                } else {
                    XrayManager.xrayGui.onRightClick((Player) e.getWhoClicked(), target);
                }
                e.setCancelled(true);
            }
        }
    }
}
