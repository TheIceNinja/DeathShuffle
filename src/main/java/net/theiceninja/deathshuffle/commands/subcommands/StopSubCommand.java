package net.theiceninja.deathshuffle.commands.subcommands;

import lombok.AllArgsConstructor;
import net.theiceninja.deathshuffle.game.Game;
import net.theiceninja.deathshuffle.game.states.ActiveGameState;
import net.theiceninja.deathshuffle.game.states.DefaultGameState;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class StopSubCommand implements SubCommand {

    private Game game;

    @Override
    public void execute(Player player, String[] args) {
        if (!(game.getGameState() instanceof ActiveGameState)) {
            player.sendMessage(ColorUtil.color("&cאי אפשר לעצור את המשחק כשהוא לא פועל.."));
            return;
        }

        game.setState(new DefaultGameState());
    }

    @Override
    public String getName() {
        return "stop";
    }
}
