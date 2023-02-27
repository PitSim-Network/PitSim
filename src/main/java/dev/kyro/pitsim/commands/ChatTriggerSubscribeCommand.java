package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ChatTriggerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatTriggerSubscribeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.isOp()) {
			if(args.length < 1 || !args[0].equalsIgnoreCase("thisisjustheretomakesureyoudontturnitononaccident")) return false;
		}

		if(ChatTriggerManager.isSubscribed(player)) {
			AOutput.send(player, ChatTriggerManager.PREFIX + "You already have this enabled");
			return false;
		}

		ChatTriggerManager.subscribePlayer(player);
		AOutput.send(player, ChatTriggerManager.PREFIX + "Subscribed to receive a shit ton of data");
		return false;
	}
}
