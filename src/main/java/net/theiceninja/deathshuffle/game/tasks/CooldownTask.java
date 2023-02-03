package net.theiceninja.deathshuffle.game.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.theiceninja.deathshuffle.game.Game;
import net.theiceninja.deathshuffle.game.states.ActiveGameState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CooldownTask extends BukkitRunnable {

    private final Game game;
    @Getter
    private int timeLeft = 11;

    @Override
    public void run() {
        timeLeft--;
        if (timeLeft <= 0) {
            cancel();

            game.setState(new ActiveGameState());
            game.playsound(Sound.BLOCK_NOTE_BLOCK_BIT);
            return;
        }

        game.sendTitle("&eהמשחק מתחיל בעוד&8: &b" + timeLeft);
        game.updateScoreBoard();
        game.playsound(Sound.BLOCK_NOTE_BLOCK_PLING);
    }
}
