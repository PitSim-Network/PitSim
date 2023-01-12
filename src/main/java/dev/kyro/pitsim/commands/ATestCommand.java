package dev.kyro.pitsim.commands;

import com.sk89q.worldguard.util.task.progress.Progress;
import dev.kyro.pitsim.adarkzone.progression.ProgressionGUI;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.inventories.AdminGUI;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		PluginMessage message = new PluginMessage().writeString("CREATE LISTING");
		message.writeString(player.getUniqueId().toString()).writeString(StorageProfile.serialize(player, player.getItemInHand()));
		message.writeInt(0).writeInt(100).writeBoolean(false).writeLong(1000 * 60).send();
		return false;
	}
}