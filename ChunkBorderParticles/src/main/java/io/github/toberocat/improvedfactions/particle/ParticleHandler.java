package io.github.toberocat.improvedfactions.particle;

import io.github.toberocat.MainIF;
import io.github.toberocat.improvedfactions.ChunkParticleBorderExtension;
import io.github.toberocat.improvedfactions.data.PositionPair;
import io.github.toberocat.improvedfactions.data.Shape;
import io.github.toberocat.improvedfactions.mesh.WorldCache;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import static io.github.toberocat.core.utility.Utility.clamp;

public class ParticleHandler {
    public static int DENSITY = 1;
    private final ArrayList<UUID> displayParticles;


    public ParticleHandler(MainIF plugin) {
        displayParticles = new ArrayList<>();

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (UUID uuid : new ArrayList<>(displayParticles)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) continue;
                sendParticles(player);
            }
        }, 1, ChunkParticleBorderExtension.VISUALISATION_INTERVAL);
    }

    private void createParticle(Player player, double x, double y, double z, Particle.DustOptions dust) {
        player.spawnParticle(
                Particle.REDSTONE,
                new Location(player.getWorld(), x, y, z),
                0,
                dust
        );
    }

    public void sendParticles(Player player) {
        long renderDstSqrt = (long) clamp(
                Math.pow(player.getClientViewDistance() * 16, 2),
                16,
                ChunkParticleBorderExtension.MAX_PARTICLE_RENDER_DISTANCE);
        long renderDst = (long) Math.sqrt(renderDstSqrt);

        BoundingBox playerViewBox = getPlayerViewBox(renderDst, player);
        WorldCache worldCache = ChunkParticleBorderExtension.meshCache.getCache(player.getWorld().getName());
        World world = player.getWorld();
        Stream<Shape> renderShapes = worldCache.getShapes(playerViewBox);

        renderShapes.forEach((shape) -> shape.lines().forEach((line -> {
            for (int i = 0; i < line.getLocations().size(); i += DENSITY) {
                PositionPair pair = line.getLocations().get(i);

                Color c = Math.random() < 0.2 ? Color.BLACK : Color.RED;
                if (Math.random() < 0.2) c = Color.WHITE;

                float size = 5;

                Particle.DustOptions dust = new Particle.DustOptions(c, size);
                createParticle(player, pair.x() + 0.5, world.getHighestBlockYAt(pair.x(), pair.z()) + 1.5,
                        pair.z() + 0.5, dust);
            }
        })));
    }

    private BoundingBox getPlayerViewBox(long renderDst, Player player) {
        Location pos = player.getLocation();
        World world = player.getWorld();
        return new BoundingBox(
                pos.getX() - renderDst, world.getMinHeight(), pos.getZ() - renderDst,
                pos.getX() + renderDst, world.getMaxHeight(), pos.getZ() + renderDst
        );
    }

    public void addPlayer(Player player) {
        displayParticles.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        displayParticles.remove(player.getUniqueId());
    }
}
