package dev.kyro.pitsim.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.OldLevelManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.megastreaks.Overdrive;
import dev.kyro.pitsim.perks.NoPerk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class BackwardsCompatibility implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		levelSystemConversion(player);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.lastVersion = PitSim.VERSION;
		APlayer aPlayer = APlayerData.getPlayerData(player);
		FileConfiguration playerData = aPlayer.playerData;

//		if(playerData.getInt("SELF_CONFIDENCE") >= 1) {
//			playerData.set("CHEMIST", 1);
//			playerData.set("SELF_CONFIDENCE", null);
//			AOutput.send(player, "&e&lUPDATE: &7Your &eSelf Confidence &7upgrade has been changed to &eChemist I&7.");
//			aPlayer.save();
//			UpgradeManager.updatePlayer(player);
//		}
//		if(playerData.getInt("REPORT_ACCESS") >= 1) {
//			System.out.println(2);
//			playerData.set("CHEMIST", 1);
//			playerData.set("REPORT_ACCESS", null);
//			AOutput.send(player, "&e&lUPDATE: &7Your &eReport Access &7upgrade has been changed to &eChemist I&7.");
//			aPlayer.save();
//			UpgradeManager.updatePlayer(player);
//		}

		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack itemStack = player.getInventory().getItem(i);

			if(Misc.isAirOrNull(itemStack)) continue;
			NBTItem nbtItem = new NBTItem(itemStack);
			if(MysticType.getMysticType(itemStack) != MysticType.PANTS) continue;
			if(!nbtItem.hasKey(NBTTag.SAVED_PANTS_COLOR.getRef())) {
				PantColor color = PantColor.getPantColor(itemStack);
				if(color == null) continue;
				nbtItem.setString(NBTTag.SAVED_PANTS_COLOR.getRef(), color.refName);
				player.getInventory().setItem(i, nbtItem.getItem());
				player.updateInventory();
			}
		}

		if(!Misc.isAirOrNull(player.getInventory().getLeggings())) {
			NBTItem nbtItem = new NBTItem(player.getInventory().getLeggings());
			if(!nbtItem.hasKey(NBTTag.SAVED_PANTS_COLOR.getRef())) {
				PantColor color = PantColor.getPantColor(player.getInventory().getLeggings());
				if(color == null) return;
				nbtItem.setString(NBTTag.SAVED_PANTS_COLOR.getRef(), color.refName);
				player.getInventory().setLeggings(nbtItem.getItem());
				player.updateInventory();
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();

		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack itemStack = player.getInventory().getItem(i);

			if(Misc.isAirOrNull(itemStack)) continue;
			NBTItem nbtItem = new NBTItem(itemStack);
			if(MysticType.getMysticType(itemStack) != MysticType.PANTS) continue;
			if(!nbtItem.hasKey(NBTTag.SAVED_PANTS_COLOR.getRef())) {
				PantColor color = PantColor.getPantColor(itemStack);
				if(color == null) continue;
				nbtItem.setString(NBTTag.SAVED_PANTS_COLOR.getRef(), color.refName);
				player.getInventory().setItem(i, nbtItem.getItem());
				player.updateInventory();
			}
		}

		if(!Misc.isAirOrNull(player.getInventory().getLeggings())) {
			NBTItem nbtItem = new NBTItem(player.getInventory().getLeggings());
			if(!nbtItem.hasKey(NBTTag.SAVED_PANTS_COLOR.getRef())) {
				PantColor color = PantColor.getPantColor(player.getInventory().getLeggings());
				if(color == null) return;
				nbtItem.setString(NBTTag.SAVED_PANTS_COLOR.getRef(), color.refName);
				player.getInventory().setLeggings(nbtItem.getItem());
				player.updateInventory();
			}
		}
	}

	public static Boolean isNew(Player player) {
		File directory = new File("plugins/PitRemake/playerdata");
		File[] files = directory.listFiles();
		for(File file : files) {

			if(file.getName().equals(player.getUniqueId().toString() + ".yml")) {
				return false;
			}

		}
		return true;
	}

	public static void levelSystemConversion(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		APlayer aPlayer = APlayerData.getPlayerData(player);
		FileConfiguration playerData = aPlayer.playerData;
		double version = pitPlayer.lastVersion;
		if(version >= 2.0) return;

		int removedRenown = OldLevelManager.getRenownFromLevel(pitPlayer.level);
		int renown = 0;
		renown += pitPlayer.renown;

		renown -= removedRenown;
		int newPrestige = Math.min(((pitPlayer.level - 1) / 2), 50);
		if(newPrestige < 0) newPrestige = 0;


		for(int i = 0; i < newPrestige - 1; i++) {
			PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(i);
			renown += info.renownReward;
		}

		for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
			if(UpgradeManager.hasUpgrade(player, upgrade)) {
				playerData.set(upgrade.refName, null);
			}
		}


		for(int i = 0; i < pitPlayer.pitPerks.size(); i++) {
			if(pitPlayer.pitPerks.get(i).refName.equals("streaker")) {
				pitPlayer.pitPerks.set(i, NoPerk.INSTANCE);
				playerData.set("perk-" + (i + 1), NoPerk.INSTANCE.refName);
			}
			if(pitPlayer.pitPerks.get(i).refName.equals("firststrike")) {
				pitPlayer.pitPerks.set(i, NoPerk.INSTANCE);
				playerData.set("perk-" + (i + 1), NoPerk.INSTANCE.refName);
			}
		}

		if(renown > 0) pitPlayer.renown = renown;

		PrestigeValues.PrestigeInfo newPrestigeInfo = PrestigeValues.getPrestigeInfo(newPrestige);

		pitPlayer.prestige = newPrestige;
		pitPlayer.level = 1;
		pitPlayer.remainingXP = (int) (PrestigeValues.getXPForLevel(1) * newPrestigeInfo.xpMultiplier);
		pitPlayer.soulsGathered = 0;
		pitPlayer.megastreak = new Overdrive(pitPlayer);
		pitPlayer.goldGrinded = 0;

		aPlayer.save();

		if(newPrestige > 0) {
			Sounds.COMPENSATION.play(player);
			AOutput.send(player, "&a&lLEVEL SYSTEM REWORK!");
			AOutput.send(player, "&7We have switched over to a prestige system!");
			AOutput.send(player, "&7Your prestige has been set to &e" + AUtil.toRoman(newPrestige));
			AOutput.send(player, "&7You were given &e" + pitPlayer.renown + " &7renown.");
			AOutput.send(player, "&cAll renown unlocks were cleared from you account.");
		}
	}
}
