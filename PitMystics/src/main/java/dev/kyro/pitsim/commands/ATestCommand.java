package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.CutsceneManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitWorker;

public class ATestCommand implements CommandExecutor {

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					onlinePlayer.sendMessage(Bukkit.getScheduler().getActiveWorkers().size() + " : " + Bukkit.getScheduler().getPendingTasks().size());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.isOp()) return false;


		for(BukkitWorker activeWorker : Bukkit.getScheduler().getActiveWorkers()) {
			System.out.println("--------------------------------");
			System.out.println(activeWorker.getTaskId());
			System.out.println(activeWorker.getOwner());
			System.out.println("--------------------------------");
		}
		System.out.println(Bukkit.getScheduler().getActiveWorkers().size());




		//
//		for (BrewingIngredient ingredient : BrewingIngredient.ingredients) {
//			ItemStack item = ingredient.getItem();
//			item.setAmount(64);
//			AUtil.giveItemSafely(player, item);
//		}


		if(args.length < 1) return false;

//		AuctionManager.auctionItems[0].addBid(player.getUniqueId(), AuctionManager.auctionItems[0].getBid(player.getUniqueId()) + 100);

//		Bukkit.broadcastMessage("\u00A7");
//		System.out.println("\u00A7");

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