package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.acosmetics.CosmeticManager;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnlockCosmeticCommand extends ACommand {
	public UnlockCosmeticCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(args.size() < 2) {
			AOutput.error(player, "&7Usage: /unlockcosmetic <player> <cosmetic> [color]");
			return;
		}

		Player target = null;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(!onlinePlayer.getName().equalsIgnoreCase(args.get(0))) continue;
			target = onlinePlayer;
			break;
		}
		if(target == null) {
			AOutput.error(player, "&7Could not find that player");
			return;
		}

		PitCosmetic pitCosmetic = CosmeticManager.getCosmetic(args.get(1));
		if(pitCosmetic == null) {
			AOutput.error(player, "&7Could not find that cosmetic");
			return;
		}

		if(pitCosmetic.isColorCosmetic && args.size() < 3) {
			AOutput.error(player, "&7Usage: /unlockcosmetic <player> <cosmetic> <color>");
			return;
		}
		RedstoneColor redstoneColor = null;
		if(pitCosmetic.isColorCosmetic) {
			redstoneColor = RedstoneColor.getRedstoneColor(args.get(2));
			if(redstoneColor == null) {
				AOutput.error(player, "&7That color does not exist");
				return;
			}
		}

		PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
		PitPlayer.UnlockedCosmeticData cosmeticData = pitTarget.unlockedCosmeticsMap.get(pitCosmetic.refName);
		if(cosmeticData != null) {
			if(!pitCosmetic.isColorCosmetic) {
				AOutput.error(player, "&7The player already has that cosmetic");
				return;
			} else if(cosmeticData.unlockedColors.contains(redstoneColor)) {
				AOutput.error(player, "&7The player already has that cosmetic in that color");
				return;
			}
		}

		CosmeticManager.unlockCosmetic(pitTarget, pitCosmetic, redstoneColor);
		if(pitCosmetic.isColorCosmetic) {
			AOutput.send(player, "&7Unlocked " + redstoneColor.displayName + "&7 " + pitCosmetic.getDisplayName() + "&7 for " + target.getName());
		} else {
			AOutput.send(player, "&7Unlocked " + pitCosmetic.getDisplayName() + "&7 for " + target.getName());
		}
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
