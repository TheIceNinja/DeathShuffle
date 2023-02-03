package net.theiceninja.deathshuffle.game.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.theiceninja.deathshuffle.game.Game;
import net.theiceninja.deathshuffle.game.states.ActiveGameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerTaskCooldown extends BukkitRunnable {

    private final Game game;

    @Getter
    private int timeLeft = 60 * 5;

    @Override
    public void run() {
        timeLeft--;

        if (timeLeft <= 0) {
            cancel();

            if (game.getPlayers().size() != game.getTaskForPlayer().size()) {
                for (UUID playerUUID : game.getTaskForPlayer().keySet()) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player == null) return;

                    game.removePlayer(player);
                }
            }

            startGame();

            return;
        }

        if (game.getTaskForPlayer().isEmpty()) {
            cancel();
            startGame();
            return;
        }

        game.updateScoreBoard();
        game.sendActionBar("&fהסיבוב נגמר בעוד&8: &e" + timeLeft / 60 + "&8:&e" + timeLeft % 60);
    }

    private void startGame() {
        game.getTaskForPlayer().clear();

        if (game.getPlayers().size() == 1 || game.getPlayers().size() == 0) return;

        for (UUID playerUUID : game.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) return;

            game.pickRandomDeath(player);
        }

        ActiveGameState activeGameState = (ActiveGameState) game.getGameState();
        activeGameState.setTaskCooldown(new PlayerTaskCooldown(game));

        activeGameState.getTaskCooldown().runTaskTimer(game.getPlugin(), 0, 20);
        game.setRounds(game.getRounds() + 1);
    }
}
