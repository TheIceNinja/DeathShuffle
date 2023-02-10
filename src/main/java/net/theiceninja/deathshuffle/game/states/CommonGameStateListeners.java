package net.theiceninja.deathshuffle.game.states;

import lombok.AllArgsConstructor;
import net.theiceninja.deathshuffle.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@AllArgsConstructor
public class CommonGameStateListeners implements Listener {

    private Game game;

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (game.getGameState() instanceof ActiveGameState) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (game.getGameState() instanceof ActiveGameState) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType().equals(Material.CHEST))
            event.setCancelled(true);
    }

    @EventHandler
    private void onItemPickUp(EntityPickupItemEvent event) {
        if (game.getGameState() instanceof ActiveGameState) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        if (game.getGameState() instanceof ActiveGameState) return;

        Player player = event.getPlayer();
        if (!game.isPlaying(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (game.getGameState() instanceof ActiveGameState) return;

        event.setCancelled(true);
    }
}
