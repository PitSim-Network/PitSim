package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SetLevelCommand extends ASubCommand {
    public SetLevelCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {

        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

        try {
            pitPlayer.level = Integer.parseInt(args.get(0)) - 1;
            pitPlayer.remainingXP = 0;
            LevelManager.incrementLevel(player);
        } catch(Exception e) {
            AOutput.error(player, "&cLevel set Failed!");
            return;
        }
        FileConfiguration playerData = APlayerData.getPlayerData(player);
        playerData.set("level", pitPlayer.level);
        APlayerData.savePlayerData(player);
        AOutput.send(player, "&aSuccess!");
    }
}
