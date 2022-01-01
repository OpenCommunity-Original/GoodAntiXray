package org.opencommunity.envel.goodantixray.listeners;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.opencommunity.envel.goodantixray.managers.XrayManager;

public class BlockListener implements Listener {
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        if (!e.isCancelled() && !p.hasPermission("gax.evade") && p.getLocation().getBlockY() < 30 && p.getWorld().getEnvironment() == World.Environment.NORMAL) {
            final Block block = e.getBlock();
            if (XrayManager.placed.remove(block)) {
                return;
            }
            XrayManager.updateXrayStats(e);
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player p = e.getPlayer();
        if (!e.isCancelled() && e.getBlock().getLocation().getBlockY() < 30 && p.getWorld().getEnvironment() == World.Environment.NORMAL) {
            XrayManager.placed.add(e.getBlock());
        }
    }
}
