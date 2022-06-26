package io.github.toberocat.improvedfactions.data;

import org.bukkit.Chunk;

public record ChunkCoords(int x, int z) {
    public static ChunkCoords fromChunk(Chunk chunk) {
        return new ChunkCoords(chunk.getX(), chunk.getZ());
    }
}
