package org.opencommunity.envel.goodantixray.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.opencommunity.envel.goodantixray.commands.XrayGUI;

import java.util.*;

public class XrayManager {
    public static HashSet<Block> placed;
    public static HashMap<UUID, XrayStats> statsMap;
    private static BlockFace[] faces;
    public static XrayGUI xrayGui;

    static {
        XrayManager.placed = new HashSet<Block>();
        XrayManager.statsMap = new HashMap<UUID, XrayStats>();
        XrayManager.xrayGui = new XrayGUI();
        XrayManager.faces = new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    }

    public static void updateXrayStats(final BlockBreakEvent e) {
        final XrayStats stats = getXrayStats(e.getPlayer());
        checkMinedXray(e, stats);
        try {
            stats.updateTrackedBlocks(getDirectionOres(e.getPlayer(), 20), e.getPlayer(), e.getBlock());
        } catch (Exception ex) {
            Bukkit.getLogger().warning(ex.getMessage());
        }
        updateAllStats(e.getBlock());
        stats.updateMinedOreStats(e.getBlock());
    }

    private static void checkMinedXray(final BlockBreakEvent e, final XrayStats stats) {
        final Block block = e.getBlock();
        if (block.getType() == Material.EMERALD_ORE || block.getType() == Material.DIAMOND_ORE || block.getType() == Material.GOLD_ORE) {
            for (final XrayBlock xrayBlock : stats.getTrackedBlocks()) {
                if (xrayBlock.getBlock().equals(block)) {
                    stats.setTrackLvl((int) ((stats.getTrackLvl() + xrayBlock.analyzeParkour()) * stats.getTimeMultiplicator(block.getType()) * stats.getPickaxeMultplicator(e.getPlayer().getInventory().getItemInMainHand())));
                    for (final Block block2 : getNearbyOres(e.getBlock().getLocation(), 2)) {
                        final Iterator<XrayBlock> iter2 = stats.getTrackedBlocks().iterator();
                        while (iter2.hasNext()) {
                            final XrayBlock xrayBlock2 = iter2.next();
                            if (xrayBlock2.getBlock().equals(block2)) {
                                iter2.remove();
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private static void updateAllStats(final Block block) {
        if (block.getType() == Material.EMERALD_ORE || block.getType() == Material.DIAMOND_ORE || block.getType() == Material.GOLD_ORE) {
            for (final Map.Entry<UUID, XrayStats> entry : XrayManager.statsMap.entrySet()) {
                final XrayStats xrayStats = entry.getValue();
                if (xrayStats != null) {
                    for (final XrayBlock xrayBlock : xrayStats.getTrackedBlocks()) {
                        if (xrayBlock.getBlock().equals(block)) {
                            xrayStats.getTrackedBlocks().remove(xrayBlock);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static ArrayList<Block> getDirectionOres(final Player p, final int radius) {
        final ArrayList<Block> oresList = new ArrayList<Block>();
        final String direction = getCardinalDirection(p);
        int yMin = p.getLocation().getBlockY();
        int yMax = 30 - p.getLocation().getBlockY();
        if (yMax > radius) {
            yMax = radius;
        }
        if (yMin > radius) {
            yMin = radius;
        }
        final Block middle = p.getLocation().getBlock();
        if (direction.equalsIgnoreCase("South")) {
            for (int x = radius; x >= -radius; --x) {
                for (int y = yMax; y >= -yMin; --y) {
                    for (int z = radius; z >= -2; --z) {
                        final Block currentBlock = middle.getRelative(x, y, z);
                        if (isInterestingOre(currentBlock)) {
                            oresList.add(currentBlock);
                        }
                    }
                }
            }
        } else if (direction.equalsIgnoreCase("West")) {
            for (int x = 2; x >= -radius; --x) {
                for (int y = yMax; y >= -yMin; --y) {
                    for (int z = radius; z >= -radius; --z) {
                        final Block currentBlock = middle.getRelative(x, y, z);
                        if (isInterestingOre(currentBlock)) {
                            oresList.add(currentBlock);
                        }
                    }
                }
            }
        } else if (direction.equalsIgnoreCase("East")) {
            for (int x = radius; x >= -2; --x) {
                for (int y = yMax; y >= -yMin; --y) {
                    for (int z = radius; z >= -radius; --z) {
                        final Block currentBlock = middle.getRelative(x, y, z);
                        if (isInterestingOre(currentBlock)) {
                            oresList.add(currentBlock);
                        }
                    }
                }
            }
        } else {
            for (int x = radius; x >= -radius; --x) {
                for (int y = yMax; y >= -yMin; --y) {
                    for (int z = 2; z >= -radius; --z) {
                        final Block currentBlock = middle.getRelative(x, y, z);
                        if (isInterestingOre(currentBlock)) {
                            oresList.add(currentBlock);
                        }
                    }
                }
            }
        }
        return oresList;
    }

    static ArrayList<Block> getNearbyOres(final Player p, final int radius) {
        int yMin = p.getLocation().getBlockY();
        int yMax = 30 - p.getLocation().getBlockY();
        if (yMax > radius) {
            yMax = radius;
        }
        if (yMin > radius) {
            yMin = radius;
        }
        final ArrayList<Block> oresList = new ArrayList<Block>();
        final Block middle = p.getLocation().getBlock();
        for (int x = radius; x >= -radius; --x) {
            for (int y = yMax; y >= -yMin; --y) {
                for (int z = radius; z >= -radius; --z) {
                    final Block currentBlock = middle.getRelative(x, y, z);
                    if (isInterestingOre(currentBlock)) {
                        oresList.add(currentBlock);
                    }
                }
            }
        }
        return oresList;
    }

    private static ArrayList<Block> getNearbyOres(final Location loc, final int radius) {
        int yMin = loc.getBlockY();
        int yMax = 30 - loc.getBlockY();
        if (yMax > radius) {
            yMax = radius;
        }
        if (yMin > radius) {
            yMin = radius;
        }
        final ArrayList<Block> oresList = new ArrayList<Block>();
        final Block middle = loc.getBlock();
        for (int x = radius; x >= -radius; --x) {
            for (int y = yMax; y >= -yMin; --y) {
                for (int z = radius; z >= -radius; --z) {
                    final Block currentBlock = middle.getRelative(x, y, z);
                    if (isInterestingOre(currentBlock)) {
                        oresList.add(currentBlock);
                    }
                }
            }
        }
        return oresList;
    }

    public static String getCardinalDirection(final Player player) {
        double rotation = player.getLocation().getYaw();
        if (rotation < 0.0) {
            rotation += 360.0;
        }
        if (0.0 <= rotation && rotation < 45.0) {
            return "South";
        }
        if (45.0 <= rotation && rotation < 135.0) {
            return "West";
        }
        if (135.0 <= rotation && rotation < 225.0) {
            return "North";
        }
        if (225.0 <= rotation && rotation < 337.5) {
            return "East";
        }
        if (337.5 <= rotation && rotation < 360.0) {
            return "North";
        }
        return null;
    }

    public static XrayStats getXrayStats(final Player p) {
        if (XrayManager.statsMap.containsKey(p.getUniqueId())) {
            return XrayManager.statsMap.get(p.getUniqueId());
        }
        final XrayStats stats = new XrayStats(p);
        XrayManager.statsMap.put(p.getUniqueId(), stats);
        return stats;
    }

    public static boolean isInterestingOre(final Block block) {
        boolean isInteresting = false;
        if (block.getType() == Material.EMERALD_ORE || block.getType() == Material.DIAMOND_ORE || block.getType() == Material.GOLD_ORE) {
            isInteresting = true;
            BlockFace[] faces;
            for (int length = (faces = XrayManager.faces).length, i = 0; i < length; ++i) {
                final BlockFace face = faces[i];
                if (block.getRelative(face).getType() == Material.AIR) {
                    isInteresting = false;
                    break;
                }
            }
        }
        return isInteresting;
    }
}
