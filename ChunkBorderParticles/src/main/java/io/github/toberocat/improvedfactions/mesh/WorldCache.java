package io.github.toberocat.improvedfactions.mesh;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.async.AsyncTask;
import io.github.toberocat.improvedfactions.data.ChunkKey;
import io.github.toberocat.improvedfactions.data.Shape;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class WorldCache {

    private final HashMap<String, LinkedList<Shape>> shapes;
    private final HashMap<String, ArrayList<ChunkKey>> chunkScans;
    private final HashMap<ChunkKey, UUID> ids;
    private final String worldName;
    HashMap<UUID, ArrayList<ChunkKey>> groups;

    public WorldCache(@NotNull String worldName) {
        this.worldName = worldName;
        this.shapes = new HashMap<>();

        this.chunkScans = new HashMap<>();
        this.ids = new HashMap<>();
        this.groups = new HashMap<>();
    }

    public void dispose() {
        shapes.clear();
        chunkScans.clear();
    }

    public void removeCache(@NotNull String registry) {
        shapes.remove(registry);
        chunkScans.remove(registry);
    }

    public void cacheRegistry(@NotNull String registry) {
        if (shapes.containsKey(registry)) return;
        recalculate(registry);
    }

    public Stream<Map.Entry<String, Stream<Shape>>> getShapes(BoundingBox player) {
        List<Map.Entry<String, LinkedList<Shape>>> copy = shapes.entrySet().stream().toList();
        return copy.stream().map(entry -> {
            Stream<Shape> items = entry.getValue().stream().filter(x -> player.overlaps(x.box()));

            return Map.entry(entry.getKey(), items);
        });
    }

    /**
     * Scans all claimed chunks and adds them to claim registry list
     *
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
     */
    public void cacheChunk(@NotNull String registry, @NotNull Chunk chunk) {
        if (!shapes.containsKey(registry)) chunkScans.put(registry, new ArrayList<>());
        chunkScans.get(registry).add(new ChunkKey(chunk.getX(), chunk.getZ()));
    }

    public void removeChunkCache(@NotNull String registry, @NotNull Chunk chunk) {
        if (!shapes.containsKey(registry)) chunkScans.put(registry, new ArrayList<>());
        chunkScans.get(registry).remove(new ChunkKey(chunk.getX(), chunk.getZ()));
    }

    public void updateChunk(@NotNull String registry, @NotNull Chunk action) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        if (!chunkScans.containsKey(registry)) scanChunks(registry);
        ChunkKey key = new ChunkKey(action.getX(), action.getZ());

        // Clear previous shape cache
        shapes.remove(registry);

        ArrayList<ChunkKey> scans = chunkScans.get(registry);
        if (!scans.contains(key)) {
            UUID id = getUuid(action.getX(), action.getZ());
            ids.remove(key, id);

            if (!groups.containsKey(id)) groups.put(id, new ArrayList<>());
            groups.get(id).remove(key);

            ShapeCalculator calculator = new ShapeCalculator(groups, ids);
            Shape shape = calculator.createGroupShape(world, id);
            if (shape == null) {
                shapes.remove(registry);
                return;
            }

            if (!shapes.containsKey(registry)) shapes.put(registry, new LinkedList<>());
            shapes.get(registry).add(shape);
        } else {
            UUID id = getUuid(action.getX(), action.getZ());
            ids.put(key, id);

            if (!groups.containsKey(id)) groups.put(id, new ArrayList<>());
            groups.get(id).add(key);

            ShapeCalculator calculator = new ShapeCalculator(groups, ids);
            Shape shape = calculator.createGroupShape(world, id);
            if (shape == null) return;

            if (!shapes.containsKey(registry)) shapes.put(registry, new LinkedList<>());
            shapes.get(registry).add(shape);
        }
    }

    private UUID getUuid(int x, int z) {
        ChunkKey neighbourWithId = AsyncTask.find(neighbours(x, z), ids::containsKey);
        return neighbourWithId == null ? UUID.randomUUID() : ids.get(neighbourWithId);
    }

    public void recalculate(@NotNull String registry) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        if (!chunkScans.containsKey(registry)) scanChunks(registry);

        // Clear previous shape cache
        shapes.remove(registry);
        ids.clear();
        groups.clear();

        ArrayList<ChunkKey> scans = chunkScans.get(registry);

        // Group chunks into one shape per continuous border
        for (ChunkKey chunk : scans) {
            // Get the shape id the chunk belongs to
            UUID id = getUuid(chunk.x(), chunk.z());
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
