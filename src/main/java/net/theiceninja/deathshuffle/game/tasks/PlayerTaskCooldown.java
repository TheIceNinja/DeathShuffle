package net.theiceninja.deathshuffle.game.tasks;

import lombok.RequiredArgsConstructor;
import net.theiceninja.deathshuffle.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerTaskCooldown extends BukkitRunnable {

    private final Game game;
    private int timeLeft = 60 * 3;

    @Override
    public void run() {
        timeLeft--;
        if (timeLeft <= 0) {

            for (UUID playerUUID : game.getTaskForPlayer().keySet()) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null) return;

                game.removePlayer(player);
            }

            game.getTaskForPlayer().clear();

            for (UUID playerUUID : game.getPlayers()) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null) return;

                game.pickRandomDeath(player);
            }

            return;
        }

        game.sendActionBar("&fהסיבוב נגמר בעוד&8: &e" + timeLeft / 60 + "&8:&e" + timeLeft % 60);
    }
}
