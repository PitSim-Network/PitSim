package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.CombatManager;
import net.pitsim.pitsim.controllers.DamageManager;
import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.controllers.SpawnManager;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OofCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(player.getWorld() == MapManager.getDarkzone() && !player.isOp() && !PitSim.isDev()) {
			AOutput.send(player, "&c&lERROR!&7 You can't in the darkzone!");
			return false;
		}

		if(SpawnManager.isInSpawn(player) && !player.isOp()) {
			AOutput.send(player, "&c&lERROR!&7 You can't /oof in spawn!");
			Sounds.ERROR.play(player);
			return false;
		}

		if(!CombatManager.taggedPlayers.containsKey(player.getUniqueId())) {
			DamageManager.killPlayer(player);
			return false;
		}

		DamageManager.killPlayer(player);
		return false;
	}
}
