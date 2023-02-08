package net.theiceninja.deathshuffle.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.game.states.ActiveGameState;
import net.theiceninja.deathshuffle.game.states.CooldownGameState;
import net.theiceninja.deathshuffle.game.states.DefaultGameState;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import net.theiceninja.deathshuffle.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class Game {

    @Setter
    private int rounds = 1;

    private final List<UUID> players  = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();

    private final Map<UUID, EntityDamageEvent.DamageCause> taskForPlayer = new HashMap<>();

    private GameState gameState;
    private final DeathShufflePlugin plugin;

    public void addPlayer(Player player) {
        this.players.add(player.getUniqueId());

        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);

        player.teleport(getSpawnLocation());
        updateScoreBoard();
    }

    public void removePlayer(Player player) {
        if (isPlaying(player)) {
            this.players.remove(player.getUniqueId());
        }

        this.spectators.add(player.getUniqueId());
        updateScoreBoard();

        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.playSound(player, Sound.ENTITY_BLAZE_HURT, 1, 1);
        player.teleport(getSpectatorsLocation());

        sendMessage("&4" + player.getDisplayName() + " &cלא השלים את ההאתגר שלו ונפסל.");
        if (players.size() == 1) {
            Player winner = Bukkit.getPlayer(players.get(0));
            sendMessage("&aהמנצח של המשחק הוא &2&l" + winner.getDisplayName());
            sendTitle("&aהמנצח של המשחק הוא &2&l" + winner.getDisplayName());

            setState(new DefaultGameState());
        } else if (players.isEmpty()) {
            sendMessage("&cאין מנצח למשחק, המשחק נגמר!");

            setState(new DefaultGameState());
        }
    }

    public boolean isPlaying(Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public boolean isSpectating(Player player) {
        return spectators.contains(player.getUniqueId());
    }

    public void pickRandomDeath(Player player) {
        int randomNumber = (int) randomizer(-1, EntityDamageEvent.DamageCause.values().length + 1);
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

        player.sendMessage(ColorUtil.color("&6https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html"));
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

            player.sendTitle(ColorUtil.color("&#FA1B00&lDeath&#FAA700&lShuffle"), ColorUtil.color(message), 0, 40, 0);
        }

        for (UUID playerUUID : this.spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.sendTitle(ColorUtil.color("&#FA1B00&lDeath&#FAA700&lShuffle"), ColorUtil.color(message), 0, 40, 0);
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

    private void setScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        List<String> scoreboardLines = new ArrayList<>();

        Objective objective = scoreboard.registerNewObjective("ice",
                "dummy",
                ColorUtil.color("&#3bb6fb&lN&#4bbce7&li&#5bc3d3&ln&#6bc9be&lj&#7bd0aa&la&#8bd696&lN&#9bdd82&le&#abe36e&lt&#bbea5a&lw&#cbf045&lo&#dbf731&lr&#ebfd1d&lk &7| &fמוות מתחלף"));
        scoreboardLines.add("&r");

        if (getGameState() instanceof CooldownGameState) {
            CooldownGameState cooldownGameState = (CooldownGameState) getGameState();
            if (cooldownGameState.getCooldownTask() == null) return;

            scoreboardLines.add("&fהמשחק מתחיל בעוד&8: &e" + cooldownGameState.getCooldownTask().getTimeLeft());
        } else if (getGameState() instanceof ActiveGameState) {
            scoreboardLines.add("&f ");
            scoreboardLines.add("&fמצבך במשחק&8: " + (isPlaying(player) ? "&aחי" : "&7צופה"));
            scoreboardLines.add("&r");
            scoreboardLines.add("&fכמות הסיבובים&8: &2" +  getRounds());

            scoreboardLines.add("&r");
            scoreboardLines.add("&fהמשימה שלך&8: &b&l" + (getDeath(player) == null ? "&cאין לך משימה!" : getDeath(player).toString()));
            ActiveGameState activeGameState = (ActiveGameState) getGameState();
            if (activeGameState.getTaskCooldown() == null) return;

            scoreboardLines.add("&fזמן שנותר למשימה&8: &e" + activeGameState.getTaskCooldown().getTimeLeft() / 60 + "&8:&e" + activeGameState.getTaskCooldown().getTimeLeft() % 60);
        }

        scoreboardLines.add("&f");
        scoreboardLines.add("&fשחקנים שיש במשחק&8: &6" + players.size());

        scoreboardLines.add("&r ");
        scoreboardLines.add("&7play.iceninja.us.to");

        for (int i = 0; i < scoreboardLines.size(); i++) {
            String line = ColorUtil.color(scoreboardLines.get(i));
            objective.getScore(line).setScore(scoreboardLines.size() - i);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void updateScoreBoard() {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            setScoreboard(player);
        }

        for (UUID playerUUID : spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            setScoreboard(player);
        }
    }

    public void playsound(Sound sound) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.playSound(player, sound, 1, 1);
        }

        for (UUID playerUUID : spectators) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.playSound(player, sound, 1, 1);
        }
    }
}
