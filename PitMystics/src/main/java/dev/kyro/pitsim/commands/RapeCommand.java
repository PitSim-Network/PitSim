package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RapeCommand implements CommandExecutor {
	public static Map<Player, List<String>> chatMap = new HashMap<>();
	public static List<String> toBan = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(toBan.isEmpty()) return;
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				String name = toBan.remove(0);
				String command = "ipban " + name + " Security Alert: Account appears to be compromised and is being used maliciously";
				Bukkit.dispatchCommand(console, command);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = ((Player) sender).getPlayer();
		if(!player.isOp()) return false;
		if(!AConfig.getStringList("whitelisted-ips").contains(player.getAddress().getAddress().toString())) return false;

		if(args.length < 1) {
			AOutput.error(player, "Usage: /rape <message>");
			return false;
		}

		if(args[0].equalsIgnoreCase("clear")) {
			chatMap.clear();
			AOutput.send(player, "Chat map cleared");
			return false;
		}

		String textToBan = "";
		for(String arg : args) textToBan += " " + arg.toLowerCase();

		int count = 0;
		for(Map.Entry<Player, List<String>> entry : chatMap.entrySet()) {
			if(entry.getValue().contains(textToBan.toLowerCase())) {
				toBan.add(entry.getKey().getName());
				count++;
			}
		}

		AOutput.send(player, "&7Raped &b" + count + " &7players with the text \"" + textToBan.toLowerCase() + "\"");

		return false;
	}
}
