package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.CustomSerializer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		new PluginMessage()
				.writeString("UBERDROP")
				.writeString(PitSim.serverName)
				.writeString(Misc.getDisplayName(player))
				.writeString(CustomSerializer.serialize(player.getItemInHand()))
				.send();

		return false;
	}
}