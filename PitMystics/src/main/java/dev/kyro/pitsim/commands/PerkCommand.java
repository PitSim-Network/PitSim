package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.PerkGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerkCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.megastreak.isOnMega()) {
			AOutput.error(player, "&cYou cannot use this command while on a megastreak!");
			return false;
		}

		PerkGUI perkGUI = new PerkGUI(player);
		perkGUI.open();

//		PerkPanel perkPanel = new PerkPanel(player);
//		player.openInventory(perkPanel.getInventory());
//		perkPanel.updateGUI();

		return false;
	}
}
