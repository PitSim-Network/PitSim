package dev.kyro.pitsim.commands.market;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.pitsim.inventories.AuctionGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AuctionCommand extends ASubCommand {

	public AuctionCommand(String executor) {
		super(executor);
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		Player player = (Player) sender;

		player.openInventory(new AuctionGUI(player).getInventory());
	}
}
