package net.theiceninja.deathshuffle.commands;

import net.theiceninja.deathshuffle.game.Game;
import net.theiceninja.deathshuffle.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathShuffleCommand implements CommandExecutor {

    public DeathShuffleCommand(Game game) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.MUST_BE_PLAYER_ERROR);
            return true;
        }

        Player player = (Player) sender;

        // todo sub commands


        return true;
    }
}
