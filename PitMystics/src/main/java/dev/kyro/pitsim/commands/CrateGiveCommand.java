package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.ChunkOfVile;
import dev.kyro.pitsim.misc.FunkyFeather;
import dev.kyro.pitsim.misc.ProtArmor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CrateGiveCommand implements CommandExecutor {
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

		String item = args[1].toLowerCase();
		int amount = args.length < 3 ? 1 : Integer.parseInt(args[2]);

		switch(item) {
			case "hjsword":
				for(int i = 0; i < amount; i++) {
					ItemStack jewelSword = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
					jewelSword = ItemManager.enableDropConfirm(jewelSword);
					NBTItem nbtItemSword = new NBTItem(jewelSword);
					nbtItemSword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
					EnchantManager.setItemLore(nbtItemSword.getItem());
					AUtil.giveItemSafely(player, nbtItemSword.getItem());
				}
				return false;
			case "hjbow":
				for(int i = 0; i < amount; i++) {
					ItemStack jewelBow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
					jewelBow = ItemManager.enableDropConfirm(jewelBow);
					NBTItem nbtItemBow = new NBTItem(jewelBow);
					nbtItemBow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
					EnchantManager.setItemLore(nbtItemBow.getItem());
					AUtil.giveItemSafely(player, nbtItemBow.getItem());
				}
				return false;
			case "hjpants":
				for(int i = 0; i < amount; i++) {
					ItemStack jewel = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
					jewel = ItemManager.enableDropConfirm(jewel);
					NBTItem nbtItem = new NBTItem(jewel);
					nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
					EnchantManager.setItemLore(nbtItem.getItem());
					AUtil.giveItemSafely(player, nbtItem.getItem());
				}
				return false;
			case "hjbundle":
				for(int i = 0; i < amount; i++) {
					ItemStack jbsword = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
					jbsword = ItemManager.enableDropConfirm(jbsword);
					NBTItem nbtjbsword = new NBTItem(jbsword);
					nbtjbsword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
					EnchantManager.setItemLore(nbtjbsword.getItem());
					AUtil.giveItemSafely(player, nbtjbsword.getItem());
				}

				for(int i = 0; i < amount; i++) {
					ItemStack jbbow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
					jbbow = ItemManager.enableDropConfirm(jbbow);
					NBTItem nbtjbbow = new NBTItem(jbbow);
					nbtjbbow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
					EnchantManager.setItemLore(nbtjbbow.getItem());
					AUtil.giveItemSafely(player, nbtjbbow.getItem());
				}

				for(int i = 0; i < amount; i++) {
					ItemStack jb = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
					jb = ItemManager.enableDropConfirm(jb);
					NBTItem nbtjb = new NBTItem(jb);
					nbtjb.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
					EnchantManager.setItemLore(nbtjb.getItem());
					AUtil.giveItemSafely(player, nbtjb.getItem());
				}
				return false;
			case "p1":
				ProtArmor.getArmor(player, "helmet");
				ProtArmor.getArmor(player, "chestplate");
				ProtArmor.getArmor(player, "leggings");
				ProtArmor.getArmor(player, "boots");
				return false;
			case "p1helmet":
				ProtArmor.getArmor(player, "helmet");
				return false;
			case "p1chestplate":
				ProtArmor.getArmor(player, "chestplate");
				return false;
			case "p1leggings":
				ProtArmor.getArmor(player, "leggings");
				return false;
			case "p1boots":
				ProtArmor.getArmor(player, "boots");
				return false;
			case "feather":
				FunkyFeather.giveFeather(player, amount);
				return false;
			case "vile":
				ChunkOfVile.giveVile(player, amount);
				return false;
			case "gold":
				LevelManager.addGold(player, amount);
				return false;
			case "renown":
				pitPlayer.renown += amount;
				FileConfiguration playerData = APlayerData.getPlayerData(player);
				playerData.set("renown", pitPlayer.renown);
				APlayerData.savePlayerData(player);
				Player finalPlayer = player;
				new BukkitRunnable() {
					@Override
					public void run() {
						AOutput.send(finalPlayer, "&7You have been given &e" + amount + " renown");
					}
				}.runTaskLater(PitSim.INSTANCE, 3L);
				return false;
		}

		return false;
	}
}
