package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.ingredients.*;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.ChunkOfVile;
import dev.kyro.pitsim.misc.FunkyFeather;
import dev.kyro.pitsim.misc.ProtArmor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@Deprecated
public class OldCrateGiveCommand implements CommandExecutor {

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getWorld("darkzone").playEffect(new Location(Bukkit.getWorld("darkzone"), 213.5, 91, -90.5), Effect.ENDER_SIGNAL, 0);
			}
		}.runTaskTimer(PitSim.INSTANCE, 2, 2);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) return false;
		Player target = null;

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(args[1].equalsIgnoreCase(onlinePlayer.getName())) target = onlinePlayer;
		}
		assert target != null;

		PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
		if(args[0].equals("HJP")) {
			for(int i = 0; i < 2; i++) {
				ItemStack jewel = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
				jewel = ItemManager.enableDropConfirm(jewel);
				NBTItem nbtItem = new NBTItem(jewel);
				nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtItem.getItem(), target);
				AUtil.giveItemSafely(target, nbtItem.getItem());
			}

			broadcast("&32x Hidden Jewel Pants", target);
		}
		if(args[0].equals("HJS")) {
			for(int i = 0; i < 2; i++) {
				ItemStack jewelSword = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
				jewelSword = ItemManager.enableDropConfirm(jewelSword);
				NBTItem nbtItemSword = new NBTItem(jewelSword);
				nbtItemSword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtItemSword.getItem(), target);
				AUtil.giveItemSafely(target, nbtItemSword.getItem());
			}
			broadcast("&e2x Hidden Jewel Sword", target);
		}
		if(args[0].equals("HJB")) {
			for(int i = 0; i < 2; i++) {
				ItemStack jewelBow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
				jewelBow = ItemManager.enableDropConfirm(jewelBow);
				NBTItem nbtItemBow = new NBTItem(jewelBow);
				nbtItemBow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtItemBow.getItem(), target);
				AUtil.giveItemSafely(target, nbtItemBow.getItem());
			}
			broadcast("&b2x Hidden Jewel Bow", target);
		}
		if(args[0].equals("P1")) {
			ProtArmor.getArmor(target, "helmet");
			ProtArmor.getArmor(target, "chestplate");
			ProtArmor.getArmor(target, "leggings");
			ProtArmor.getArmor(target, "boots");
			broadcast("&bProtection I Diamond Set", target);
		}
		if(args[0].equals("1F")) {
			FunkyFeather.giveFeather(target, 1);
			broadcast("&31x Funky Feather", target);
		}
		if(args[0].equals("2F")) {
			FunkyFeather.giveFeather(target, 2);
			broadcast("&32x Funky Feather", target);
		}
		if(args[0].equals("3F")) {
			FunkyFeather.giveFeather(target, 3);
			broadcast("&33x Funky Feather", target);
		}
		if(args[0].equals("5F")) {
			FunkyFeather.giveFeather(target, 5);
			broadcast("&35x Funky Feather", target);
		}
		if(args[0].equals("1V")) {
			ChunkOfVile.giveVile(target, 1);
			broadcast("&51x Chunk of Vile", target);
		}
		if(args[0].equals("2V")) {
			ChunkOfVile.giveVile(target, 2);
			broadcast("&52x Chunk of Vile", target);
		}
		if(args[0].equals("3V")) {
			ChunkOfVile.giveVile(target, 3);
			broadcast("&53x Chunk of Vile", target);
		}
		if(args[0].equals("6V")) {
			ChunkOfVile.giveVile(target, 6);
			broadcast("&56x Chunk of Vile", target);
		}
		if(args[0].equals("50K")) {
			pitTarget.gold += 150_000;
			broadcast("&6150,000 Gold", target);
		}
		if(args[0].equals("150K")) {
			pitTarget.gold += 300_000;
			broadcast("&6300,000 Gold", target);
		}
		if(args[0].equals("300K")) {
			pitTarget.gold += 500_000;
			broadcast("&6500,000 Gold", target);
		}
		if(args[0].equals("DC")) {
			if(pitTarget.gold > 5_000_000) pitTarget.gold += 5_000_000;
			else pitTarget.gold *= 2;
			broadcast("&6Double Current Gold", target);
		}
		if(args[0].equals("JB")) {
			for(int i = 0; i < 2; i++) {
				ItemStack jbsword = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
				jbsword = ItemManager.enableDropConfirm(jbsword);
				NBTItem nbtjbsword = new NBTItem(jbsword);
				nbtjbsword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtjbsword.getItem(), target);
				AUtil.giveItemSafely(target, nbtjbsword.getItem());
			}

			for(int i = 0; i < 2; i++) {
				ItemStack jbbow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
				jbbow = ItemManager.enableDropConfirm(jbbow);
				NBTItem nbtjbbow = new NBTItem(jbbow);
				nbtjbbow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtjbbow.getItem(), target);
				AUtil.giveItemSafely(target, nbtjbbow.getItem());
			}

			for(int i = 0; i < 2; i++) {
				ItemStack jb = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
				jb = ItemManager.enableDropConfirm(jb);
				NBTItem nbtjb = new NBTItem(jb);
				nbtjb.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtjb.getItem(), target);
				AUtil.giveItemSafely(target, nbtjb.getItem());
			}
			broadcast("&bHidden Jewel Bundle", target);
		}
		if(args[0].equals("P1H")) {
			ProtArmor.getArmor(target, "Helmet");
			broadcast("&bProtection I Diamond Helmet", target);
		}
		if(args[0].equals("P1C")) {
			ProtArmor.getArmor(target, "Chestplate");
			broadcast("&bProtection I Diamond Chestplate", target);
		}
		if(args[0].equals("P1L")) {
			ProtArmor.getArmor(target, "Leggings");
			broadcast("&bProtection I Diamond Leggings", target);
		}
		if(args[0].equals("P1B")) {
			ProtArmor.getArmor(target, "Boots");
			broadcast("&bProtection I Diamond Boots", target);
		}
		if(args[0].equals("TS")) {
			AUtil.giveItemSafely(target, FreshCommand.getFreshItem(MysticType.TAINTED_SCYTHE, PantColor.TAINTED));
			taintedBroadcast("&5Fresh Tainted Scythe", target);
		}
