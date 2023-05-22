package dev.kyro.pitsim.commands;

import dev.kyro.arcticguilds.Guild;
import dev.kyro.arcticguilds.GuildManager;
import dev.kyro.pitsim.controllers.OutpostManager;
import dev.kyro.pitsim.controllers.objects.OutpostBanner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		Guild guild = GuildManager.getGuild(player);
		if(guild == null) return false;
		OutpostBanner banner = OutpostManager.banner;
		banner.setBanner(guild);
		banner.setPercent(Integer.parseInt(args[0]));

		return false;
	}
}










