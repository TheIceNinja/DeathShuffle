package net.theiceninja.deathshuffle.game;

import lombok.Getter;
import lombok.Setter;
import net.theiceninja.deathshuffle.DeathShufflePlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GameState implements Listener {

    @Getter @Setter
    private Game game;

    public void onEnable(DeathShufflePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
