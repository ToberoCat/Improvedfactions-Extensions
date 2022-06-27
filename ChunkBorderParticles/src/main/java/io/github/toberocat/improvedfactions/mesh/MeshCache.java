package io.github.toberocat.improvedfactions.mesh;

import io.github.toberocat.core.factions.FactionUtility;
import io.github.toberocat.core.utility.claim.ClaimManager;
import io.github.toberocat.improvedfactions.ChunkParticleBorderExtension;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class MeshCache {

    private final LinkedHashMap<String, WorldCache> worldCache;

    public MeshCache() {
        this.worldCache = new LinkedHashMap<>();

        for (World world : Bukkit.getWorlds()) worldCache.put(world.getName(), new WorldCache(world.getName()));
    }

    public void dispose() {
        worldCache.values().forEach(WorldCache::dispose);
        worldCache.clear();
    }

    public void cacheFactions() {
        FactionUtility.getAllFactionsStream().forEach(this::cacheRegistry);
    }

    public void cacheZones() {
        ClaimManager.getZones(ChunkParticleBorderExtension.DISPLAY_UNCLAIMABLE).forEach(this::cacheRegistry);
    }

    public void cacheChunk(@NotNull String registry, @NotNull Chunk chunk) {
        WorldCache cache = worldCache.get(chunk.getWorld().getName());
        cache.cacheChunk(registry, chunk);
        cache.updateChunk(registry, chunk);
    }

    public void removeCacheChunk(@NotNull String registry, @NotNull Chunk chunk) {
        WorldCache cache = worldCache.get(chunk.getWorld().getName());
        cache.removeChunkCache(registry, chunk);
        cache.recalculate(registry);
    }

    public void removeCache(@NotNull String registry) {
        worldCache.values().forEach(cache -> cache.removeCache(registry));
    }

    public void cacheRegistry(@NotNull String registry) {
        worldCache.values().forEach((cache) -> cache.cacheRegistry(registry));
    }

    public void updateChunk(@NotNull String registry, @NotNull Chunk chunk) {

    }

    public WorldCache getCache(@NotNull String world) {
        return worldCache.get(world);
    }
}
