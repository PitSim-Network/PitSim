package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SetPrestigeCommand extends ASubCommand {
    public SetPrestigeCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {

        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

        try {
            pitPlayer.prestige = Integer.parseInt(args.get(0));
        } catch(Exception e) {
            AOutput.error(player, "&cPrestige set Failed!");
            return;
        }
        FileConfiguration playerData = APlayerData.getPlayerData(player);
        playerData.set("prestige", pitPlayer.prestige);

        pitPlayer.moonBonus = 0;
        playerData.set("moonbonus", pitPlayer.moonBonus);

        pitPlayer.goldStack = 0;
        playerData.set("goldstack", pitPlayer.goldStack);

        APlayerData.savePlayerData(player);
        AOutput.send(player, "&aSuccess!");
    }
}
