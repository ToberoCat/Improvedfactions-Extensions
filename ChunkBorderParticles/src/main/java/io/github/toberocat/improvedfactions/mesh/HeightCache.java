package io.github.toberocat.improvedfactions.mesh;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.LinkedHashSet;

public record HeightCache(LinkedHashSet<Integer> heights) {

    public static HeightCache fromXZ(String name, int x, int z) {
        World world = Bukkit.getWorld(name);
        if (world == null) return null;

        Material lastMaterial = Material.BEDROCK;
        LinkedHashSet<Integer> heights = new LinkedHashSet<>();
        for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
            Block block = world.getBlockAt(x, y, z);
            Material now = block.getType();

            if (!now.isSolid() && lastMaterial.isSolid()) heights.add(y);
            lastMaterial = now;
        }

        return new HeightCache(heights);
    }
}
