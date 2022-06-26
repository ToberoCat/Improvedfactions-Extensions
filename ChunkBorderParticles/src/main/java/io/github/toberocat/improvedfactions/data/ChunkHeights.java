package io.github.toberocat.improvedfactions.data;

import org.bukkit.util.Vector;

import java.util.concurrent.CopyOnWriteArrayList;

public record ChunkHeights(RenderLocation location, CopyOnWriteArrayList<Vector> heights) {
}
