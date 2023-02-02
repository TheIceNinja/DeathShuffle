package net.theiceninja.deathshuffle.game.tasks;

import lombok.RequiredArgsConstructor;
import net.theiceninja.deathshuffle.game.Game;
import net.theiceninja.deathshuffle.game.states.ActiveGameState;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CooldownTask extends BukkitRunnable {

    private final Game game;
    private int timeLeft = 11;

    @Override
    public void run() {
        timeLeft--;
        if (timeLeft <= 0) {
            cancel();
            game.setState(new ActiveGameState());
            return;
        }

        game.sendTitle("&eהמשחק מתחיל בעוד&8: &b" + timeLeft);
    }
}
