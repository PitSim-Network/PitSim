package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.TaintedWell;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.misc.BossBar;
import dev.kyro.pitsim.mobs.PitZombie;
import dev.kyro.pitsim.slayers.ZombieBoss;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		TaintedWell.onEnchant(player, player.getItemInHand());


//		try {
//			new ZombieBoss((Player) sender);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			AUtil.giveItemSafely((Player) sender, ZombieBoss.getBillionaire());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		((Player) sender).launchProjectile(Arrow.class);


//		new PitZombie(player.getLocation());
//		Bukkit.broadcastMessage(MobManager.mobs + "");

//		AOutput.send(sender, "Running dupe manager");
//		DupeManager.run();

		return false;
	}
}