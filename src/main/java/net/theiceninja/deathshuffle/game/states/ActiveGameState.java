package net.theiceninja.deathshuffle.game.states;

import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ActiveGameState extends GameState {

    @Override
    public void onEnable(DeathShufflePlugin plugin) {
        super.onEnable(plugin);

        getGame().sendMessage("&e&lהמשחק התחיל!");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        for (UUID playerUUID : getGame().getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) return;

            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(20);
        }

        for (UUID playerUUID : getGame().getSpectators()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) return;

            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(20);
        }

        getGame().getPlayers().clear();
        getGame().getSpectators().clear();
    }
}
