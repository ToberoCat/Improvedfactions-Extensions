package io.github.toberocat.improvedfactions.data;

import io.github.toberocat.improvedfactions.mesh.HeightCache;
import org.bukkit.util.Vector;

public record PositionPair(int x, int z, HeightCache cache) {
    public Vector toVector(double y) {
        return new Vector(x, y, z);
    }
}
