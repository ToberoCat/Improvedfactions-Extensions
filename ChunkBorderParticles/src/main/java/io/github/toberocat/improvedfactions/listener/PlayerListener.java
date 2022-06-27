package io.github.toberocat.improvedfactions.listener;

import io.github.toberocat.improvedfactions.ChunkParticleBorderExtension;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void join(PlayerJoinEvent event) {
        ChunkParticleBorderExtension.handler.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        ChunkParticleBorderExtension.handler.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {

    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {

    }
}
