package net.theiceninja.deathshuffle.game;

import lombok.Getter;
import lombok.Setter;
import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.game.states.CommonGameStateListeners;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GameState implements Listener {

    @Getter @Setter
    private Game game;

    public void onEnable(DeathShufflePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(new CommonGameStateListeners(game), plugin);
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
        HandlerList.unregisterAll(new CommonGameStateListeners(game));
    }
}
