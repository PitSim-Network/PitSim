package net.pitsim.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.aitems.misc.*;
import net.pitsim.pitsim.aitems.diamond.ProtBoots;
import net.pitsim.pitsim.aitems.diamond.ProtChestplate;
import net.pitsim.pitsim.aitems.diamond.ProtHelmet;
import net.pitsim.pitsim.aitems.diamond.ProtLeggings;
import net.pitsim.pitsim.battlepass.PassData;
import net.pitsim.pitsim.battlepass.PassManager;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.controllers.LevelManager;
import net.pitsim.pitsim.controllers.objects.HelmetManager;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.MysticType;
import net.pitsim.pitsim.misc.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class RewardCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) return false;

		if(args.length < 2) return false;

		Player player = null;
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(!args[0].equalsIgnoreCase(onlinePlayer.getName())) continue;
			player = onlinePlayer;
			break;
		}
		if(player == null) return false;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PassData passData = pitPlayer.getPassData(PassManager.currentPass.startDate);

		String item = args[1].toLowerCase();
		int amount = args.length < 3 ? 1 : Integer.parseInt(args[2]);

		Player finalPlayer = player;
		switch(item) {
			case "pass":
				passData.hasPremium = true;
				return false;
			case "passboost":
				if(!passData.hasPremium) passData.totalPoints += PassManager.POINTS_PER_TIER * 9;
				passData.hasPremium = true;
				return false;
			case "passtiers":
				passData.totalPoints += amount * PassManager.POINTS_PER_TIER;
				return false;
			case "hjsword":
				for(int i = 0; i < amount; i++) {
					ItemStack jewelSword = MysticFactory.getJewelItem(MysticType.SWORD);
					AUtil.giveItemSafely(player, jewelSword);
				}
				return false;
			case "hjbow":
				for(int i = 0; i < amount; i++) {
					ItemStack jewelBow = MysticFactory.getJewelItem(MysticType.BOW);
					AUtil.giveItemSafely(player, jewelBow);
				}
				return false;
			case "hjpants":
				for(int i = 0; i < amount; i++) {
					ItemStack jewel = MysticFactory.getJewelItem(MysticType.PANTS);
					AUtil.giveItemSafely(player, jewel);
				}
				return false;
			case "hjbundle":
				for(int i = 0; i < amount; i++) {
					ItemStack jbsword = MysticFactory.getJewelItem(MysticType.SWORD);
					AUtil.giveItemSafely(player, jbsword);
				}

				for(int i = 0; i < amount; i++) {
					ItemStack jbbow = MysticFactory.getJewelItem(MysticType.BOW);
					AUtil.giveItemSafely(player, jbbow);
				}

				for(int i = 0; i < amount; i++) {
					ItemStack jbpants = MysticFactory.getJewelItem(MysticType.PANTS);
					AUtil.giveItemSafely(player, jbpants);
				}
				return false;
			case "scythe":
				for(int i = 0; i < amount; i++) {
					ItemStack scythe = MysticFactory.getFreshItem(MysticType.TAINTED_SCYTHE, null);
					AUtil.giveItemSafely(player, scythe);
				}
				return false;
			case "chestplate":
				for(int i = 0; i < amount; i++) {
					ItemStack chestplate = MysticFactory.getFreshItem(MysticType.TAINTED_CHESTPLATE, null);
					AUtil.giveItemSafely(player, chestplate);
				}
				return false;
			case "p1":
				ItemFactory.getItem(ProtHelmet.class).giveItem(player, 1);
				ItemFactory.getItem(ProtChestplate.class).giveItem(player, 1);
				ItemFactory.getItem(ProtLeggings.class).giveItem(player, 1);
				ItemFactory.getItem(ProtBoots.class).giveItem(player, 1);
				return false;
			case "p1helmet":
				ItemFactory.getItem(ProtHelmet.class).giveItem(player, 1);
				return false;
			case "p1chestplate":
				ItemFactory.getItem(ProtChestplate.class).giveItem(player, 1);
				return false;
			case "p1leggings":
				ItemFactory.getItem(ProtLeggings.class).giveItem(player, 1);
				return false;
			case "p1boots":
				ItemFactory.getItem(ProtBoots.class).giveItem(player, 1);
				return false;
			case "ghelm":
				ItemStack itemStack = ItemFactory.getItem(GoldenHelmet.class).getItem();
				itemStack = HelmetManager.depositGold(itemStack, amount);
				AUtil.giveItemSafely(player, itemStack, true);
				return false;
			case "feather":
				ItemFactory.getItem(FunkyFeather.class).giveItem(player, amount);
				return false;
			case "corruptedfeather":
				ItemFactory.getItem(CorruptedFeather.class).giveItem(player, amount);
				return false;
			case "vile":
				ItemFactory.getItem(ChunkOfVile.class).giveItem(player, amount);
				return false;
			case "shard":
				AUtil.giveItemSafely(player, ItemFactory.getItem(AncientGemShard.class).getItem(amount), true);
				return false;
			case "yummybread":
				ItemFactory.getItem(YummyBread.class).giveItem(player, amount);
				return false;
			case "xp":
				LevelManager.addXP(player, amount);
				return false;
			case "gold":
				LevelManager.addGold(player, amount);
				return false;
			case "double":
				double goldToGive = Math.min(pitPlayer.gold, 2000000);
				LevelManager.addGold(player, (int) goldToGive);
				return false;
			case "renown":
				pitPlayer.renown += amount;
				new BukkitRunnable() {
					@Override
					public void run() {
						AOutput.send(finalPlayer, "&7You have been given " + Formatter.formatRenown(amount));
					}
				}.runTaskLater(PitSim.INSTANCE, 3L);
				return false;
			case "soul":
				pitPlayer.giveSouls(amount);
				new BukkitRunnable() {
					@Override
					public void run() {
						AOutput.send(finalPlayer, "&7You have been given " + Formatter.formatSouls(amount));
					}
				}.runTaskLater(PitSim.INSTANCE, 3L);
				return false;
		}
		return false;
	}
}
