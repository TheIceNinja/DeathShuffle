package net.theiceninja.deathshuffle.commands.subcommands;

import lombok.AllArgsConstructor;
import net.theiceninja.deathshuffle.game.Game;
import net.theiceninja.deathshuffle.game.states.ActiveGameState;
import net.theiceninja.deathshuffle.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ReviveSubCommand implements SubCommand {

    private Game game;

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(ColorUtil.color("&cאתה צריך לציין את השחקן שאתה רוצה להחיות."));
            return;
        }

        if (!(game.getGameState() instanceof ActiveGameState)) {
            player.sendMessage(ColorUtil.color("&cאתה לא יכול להחיות מישהו כשהמשחק לא פעיל."));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ColorUtil.color("&cשחקן זה לא קיים במערכת של המשחק."));
            return;
        }

        if (!game.isSpectating(target)) {
            player.sendMessage(ColorUtil.color("&cשחקן זה כבר חי במשחק!"));
        }

        game.addPlayer(target);
        game.sendMessage("&e&l" + target.getDisplayName() + " &b&lחזר למשחק!");
    }

    @Override
    public String getName() {
        return "revive";
    }
}
