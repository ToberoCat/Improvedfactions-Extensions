package io.github.toberocat.improvedfactions.listener;

import io.github.toberocat.core.utility.events.faction.FactionDeleteEvent;
import io.github.toberocat.improvedfactions.ChunkParticleBorderExtension;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionListener implements Listener {
    @EventHandler
    public void delete(FactionDeleteEvent event) {
        ChunkParticleBorderExtension.meshCache.removeCache(event.getFaction().getRegistryName());
    }
}
