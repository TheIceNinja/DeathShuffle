package net.theiceninja.deathshuffle;

import net.theiceninja.deathshuffle.commands.DeathShuffleCommand;
import net.theiceninja.deathshuffle.game.Game;
import net.theiceninja.deathshuffle.game.states.DefaultGameState;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import net.theiceninja.deathshuffle.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathShufflePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Game game = new Game(this);

        // set the state to default (enable listeners)
        game.setState(new DefaultGameState());

        getCommand("deathshuffle").setExecutor(new DeathShuffleCommand(game));
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color(Messages.PREFIX + "&aenabled!"));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ColorUtil.color(Messages.PREFIX + "&cdisabled!"));
    }
}