//		TODO: Fix
		if(args[0].equals("TC")) {
			AUtil.giveItemSafely(target, FreshCommand.getFreshItem(MysticType.TAINTED_CHESTPLATE, PantColor.TAINTED));
			taintedBroadcast("&5Fresh Tainted Chestplate", target);
		}
		if(args[0].equals("15S")) {
//			BossManager.giveSouls(target, 15);
			taintedBroadcast("&f15 Tainted Souls", target);
		}
		if(args[0].equals("30S")) {
//			BossManager.giveSouls(target, 30);
			taintedBroadcast("&f30 Tainted Souls", target);
		}
		if(args[0].equals("45S")) {
//			BossManager.giveSouls(target, 45);
			taintedBroadcast("&f45 Tainted Souls", target);
		}
		if(args[0].equals("60S")) {
//			BossManager.giveSouls(target, 60);
			taintedBroadcast("&f60 Tainted Souls", target);
		}
		if(args[0].equals("75S")) {
//			BossManager.giveSouls(target, 75);
			taintedBroadcast("&f75 Tainted Souls", target);
		}
		if(args[0].equals("90S")) {
//			BossManager.giveSouls(target, 90);
			taintedBroadcast("&f90 Tainted Souls", target);
		}
		if(args[0].equals("FLESH")) {
			ItemStack item = RottenFlesh.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Rotten Flesh", target);
		}
		if(args[0].equals("BONE")) {
			ItemStack item = Bone.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Bone", target);
		}
		if(args[0].equals("EYE")) {
			ItemStack item = SpiderEye.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Spider Eye", target);
		}
		if(args[0].equals("GUNPOWDER")) {
			ItemStack item = Gunpowder.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Gunpowder", target);
		}
		if(args[0].equals("FERMENTED_EYE")) {
			ItemStack item = FermentedSpiderEye.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Fermented Spider Eye", target);
		}
		if(args[0].equals("CREAM")) {
			ItemStack item = MagmaCream.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Magma Cream", target);
		}
		if(args[0].equals("PORK")) {
			ItemStack item = RawPork.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Raw Pork", target);
		}
		if(args[0].equals("COAL")) {
			ItemStack item = WitherSkull.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Wither Skull", target);
		}
		if(args[0].equals("IRON")) {
			ItemStack item = IronIngot.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Iron Ingot", target);
		}
		if(args[0].equals("PEARL")) {
			ItemStack item = EnderPearl.INSTANCE.getItem();
			item.setAmount(10);
			AUtil.giveItemSafely(target, item, true);
			taintedBroadcast("&a10x Ender Pearl", target);
		}


		return false;
	}

	public static void broadcast(String prize, Player player) {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			String message = ChatColor.translateAlternateColorCodes('&', "&e&lGG! %luckperms_prefix%"
					+ player.getDisplayName() + " &7has won " + prize + " &7from the &6&lPit&e&lSim &7Crate!");
			onlinePlayer.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
		}
	}

	public static void taintedBroadcast(String prize, Player player) {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			String message = ChatColor.translateAlternateColorCodes('&', "&e&lGG! %luckperms_prefix%"
					+ player.getDisplayName() + " &7has won " + prize + " &7from the &5&lTainted &7Crate!");
			onlinePlayer.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
		}
	}
}
