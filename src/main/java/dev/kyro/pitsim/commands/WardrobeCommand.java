package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.megastreaks.StashStreaker;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.storage.EnderchestGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WardrobeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(!player.isOp() && !PitSim.isDev()) {
			if(!(pitPlayer.getMegastreak() instanceof StashStreaker) || !pitPlayer.isOnMega()) {
				AOutput.error(player, "&c&lERROR!&7 You must be on " + StashStreaker.INSTANCE.getCapsDisplayName() + "&7 to use this!");
				Sounds.NO.play(player);
				return false;
			}
		}

		EnderchestGUI enderchestGUI = new EnderchestGUI(player, player.getUniqueId());
		enderchestGUI.enderchestPanel.openPanel(enderchestGUI.wardrobePanel);
		return false;
	}
}
