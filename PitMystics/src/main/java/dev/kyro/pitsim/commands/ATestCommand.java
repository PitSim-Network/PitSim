package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.controllers.AuctionManager;
import dev.kyro.pitsim.controllers.CutsceneManager;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.TaintedWell;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.inventories.PerkGUI;
import dev.kyro.pitsim.inventories.TaintedGUI;
import dev.kyro.pitsim.misc.BossBar;
import dev.kyro.pitsim.mobs.PitZombie;
import dev.kyro.pitsim.slayers.ZombieBoss;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ATestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.isOp()) return false;

//		AuctionManager.auctionItems[0].addBid(player.getUniqueId(), AuctionManager.auctionItems[0].getBid(player.getUniqueId()) + 100);

		CutsceneManager.play(player);

//		TaintedGUI taintedGUI = new TaintedGUI(player);
//		taintedGUI.open();
//
//		for (BrewingIngredient ingredient : BrewingIngredient.ingredients) {
//			AUtil.giveItemSafely(player, ingredient.getItem());
//		}
//
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//
//				int radius = 2;
//				int pointsTheta = 60;
//				int pointsPhi = 360;
//
//				double theta = 0;
//				double phi = 0;
//
//				double thetaRand = 360 * Math.random();
//				double phiRand = 360 * Math.random();
//
//
//				for (int i = 0; i < 512; i++) {
//					//Do whatever with angle
//					double x = Math.cos(theta);
//					x *= radius;
//					double y = Math.sin(theta);
//					y *= radius;
//					double x2 = radius * Math.cos(phiRand) * Math.sin(thetaRand);
//					double z2 = radius * Math.sin(phiRand) * Math.sin(thetaRand);
//					double y2 = radius * Math.cos(thetaRand);
//
//					player.getWorld().spigot().playEffect(player.getLocation().add(x2, y2 + 1, z2), Effect.COLOURED_DUST, 0, 0, (float) -1, (float) 0, (float) Math.random(), 1, 0, 64);
////					theta += (360 / pointsTheta);
////					phi += (360 / pointsPhi);
//
//					thetaRand = 360 * Math.random();
//					phiRand = 360 * Math.random();
//				}
//
//
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 5, 5);

		return false;
	}
}