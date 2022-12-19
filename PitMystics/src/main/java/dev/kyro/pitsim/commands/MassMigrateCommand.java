package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.ProxyMessaging;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class MassMigrateCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

		if(!(commandSender instanceof Player)) return false;
		Player player = ((Player) commandSender);

		if(!player.isOp()) return false;

		File dataFolder = new File("plugins/PitRemake/playerdata");
		File[] files = dataFolder.listFiles();

		System.out.println("Starting Data Migration Process");
		long startTime = System.currentTimeMillis();

		int i = 1;

		assert files != null;
		for(File file : files) {
			String fileName = file.getName().substring(0, file.getName().length() - 4);

			System.out.println("Migrating Player: " + fileName + " (" + i + "/" + files.length + ")");
			UUID uuid = UUID.fromString(fileName);
			ProxyMessaging.migrate(uuid);
			i++;
		}

		long endTime = System.currentTimeMillis();
		long diff = endTime - startTime;
		System.out.println("Migration process completed in " + diff + "ms");

		return false;
	}
}
