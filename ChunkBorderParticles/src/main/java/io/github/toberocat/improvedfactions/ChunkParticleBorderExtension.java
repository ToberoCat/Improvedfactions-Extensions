package io.github.toberocat.improvedfactions;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.extensions.Extension;
import io.github.toberocat.core.utility.async.AsyncTask;
import io.github.toberocat.improvedfactions.listener.ChunkListener;
import io.github.toberocat.improvedfactions.listener.PlayerListener;
import io.github.toberocat.improvedfactions.mesh.MeshCache;
import io.github.toberocat.improvedfactions.particle.ParticleHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class ChunkParticleBorderExtension extends Extension {
    public static int VISUALISATION_INTERVAL;
    public static boolean DISPLAY_UNCLAIMABLE;
    public static int MAX_PARTICLE_RENDER_DISTANCE;

    public static MeshCache meshCache;
    public static ParticleHandler handler;

    @Override
    protected void onEnable(MainIF plugin) {
        reloadConfigs();

        AsyncTask.runLater(1, () -> {
            MainIF.logMessage(Level.INFO, "&aCaching particle meshes... this may take a while");
            createShapes(plugin);
            registerListeners(plugin);
            addPlayers();
            MainIF.logMessage(Level.INFO, "&aCaching particle meshes finished. Particles will now get spawned");
        });
    }

    @Override
    public void reloadConfigs() {
        setConfigDefaultValue("interval", 10L);
        VISUALISATION_INTERVAL = configValue("interval");

        setConfigDefaultValue("display-unclaimable", true);
        DISPLAY_UNCLAIMABLE = configValue("display-unclaimable");

        setConfigDefaultValue("max-particle-render-distance", Math.round(Math.pow(Bukkit.getViewDistance() * 16, 2)));
        MAX_PARTICLE_RENDER_DISTANCE = configValue("max-particle-render-distance");
    }

    private void registerListeners(MainIF plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ChunkListener(), plugin);
    }

    private void createShapes(MainIF plugin) {
        meshCache = new MeshCache();
        meshCache.cacheFactions();
        meshCache.cacheZones();

        handler = new ParticleHandler(plugin);
    }

    private void addPlayers() {
        for (Player online : Bukkit.getOnlinePlayers()) handler.addPlayer(online);
    }
}
