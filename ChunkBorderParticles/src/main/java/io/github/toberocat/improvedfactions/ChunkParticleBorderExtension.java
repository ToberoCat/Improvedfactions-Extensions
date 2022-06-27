package io.github.toberocat.improvedfactions;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.extensions.Extension;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.async.AsyncTask;
import io.github.toberocat.core.utility.settings.PlayerSettings;
import io.github.toberocat.core.utility.settings.type.EnumSetting;
import io.github.toberocat.improvedfactions.listener.ChunkListener;
import io.github.toberocat.improvedfactions.listener.FactionListener;
import io.github.toberocat.improvedfactions.listener.PlayerListener;
import io.github.toberocat.improvedfactions.mesh.MeshCache;
import io.github.toberocat.improvedfactions.particle.ParticleHandler;
import io.github.toberocat.improvedfactions.particle.ParticlePerformance;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class ChunkParticleBorderExtension extends Extension {
    public static int VISUALISATION_INTERVAL;
    public static boolean DISPLAY_UNCLAIMABLE;
    public static int MAX_PARTICLE_RENDER_DISTANCE;
    public static int PARTICLE_HEIGHT;
    public static int PARTICLE_HIDE_DST;
    public static Particle PARTICLE;

    public static final String PERFORMANCE_SETTING = "particle_performance";

    public static MeshCache meshCache;
    public static ParticleHandler handler;

    @Override
    protected void onEnable(MainIF plugin) {
        reloadConfigs();
        addPlayerSettings();

        AsyncTask.runLater(1, () -> {
            long start = System.currentTimeMillis();
            MainIF.logMessage(Level.INFO, "&aCaching particle meshes... this may take a while");
            createShapes(plugin);
            registerListeners(plugin);
            addPlayers();

            MainIF.logMessage(Level.INFO, String.format("&aCaching particle meshes finished. Took &6%d ms.&a Particles " +
                    "will now get spawned", System.currentTimeMillis() - start));
        });
    }

    @Override
    protected void onDisable(MainIF plugin) {
        handler.dispose();
        meshCache.dispose();
    }

    @Override
    public void reloadConfigs() {
        setConfigDefaultValue("interval", 25L);
        VISUALISATION_INTERVAL = configValue("interval");

        setConfigDefaultValue("display-unclaimable", true);
        DISPLAY_UNCLAIMABLE = configValue("display-unclaimable");

        setConfigDefaultValue("max-particle-render-distance", Bukkit.getViewDistance());
        MAX_PARTICLE_RENDER_DISTANCE = configValue("max-particle-render-distance");

        setConfigDefaultValue("particle-height", 1);
        PARTICLE_HEIGHT = configValue("particle-height");

        setConfigDefaultValue("hide-particle-dst", 10);
        PARTICLE_HIDE_DST = configValue("hide-particle-dst");

        setConfigDefaultValue("particle-type", "REDSTONE");
        PARTICLE = Particle.valueOf(configValue("particle-type"));
    }

    private void addPlayerSettings() {
        EnumSetting setting = new EnumSetting(ParticlePerformance.values(), PERFORMANCE_SETTING,
                Utility.createItem(Material.SUGAR, "&eParticle performance"));
        setting.setUpdate((player, selected) -> handler.update(player, selected));
        PlayerSettings.add(setting);
    }

    private void registerListeners(MainIF plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ChunkListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FactionListener(), plugin);

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
