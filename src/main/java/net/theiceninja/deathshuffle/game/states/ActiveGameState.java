package net.theiceninja.deathshuffle.game.states;

import lombok.Getter;
import lombok.Setter;
import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.game.GameState;
import net.theiceninja.deathshuffle.game.tasks.PlayerTaskCooldown;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.UUID;

public class ActiveGameState extends GameState {

    @Getter @Setter
    private PlayerTaskCooldown taskCooldown;

    @Override
    public void onEnable(DeathShufflePlugin plugin) {
        super.onEnable(plugin);

        getGame().updateScoreBoard();
        getGame().sendMessage("&e&lהמשחק התחיל!");
        if (this.taskCooldown != null) this.taskCooldown.cancel();

        this.taskCooldown = new PlayerTaskCooldown(getGame());
        this.taskCooldown.runTaskTimer(plugin, 0, 20);

        for (UUID playerUUID : getGame().getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) return;

            getGame().pickRandomDeath(player);
        }

    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.taskCooldown != null) this.taskCooldown.cancel();

        getGame().getTaskForPlayer().clear();

        getGame().sendMessage("&cהמשחק נגמר...");
        for (UUID playerUUID : getGame().getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) return;

            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(20);

            player.getInventory().clear();
            player.teleport(getGame().getSpawnLocation());
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        for (UUID playerUUID : getGame().getSpectators()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) return;

            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(20);

            player.getInventory().clear();
            player.teleport(getGame().getSpawnLocation());
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        getGame().getPlayers().clear();
        getGame().getSpectators().clear();
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        if (this.taskCooldown == null) return;

        event.setDeathMessage(null);
        Player player = event.getEntity();
        if (!getGame().isPlaying(player)) return;

        EntityDamageEvent damageEvent = player.getLastDamageCause();
        if (damageEvent.getCause().equals(getGame().getDeath(player))) {
            getGame().getTaskForPlayer().remove(player.getUniqueId());
            getGame().sendMessage("&2&l" + player.getDisplayName() + " &aהשלים את המשימה שלו!");
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();

        getGame().removePlayer(player);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();

        getGame().removePlayer(player);
    }
}
