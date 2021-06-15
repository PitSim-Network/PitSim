package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.builders.AMessageBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnchantListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(args.length < 2) {

			AOutput.error(player, "Usage: /enchants <sword|bow|pants> <common|uncommon|rare|all>");
			return false;
		}

		switch(args[0].toLowerCase()) {

			case "sword":
			case "swords":
				listEnchants(player, MysticType.SWORD, args[1]);
				break;
			case "bow":
			case "bows":
				listEnchants(player, MysticType.BOW, args[1]);
				break;
			case "pant":
			case "pants":
				listEnchants(player, MysticType.PANTS, args[1]);
				break;
			default:
			AOutput.error(player, "Usage: /enchants <sword|bow|pants> <common|uncommon|rare|all>");
		}

		return false;
	}

	public static void listEnchants(Player player, MysticType mysticType, String rarity) {

		AMessageBuilder messageBuilder = new AMessageBuilder()
				.addLine("&bENCHANT LIST: (" + mysticType.displayName.toLowerCase() + ", " + rarity + ")");

		switch(rarity.toLowerCase()) {
			case "common":
				for(PitEnchant pitEnchant : EnchantManager.getEnchants(mysticType)) if(!pitEnchant.isUncommonEnchant && !pitEnchant.isRare)
						messageBuilder.addLine("&b * " + pitEnchant.getDisplayName());
				break;
			case "uncommon":
				for(PitEnchant pitEnchant : EnchantManager.getEnchants(mysticType)) if(pitEnchant.isUncommonEnchant && !pitEnchant.isRare)
					messageBuilder.addLine("&b * " + pitEnchant.getDisplayName());
				break;
			case "rare":
				for(PitEnchant pitEnchant : EnchantManager.getEnchants(mysticType)) if(pitEnchant.isRare)
					messageBuilder.addLine("&b * " + pitEnchant.getDisplayName());
				break;
			default:
				for(PitEnchant pitEnchant : EnchantManager.getEnchants(mysticType))
					messageBuilder.addLine("&b * " + pitEnchant.getDisplayName());
		}

		messageBuilder.colorize().send(player);
	}
}