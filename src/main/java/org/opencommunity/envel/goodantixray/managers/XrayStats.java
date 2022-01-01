package org.opencommunity.envel.goodantixray.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.opencommunity.envel.goodantixray.Main;

import java.util.ArrayList;
import java.util.Iterator;

public class XrayStats {
    private int xrayLvl;
    private int trackLvl;
    private int diamondMined;
    private int emeraldMined;
    private int goldMined;
    private long lastGold;
    private long lastDiamond;
    private long lastEmerald;
    private final Player p;
    private ArrayList<XrayBlock> trackedBlocks;
    private ArrayList<Block> toIgnore;
    private static BlockFace[] faces;

    static {
        XrayStats.faces = new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    }

    public XrayStats(final Player p) {
        this.xrayLvl = 0;
        this.trackLvl = 0;
        this.diamondMined = 0;
        this.emeraldMined = 0;
        this.goldMined = 0;
        this.lastGold = 0L;
        this.lastDiamond = 0L;
        this.lastEmerald = 0L;
        this.trackedBlocks = new ArrayList<XrayBlock>();
        this.toIgnore = new ArrayList<Block>();
        this.p = p;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                XrayStats.this.resetStats();
            }
        }, 6000L, 6000L);
    }

    private void checkAndUpdateXrayLvl() {
        this.xrayLvl = 0;
        this.xrayLvl += this.diamondMined * 25;
        this.xrayLvl += this.emeraldMined * 35;
        this.xrayLvl += this.goldMined * 12;
        this.xrayLvl += this.trackLvl;
        Sanction.searchAndApplySanction(this);
    }

    public void resetStats() {
        this.xrayLvl = 0;
        this.trackLvl = 0;
        this.diamondMined = 0;
        this.emeraldMined = 0;
        this.goldMined = 0;
        Sanction.resetSanction(this);
        if (this.p != null && this.p.isOnline() && this.p.getLocation().getBlockY() > 30) {
            this.trackedBlocks.clear();
        }
    }

    public void updateTrackedBlocks(final ArrayList<Block> oresList, final Player p, final Block eventBlock) {
        Iterator<XrayBlock> iter = this.trackedBlocks.iterator();
        while (iter.hasNext()) {
            final XrayBlock xrayBlock = iter.next();
            if (xrayBlock.getBlock().getWorld() == p.getWorld()) {
                if ((int) xrayBlock.getBlock().getLocation().distance(p.getLocation()) > 25) {
                    iter.remove();
                } else {
                    xrayBlock.updateDistance((int) xrayBlock.getBlock().getLocation().distance(eventBlock.getLocation()));
                }
            }
        }
        final ArrayList<XrayBlock> toCreate = new ArrayList<XrayBlock>();
        for (final Block block : oresList) {
            boolean create = true;
            iter = this.trackedBlocks.iterator();
            while (iter.hasNext()) {
                final XrayBlock xrayBlock2 = iter.next();
                if (block.equals(xrayBlock2.getBlock())) {
                    create = false;
                    break;
                }
            }
            if (create) {
                boolean ok = true;
                Label_0326:
                for (int x = 2; x >= -2; --x) {
                    for (int y = 2; y >= -2; --y) {
                        for (int z = 2; z >= -2; --z) {
                            final Block currentBlock = block.getRelative(x, y, z);
                            if (currentBlock.getType() == block.getType()) {
                                BlockFace[] faces;
                                for (int length = (faces = XrayStats.faces).length, i = 0; i < length; ++i) {
                                    final BlockFace face = faces[i];
                                    if (currentBlock.getRelative(face).getType() == Material.AIR) {
                                        ok = false;
                                        break Label_0326;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!ok) {
                    continue;
                }
                toCreate.add(new XrayBlock(block, (int) p.getLocation().distance(block.getLocation())));
            }
        }
        this.trackedBlocks.addAll(toCreate);
    }

    public void updateMinedOreStats(final Block block) {
        if (block.getType() == Material.DIAMOND_ORE) {
            ++this.diamondMined;
            this.lastDiamond = System.currentTimeMillis();
            this.checkAndUpdateXrayLvl();
        } else if (block.getType() == Material.EMERALD_ORE) {
            this.lastEmerald = System.currentTimeMillis();
            ++this.emeraldMined;
            this.checkAndUpdateXrayLvl();
        } else if (block.getType() == Material.GOLD_ORE) {
            this.lastGold = System.currentTimeMillis();
            ++this.goldMined;
            this.checkAndUpdateXrayLvl();
        }
    }

    public float getTimeMultiplicator(final Material material) {
        float multiplicator = 1.0f;
        int elapsedTime = 0;
        if (material == Material.GOLD_ORE) {
            if (this.lastGold == 0L) {
                return 1.0f;
            }
            elapsedTime = (int) (System.currentTimeMillis() / 1000L - this.lastGold / 1000L);
            multiplicator = (float) (1 - elapsedTime * 0);
            multiplicator += (float) 0.05;
        } else if (material == Material.DIAMOND_ORE) {
            if (this.lastDiamond == 0L) {
                return 0.75f;
            }
            elapsedTime = (int) (System.currentTimeMillis() / 1000L - this.lastDiamond / 1000L);
            multiplicator = (float) (1 - elapsedTime * 0);
            multiplicator += (float) 0.075;
        } else if (material == Material.EMERALD_ORE) {
            if (this.lastEmerald == 0L) {
                return 1.0f;
            }
            elapsedTime = (int) (System.currentTimeMillis() / 1000L - this.lastEmerald / 1000L);
            multiplicator = (float) (1 - elapsedTime * 0);
            multiplicator += (float) 0.2;
        }
        if (multiplicator < 0.3) {
            multiplicator = 0.3f;
        } else if (multiplicator > 1.0f) {
            multiplicator = 1.0f;
        }
        return multiplicator;
    }

    public float getPickaxeMultplicator(final ItemStack item) {
        float pickAxeMultiplicator = 1.0f;
        float enchantmentMultiplicator = 3.71f;
        if (item == null) {
            return enchantmentMultiplicator;
        }
        final int efficiencyPower = item.hasItemMeta() ? item.getItemMeta().getEnchantLevel(Enchantment.DIG_SPEED) : 0;
        if (efficiencyPower <= 2) {
            return pickAxeMultiplicator;
        }
        if (item.getType() == Material.IRON_PICKAXE) {
            pickAxeMultiplicator = 1.25f;
        } else if (item.getType() == Material.GOLDEN_PICKAXE) {
            pickAxeMultiplicator = 5.0f;
        }
        for (int i = 0; i < efficiencyPower; ++i) {
            enchantmentMultiplicator -= (float) (23.065 * enchantmentMultiplicator / 100.0);
        }
        final float finalMultiplicator = pickAxeMultiplicator * enchantmentMultiplicator;
        return finalMultiplicator;
    }

    public int getXrayLvl() {
        return this.xrayLvl;
    }

    public void setXrayLvl(final int xrayLvl) {
        this.xrayLvl = xrayLvl;
    }

    public ArrayList<XrayBlock> getTrackedBlocks() {
        return this.trackedBlocks;
    }

    public void setTrackedBlocks(final ArrayList<XrayBlock> trackedBlocks) {
        this.trackedBlocks = trackedBlocks;
    }

    public ArrayList<Block> getToIgnore() {
        return this.toIgnore;
    }

    public void setToIgnore(final ArrayList<Block> toIgnore) {
        this.toIgnore = toIgnore;
    }

    public int getTrackLvl() {
        return this.trackLvl;
    }

    public void setTrackLvl(final int trackLvl) {
        this.trackLvl = trackLvl;
    }

    public long getLastGold() {
        return this.lastGold;
    }

    public void setLastGold(final long lastGold) {
        this.lastGold = lastGold;
    }

    public long getLastDiamond() {
        return this.lastDiamond;
    }

    public void setLastDiamond(final long lastDiamond) {
        this.lastDiamond = lastDiamond;
    }

    public long getLastEmerald() {
        return this.lastEmerald;
    }

    public void setLastEmerald(final long lastEmerald) {
        this.lastEmerald = lastEmerald;
    }

    public Player getPlayer() {
        return this.p;
    }
}
