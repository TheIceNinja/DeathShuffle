package net.theiceninja.deathshuffle.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.game.states.DefaultGameState;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import net.theiceninja.deathshuffle.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class Game {

    private final List<UUID> players  = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();

    @Setter
    private int rounds = 1;

    private final Map<UUID, EntityDamageEvent.DamageCause> taskForPlayer = new HashMap<>();
    private GameState gameState;

    private final DeathShufflePlugin plugin;

    public void addPlayer(Player player) {
        this.players.add(player.getUniqueId());

        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);

        player.teleport(getSpawnLocation());
    }

    public void removePlayer(Player player) {
        if (isPlaying(player)) {
            this.players.remove(player.getUniqueId());
        }

        // todo do something if the size is 0
        this.spectators.add(player.getUniqueId());

        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(getSpectatorsLocation());

        sendMessage("&4" + player.getDisplayName() + " &cלא השלים את ההאתגר שלו ונפסל.");
        if (players.size() == 1) {
            Player winner = Bukkit.getPlayer(players.get(0));
            sendMessage("&aהמנצח של המשחק הוא &2&l" + winner.getDisplayName());
            sendTitle("&aהמנצח של המשחק הוא &2&l" + winner.getDisplayName());

            setState(new DefaultGameState());
        } else if (players.size() == 0) {
            sendMessage("&cאין מנצח למשחק, המשחק נגמר!");

            setState(new DefaultGameState());
        }
    }

    public boolean isPlaying(Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public void pickRandomDeath(Player player) {
        int randomNumber = (int) randomizer(-1, EntityDamageEvent.DamageCause.values().length);
        this.taskForPlayer.put(player.getUniqueId(), EntityDamageEvent.DamageCause.values()[randomNumber]);

        player.sendMessage(ColorUtil.color("&eאתה צריך להשלים את המשימה&8: &b&l" + getDeath(player).toString()));
        player.sendMessage(ColorUtil.color(
                "&fאם אתה לא יודע מה המשימה שלך אז הנה דף הסבר&8:\n" +
                    "&b&lCONTACT = &eמוות מקקטוס או מדריפסטון או מדובדבנים" + "\n" +
                    "&b&lHOT-FLOOR = &eמגמה בלוק" + "\n" +
                    "&b&lSONIC_BOOM = &eמהוורדן" + "\n" +
                    "&b&lSUICIDE = &eתתאבד ותחשוב טוב" + "\n" +
                    "&b&lDRYOUT = &eלמות ממוב שהוא לא במים"
        ));
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

    public Location getSpawnLocation() {
        return plugin.getConfig().getLocation("game.spawn");
    }

    public Location getSpectatorsLocation() {
        return plugin.getConfig().getLocation("game.spectators");
    }
}
