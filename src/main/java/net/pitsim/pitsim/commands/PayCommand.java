package net.pitsim.pitsim.commands;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Lang;
import net.pitsim.pitsim.upgrades.TheWay;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class PayCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		new BukkitRunnable() {
			@Override
			public void run() {
				if(args.length < 2) {
					AOutput.error(player, "&c&lERROR!&7 Usage: /pay <player> <amount>");
					return;
				}

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				int levelRequired = 100 - TheWay.INSTANCE.getLevelReduction(pitPlayer.player);
				if(pitPlayer.level < levelRequired && !player.isOp()) {
					AOutput.error(player, "&c&lERROR!&7 You cannot trade until level " + levelRequired);
					return;
				}

				Player target = null;
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(!onlinePlayer.getName().equalsIgnoreCase(args[0])) continue;
					if(VanishAPI.isInvisible(onlinePlayer)) continue;
					target = onlinePlayer;
					break;
				}
				if(target == null) {
					Lang.COULD_NOT_FIND_PLAYER_WITH_NAME.send(player);
					return;
				} else if(target == player) {
					AOutput.error(player, "&c&lERROR!&7 You cannot pay yourself");
					return;
				}

				PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
				int levelRequiredTarget = 100 - TheWay.INSTANCE.getLevelReduction(pitTarget.player);
				if(pitTarget.level < levelRequiredTarget && !player.isOp()) {
					AOutput.error(player, "&c&lERROR!&7 That player is not level " + levelRequiredTarget);
					return;
				}

				int amount;
				try {
					amount = Integer.parseInt(args[1]);
					if(amount <= 0) throw new IllegalArgumentException();
				} catch(Exception ignored) {
					AOutput.error(player, "Invalid amount of money");
					return;
				}

				if(amount > pitPlayer.gold) {
					AOutput.error(player, "You do not have enough money");
					return;
				}

				pitPlayer.gold -= amount;
				pitTarget.gold += amount;
				DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0");
				AOutput.send(player, "&6&lTRADE!&7 You have sent &6" + target.getName() + " &7$" + decimalFormat.format(amount));
				AOutput.send(target, "&6&lTRADE!&7 You have received $" + decimalFormat.format(amount) + " from &7" + player.getName());
			}
		}.runTask(PitSim.INSTANCE);
		return false;
	}
}