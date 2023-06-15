package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.enums.MysticType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FreshCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!PitSim.isDev()) {
			if(!player.hasPermission("group.nitro")) {
				AOutput.send(player, "&cYou must boost our discord server to gain access to this feature!&7 Join with: &f&ndiscord.pitsim.net");
				return false;
			}
		}

		if(args.length < 1) {
			AOutput.error(player, "Usage: /fresh <sword|bow|fresh>");
			return false;
		}

		String type = args[0].toLowerCase();
		ItemStack mystic = MysticFactory.getFreshItem(player, type);
		if(mystic == null) {
			AOutput.error(player, "Usage: /fresh <sword|bow|fresh>");
			return false;
		}

		if(!PitSim.isDev() && !player.isOp()) {
			if(MysticType.getMysticType(mystic) == MysticType.TAINTED_CHESTPLATE || MysticType.getMysticType(mystic) == MysticType.TAINTED_SCYTHE) {
				AOutput.error(player, "&cNice try.");
				return false;
			}
		}

		AUtil.giveItemSafely(player, mystic);
		return false;
	}
}
