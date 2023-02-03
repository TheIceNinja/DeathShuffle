package net.theiceninja.deathshuffle.game.states;

import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.game.GameState;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import net.theiceninja.deathshuffle.utils.Messages;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DefaultGameState extends GameState {

    @Override
    public void onEnable(DeathShufflePlugin plugin) {
        super.onEnable(plugin);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(ColorUtil.color(
                Messages.PREFIX + "&a" + player.getDisplayName()
                + " &2נכנס לשרת!"
        ));

        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ColorUtil.color(
                Messages.PREFIX + "&a" + player.getDisplayName()
                        + " &cיצא מהשרת!"
        ));
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType().equals(Material.CHEST))
            event.setCancelled(true);
    }

    @EventHandler
    private void onItemPickUp(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }
}
