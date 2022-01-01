package org.opencommunity.envel.goodantixray.managers;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class XrayBlock {
    private Block block;
    private final ArrayList<Integer> distanceList;

    public XrayBlock(final Block block, final int dist) {
        this.distanceList = new ArrayList<Integer>();
        this.block = block;
        this.distanceList.add(dist);
    }

    public int analyzeParkour() {
        int xrayLvl = 0;
        for (int i = 1; i < this.distanceList.size(); ++i) {
            final int lastDistance = this.distanceList.get(i - 1);
            final int currentDistance = this.distanceList.get(i);
            final int distanceBetween = lastDistance - currentDistance;
            if (distanceBetween > 0) {
                xrayLvl += distanceBetween * 7;
            } else if (distanceBetween <= 0) {
                xrayLvl += (int) (distanceBetween * 6.75);
            }
        }
        if (this.block.getType() == Material.GOLD_ORE) {
            xrayLvl *= (int) 0.5;
        } else if (this.block.getType() == Material.DIAMOND_ORE) {
            xrayLvl *= (int) 0.9;
        } else if (this.block.getType() == Material.EMERALD_ORE) {
            xrayLvl *= 1;
        } else if (this.block.getType() == Material.ANCIENT_DEBRIS) {
            xrayLvl *= 10;
        }
        return xrayLvl;
    }

    public void updateDistance(final int distance) {
        if (this.distanceList.size() > 65) {
            for (int i = 0; i < this.distanceList.size() - 65; ++i) {
                this.distanceList.remove(i);
            }
        }
        this.distanceList.add(distance);
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBlock(final Block block) {
        this.block = block;
    }
}
