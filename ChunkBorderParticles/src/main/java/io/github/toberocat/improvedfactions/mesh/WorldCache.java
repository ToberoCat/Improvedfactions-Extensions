package io.github.toberocat.improvedfactions.mesh;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.async.AsyncTask;
import io.github.toberocat.improvedfactions.data.ChunkKey;
import io.github.toberocat.improvedfactions.data.Shape;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class WorldCache {

    private final HashMap<String, LinkedList<Shape>> shapes;
    private final HashMap<String, ArrayList<ChunkKey>> chunkScans;

    private final String worldName;

    public WorldCache(@NotNull String worldName) {
        this.worldName = worldName;

        this.shapes = new HashMap<>();
        this.chunkScans = new HashMap<>();
    }

    public void cacheRegistry(@NotNull String registry) {
        if (shapes.containsKey(registry)) return;
        recalculate(registry);
    }

    public Stream<Shape> getShapes(BoundingBox player) {
        List<LinkedList<Shape>> copy = shapes.values().stream().toList();
        return copy.stream().flatMap((list) -> list.stream()
                .filter(x -> player.contains(x.box())));
    }

    /**
     * Scans all claimed chunks and adds them to claim registry list
     * @param registry The registry of a faction that should get used for scanning
     */
    public void scanChunks(@NotNull String registry) {
        if (!chunkScans.containsKey(registry)) chunkScans.put(registry, new ArrayList<>());

        ArrayList<ChunkKey> scans = chunkScans.get(registry);
        MainIF.getIF().getClaimManager()
                .registryClaims(registry, worldName)
                .forEach((claim -> scans.add(new ChunkKey(claim.getX(), claim.getY()))));
    }

    /**
     * Add a chunk to the registry as claimed
     * @param registry
     * @param chunk
     */
    public void cacheChunk(@NotNull String registry, @NotNull Chunk chunk) {
        if (!shapes.containsKey(registry)) chunkScans.put(registry, new ArrayList<>());
        chunkScans.get(registry).add(new ChunkKey(chunk.getX(), chunk.getZ()));
    }

    public void removeChunkCache(@NotNull String registry, @NotNull Chunk chunk) {
        if (!shapes.containsKey(registry)) chunkScans.put(registry, new ArrayList<>());
        chunkScans.get(registry).remove(new ChunkKey(chunk.getX(), chunk.getZ()));
    }

    public void recalculate(@NotNull String registry) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        if (!chunkScans.containsKey(registry)) scanChunks(registry);

        // Clear previous shape cache
        shapes.remove(registry);

        ArrayList<ChunkKey> scans = chunkScans.get(registry);
        HashMap<UUID, ArrayList<ChunkKey>> groups = new HashMap<>();
        HashMap<ChunkKey, UUID> ids = new HashMap<>();

        // Group chunks into one shape per continuous border
        for (ChunkKey chunk : scans) {
            // Get the shape id the chunk belongs to
            ChunkKey neighbourWithId = AsyncTask.find(neighbours(chunk.x(), chunk.z()), ids::containsKey);
            UUID id = neighbourWithId == null ? UUID.randomUUID() : ids.get(neighbourWithId);
            ids.put(chunk, id);

            if (!groups.containsKey(id)) groups.put(id, new ArrayList<>());
            groups.get(id).add(chunk);
        }

        ShapeCalculator calculator = new ShapeCalculator(groups, ids);

        // Create lines from groups
        for (UUID id : groups.keySet()) {
            Shape shape = calculator.createGroupShape(world, id);
            if (shape == null) continue;

            if (!shapes.containsKey(registry)) shapes.put(registry, new LinkedList<>());
            shapes.get(registry).add(shape);
        }
    }

    private List<ChunkKey> neighbours(int x, int z) {
        return List.of(
                new ChunkKey(x - 1, z),
                new ChunkKey(x + 1, z),
                new ChunkKey(x, z - 1),
                new ChunkKey(x, z + 1)
        );
    }
}
