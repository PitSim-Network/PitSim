package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.OldLevelManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.megastreaks.Overdrive;
import dev.kyro.pitsim.perks.NoPerk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

public class BackwardsCompatibility implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		levelSystemConversion(player);

		FileConfiguration playerData = APlayerData.getPlayerData(player);
		playerData.set("lastversion", PitSim.version);
		APlayerData.savePlayerData(player);
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
		FileConfiguration playerData = APlayerData.getPlayerData(player);
		double version = playerData.getDouble("lastversion");
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


		for(int i = 0; i < pitPlayer.pitPerks.length; i++) {
			if(pitPlayer.pitPerks[i].refName.equals("streaker")) {
				pitPlayer.pitPerks[i] = NoPerk.INSTANCE;
				playerData.set("perk-" + (i + 1),  NoPerk.INSTANCE.refName);
			}
			if(pitPlayer.pitPerks[i].refName.equals("firststrike")) {
				pitPlayer.pitPerks[i] = NoPerk.INSTANCE;
				playerData.set("perk-" + (i + 1),  NoPerk.INSTANCE.refName);
			}
		}

		if(renown > 0) pitPlayer.renown = renown;

		PrestigeValues.PrestigeInfo newPrestigeInfo = PrestigeValues.getPrestigeInfo(newPrestige);

		pitPlayer.prestige = newPrestige;
		pitPlayer.level = 1;
		pitPlayer.remainingXP = (int) (PrestigeValues.getXPForLevel(1) * newPrestigeInfo.xpMultiplier);
		pitPlayer.playerKills = 0;
		pitPlayer.megastreak = new Overdrive(pitPlayer);
		pitPlayer.goldGrinded = 0;

		playerData.set("goldgrinded", pitPlayer.goldGrinded);
		playerData.set("prestige", pitPlayer.prestige);
		playerData.set("level", pitPlayer.level);
		playerData.set("renown", pitPlayer.renown);
		playerData.set("playerkills", pitPlayer.playerKills);
		playerData.set("xp", pitPlayer.remainingXP);
		playerData.set("megastreak", pitPlayer.megastreak.getRawName());

		APlayerData.savePlayerData(player);

		if(newPrestige  > 0) {
			Sounds.COMPENSATION.play(player);
			AOutput.send(player, "&a&lLEVEL SYSTEM REWORK!");
			AOutput.send(player, "&7We have switched over to a prestige system!");
			AOutput.send(player, "&7Your prestige has been set to &e" + AUtil.toRoman(newPrestige));
			AOutput.send(player, "&7You were given &e" + pitPlayer.renown + " &7renown.");
			AOutput.send(player, "&cAll renown unlocks were cleared from you account.");
		}


	}

//	public static void compensateRenown(Player player) {
//		FileConfiguration playerData = APlayerData.getPlayerData(player);
//		if(playerData.contains("lastversion") && playerData.getDouble("lastversion")  >= 1.0) return;
//
//		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//		int totalRenown = 0;
//		for(int i = 0; i < pitPlayer.playerLevel; i++) {
//			totalRenown += OldLevelManager.getRenownFromLevel(i);
//		}
//		pitPlayer.renown += totalRenown;
//		playerData.set("renown", pitPlayer.renown);
//		AOutput.send(player, "&a&lCOMPENSATION! &7Received &e+" + totalRenown + " Renown &7for your current level.");
//		Sounds.COMPENSATION.play(player);
//
//		APlayerData.savePlayerData(player);
//	}
//
//	public static void compensateFancyPants(Player player) {
//		FileConfiguration playerData = APlayerData.getPlayerData(player);
//		if(!playerData.contains("FANCY_PANTS")) return;
//		playerData.set("FANCY_PANTS", null);
//
//		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//
//		pitPlayer.renown += 10;
//		playerData.set("renown", pitPlayer.renown);
//		AOutput.send(player, "&a&lCOMPENSATION! &7Received &e+" + 10 + " Renown &7for the removal of &fFancy Pants");
//		Sounds.COMPENSATION.play(player);
//
//		APlayerData.savePlayerData(player);
//	}
//
//	public static void compensateRenownPerks(Player player) {
//		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//		FileConfiguration playerData = APlayerData.getPlayerData(player);
//		int renown = 0;
//		for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
//			if(UpgradeManager.hasUpgrade(player, upgrade) && upgrade.prestigeReq > pitPlayer.prestige) {
//				Bukkit.broadcastMessage(upgrade.name);
//				if(upgrade.isTiered) {
//					int tier = UpgradeManager.getTier(player, upgrade);
//					for(int i = 0; i < tier; i++) {
//						renown += upgrade.getTierCosts().get(i);
//					}
//				} else renown += upgrade.renownCost;
//				playerData.set(upgrade.refName, null);
//			}
//		}
//		pitPlayer.renown += renown;
//
//		if(renown == 0) return;
//		playerData.set("renown", pitPlayer.renown);
//		APlayerData.savePlayerData(player);
//		AOutput.send(player, "&a&lCOMPENSATION! &7Received &e+" + renown + " Renown &7for recent renown shop changes.");
//		Sounds.COMPENSATION.play(player);
//	}
}
