package net.theiceninja.deathshuffle.game;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.game.states.DefaultGameState;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import net.theiceninja.deathshuffle.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

@Getter
public class Game {

    private final List<UUID> players  = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();

    private int rounds;

    private final Map<UUID, EntityDamageEvent.DamageCause> taskForPlayer = new HashMap<>();
    private GameState gameState;

    private final DeathShufflePlugin plugin;

    public Game(DeathShufflePlugin plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        this.players.add(player.getUniqueId());

        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    public void removePlayer(Player player) {
        if (!isPlaying(player)) return;

        // todo do something if the size is 0
        this.players.remove(player.getUniqueId());
        this.spectators.add(player.getUniqueId());

        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);

        sendMessage("&4" + player.getDisplayName() + " &cלא השלים את ההאתגר שלו ונפסל.");
        if (players.size() == 1) {
            Player winner = Bukkit.getPlayer(players.get(0));
            sendMessage("&aהמנצח של המשחק הוא &2&l" + winner);
            sendTitle("&aהמנצח של המשחק הוא &2&l" + winner);

            setState(new DefaultGameState());
        }
    }

    public boolean isPlaying(Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public void pickRandomDeath(Player player) {
        int randomNumber = (int) randomizer(-1, EntityDamageEvent.DamageCause.values().length);
        this.taskForPlayer.put(player.getUniqueId(), EntityDamageEvent.DamageCause.values()[randomNumber]);

        player.sendMessage(ColorUtil.color("&eאתה צריך להשלים את המשימה&8: " + getDeath(player).toString()));
    }

    public EntityDamageEvent.DamageCause getDeath(Player player) {
        return this.taskForPlayer.get(player.getUniqueId());
    }

    private double randomizer(int a, int b) {
        return (double) b + (Math.random() * (a - b + 1));
    }

    public void setState(GameState gameState) {
        if (this.gameState != null) this.gameState.onDisable();

        this.gameState = gameState;
        gameState.setGame(this);
        gameState.onEnable(plugin);
    }

    public void sendMessage(String message) {
        for (UUID playerUUID : this.players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendMessage(ColorUtil.color(Messages.PREFIX + message));
        }

        for (UUID playerUUID : this.spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendMessage(ColorUtil.color(Messages.PREFIX + message));
        }
    }

    public void sendTitle(String message) {
        for (UUID playerUUID : this.players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendTitle(ColorUtil.color("&c&lDeath&e&lShuffle"), ColorUtil.color(message), 0, 40, 0);
        }

        for (UUID playerUUID : this.spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendTitle(ColorUtil.color("&c&lDeath&e&lShuffle"), ColorUtil.color(message), 0, 40, 0);
        }
    }

    public void sendActionBar(String str) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtil.color(str)));
        }

        for (UUID playerUUID : spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtil.color(str)));
        }
    }

}
