package net.theiceninja.deathshuffle.commands.subcommands;

import lombok.AllArgsConstructor;
import net.theiceninja.deathshuffle.game.Game;
import net.theiceninja.deathshuffle.game.states.CooldownGameState;
import net.theiceninja.deathshuffle.game.states.DefaultGameState;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class StartSubCommand implements SubCommand {

    private Game game;

    @Override
    public void execute(Player player, String[] args) {
        if (Bukkit.getOnlinePlayers().size() <= 1) {
            player.sendMessage(ColorUtil.color("&cאין מספיק שחקנים בשרת."));
            return;
        }

        if (game.getSpawnLocation() == null || game.getSpectatorsLocation() == null) {
            player.sendMessage(ColorUtil.color("&cהספאונים של השחקנים לא מכוונים!"));
            return;
        }

        if (!(game.getGameState() instanceof DefaultGameState)) {
            player.sendMessage(ColorUtil.color("&cיש משחק פעיל כבר!"));
            return;
        }

        game.setState(new CooldownGameState());
    }

    @Override
    public String getName() {
        return "start";
    }
}
