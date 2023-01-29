package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.misc.AncientGemShard;
import dev.kyro.pitsim.aitems.misc.ChunkOfVile;
import dev.kyro.pitsim.aitems.misc.FunkyFeather;
import dev.kyro.pitsim.aitems.prot.ProtBoots;
import dev.kyro.pitsim.aitems.prot.ProtChestplate;
import dev.kyro.pitsim.aitems.prot.ProtHelmet;
import dev.kyro.pitsim.aitems.prot.ProtLeggings;
import dev.kyro.pitsim.battlepass.PassData;
import dev.kyro.pitsim.battlepass.PassManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
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
			case "feather":
				ItemFactory.getItem(FunkyFeather.class).giveItem(player, amount);
				return false;
			case "vile":
				ItemFactory.getItem(ChunkOfVile.class).giveItem(player, amount);
				return false;
			case "xp":
				LevelManager.addXP(player, amount);
				return false;
			case "gold":
				LevelManager.addGold(player, amount);
				return false;
			case "renown":
				pitPlayer.renown += amount;
				Player finalPlayer = player;
				new BukkitRunnable() {
					@Override
					public void run() {
						AOutput.send(finalPlayer, "&7You have been given &e" + amount + " renown");
					}
				}.runTaskLater(PitSim.INSTANCE, 3L);
				return false;
			case "shard":
				AUtil.giveItemSafely(player, ItemFactory.getItem(AncientGemShard.class).getItem(amount), true);
				return false;
		}
		return false;
	}
}
