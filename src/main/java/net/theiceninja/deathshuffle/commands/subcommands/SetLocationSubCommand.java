package net.theiceninja.deathshuffle.commands.subcommands;

import lombok.AllArgsConstructor;
import net.theiceninja.deathshuffle.DeathShufflePlugin;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class SetLocationSubCommand implements SubCommand {

    private DeathShufflePlugin plugin;

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(ColorUtil.color("&cUsage: /deathshuffle <setlocation> <spawn|spectators>"));
            return;
        }

        if (args[1].equalsIgnoreCase("spawn")) {
            plugin.getConfig().set("game.spawn", player.getLocation());
            plugin.saveConfig();

            player.sendMessage(ColorUtil.color("&aהמיקום &2" + args[1] + " &aנשמר בהצלחה!"));
        } else if (args[1].equalsIgnoreCase("spectators")) {
            plugin.getConfig().set("game.spectators", player.getLocation());
            plugin.saveConfig();

            player.sendMessage(ColorUtil.color("&aהמיקום &2" + args[1] + " &aנשמר בהצלחה!"));
        } else {
            player.sendMessage(ColorUtil.color("&cUsage: /deathshuffle <setlocation> <locationName>"));
        }
    }

    @Override
    public String getName() {
        return "setlocation";
    }
}
