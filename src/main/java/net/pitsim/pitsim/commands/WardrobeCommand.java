package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.megastreaks.StashStreaker;
import net.pitsim.pitsim.misc.Sounds;
import net.pitsim.pitsim.storage.EnderchestGUI;
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
