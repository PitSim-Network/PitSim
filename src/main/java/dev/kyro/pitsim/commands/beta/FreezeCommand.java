package dev.kyro.pitsim.commands.beta;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FreezeCommand extends ACommand {


	public FreezeCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		boolean remove = false;
		boolean effectingOther = false;
		for(String arg : args) {
			if(!arg.contains("-")) effectingOther = true;
			if(arg.equalsIgnoreCase("-r")) remove = true;
		}

		Player target = player;

		if(effectingOther) {
			String playerName = args.get(0);
			target = Bukkit.getPlayer(playerName);
			if(target == null) {
				AOutput.error(player, "&cPlayer not found!");
			}
		}

		if(target == null) return;

		if(remove) {
			AOutput.send(player, "&a&lSUCCESS!&7 Removed data freeze from &e" + target.getName() + "&7!");
			StorageManager.frozenPlayers.remove(target.getUniqueId());
		} else if(!StorageManager.frozenPlayers.contains(target.getUniqueId())) {
			AOutput.send(player, "&a&lSUCCESS!&7 Froze data for &e" + target.getName() + "&7!");
			StorageManager.frozenPlayers.add(target.getUniqueId());
		} else {
			AOutput.error(player, "&cData is already frozen for &e" + target.getName() + "&7!");
		}
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
