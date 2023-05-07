package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.holograms.Hologram;
import dev.kyro.pitsim.holograms.RefreshMode;
import dev.kyro.pitsim.holograms.ViewMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;


		Hologram hologram = new Hologram(player.getLocation(), ViewMode.ALL, RefreshMode.MANUAL) {
			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				strings.add("&bTest");
				strings.add("&3eeeeeee");

				return strings;
			}
		};


		return false;
	}
}










