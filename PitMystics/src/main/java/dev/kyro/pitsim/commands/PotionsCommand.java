package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PotionsCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(PotionManager.getPotionEffects(player).isEmpty()) {
			AOutput.send(player, "&5&lPOTION!&7 No active potion effects!");
			return false;
		}

		AOutput.send(player, "&d&m----------&d<&5&lPOTIONS&d>&m----------");
		for(PotionEffect potionEffect : PotionManager.getPotionEffects(player)) {
			AOutput.send(player, "&d * &5" + potionEffect.potionType.color + potionEffect.potionType.name + " " +
					AUtil.toRoman(potionEffect.potency.tier) + ": &f" + Misc.ticksToTime(potionEffect.ticksLeft));
		}
		AOutput.send(player, "&d&m----------&d<&5&lPOTIONS&d>&m----------");

		return false;
	}
}
