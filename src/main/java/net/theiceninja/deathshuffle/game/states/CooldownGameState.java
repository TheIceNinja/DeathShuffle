package net.theiceninja.deathshuffle.game.states;

import lombok.Getter;
import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.game.GameState;
import net.theiceninja.deathshuffle.game.tasks.CooldownTask;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import org.bukkit.Bukkit;
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

public class CooldownGameState extends GameState {

    @Getter
    private CooldownTask cooldownTask;

    @Override
    public void onEnable(DeathShufflePlugin plugin) {
        super.onEnable(plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            getGame().addPlayer(player);
        }

        getGame().sendMessage("&aהמשחק עכשיו עומד להתחיל...");

        if (this.cooldownTask != null) this.cooldownTask.cancel();

        this.cooldownTask = new CooldownTask(getGame());
        this.cooldownTask.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.cooldownTask != null) this.cooldownTask.cancel();
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);
        if (!getGame().isPlaying(player)) return;

        // remove the player in var and not method (no-spectator)
        getGame().getPlayers().remove(player.getUniqueId());
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);

        player.kickPlayer(ColorUtil.color("&c&lDeath&e&lShuffle\n&cהמשחק כבר התחיל!"));
    }
}
