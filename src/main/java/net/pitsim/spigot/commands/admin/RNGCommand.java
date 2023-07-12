package net.pitsim.spigot.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.ChatTriggerManager;
import net.pitsim.spigot.controllers.PerkManager;
import net.pitsim.spigot.controllers.objects.Megastreak;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.megastreaks.RNGesus;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RNGCommand extends ACommand {
	public RNGCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		if(!player.hasPermission("pitsim.rng")) return;

		if(args.size() < 1) {
			AOutput.error(player, "Usage: /rng <player>");
			return;
		}

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getDisplayName().equalsIgnoreCase(args.get(0))) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
				Megastreak megastreak = PerkManager.getMegastreak("rngesus");
				PitPlayer.MegastreakLimit limit = pitPlayer.getMegastreakCooldown(megastreak);
				limit.forceReset(pitPlayer);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccess!"));
			}
		}
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
