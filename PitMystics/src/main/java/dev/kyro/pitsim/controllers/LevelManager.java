package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LevelManager {


	public static void addXp(Player player, int xp) {
		if(!(NonManager.getNon(player) == null)) return;
		FileConfiguration playerData = APlayerData.getPlayerData(player);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		while(xp > 0) {
			if(!(pitPlayer.level < 120)) {
				pitPlayer.remainingXP = 0;
				return;
			}
			if(pitPlayer.remainingXP - xp <= 0) {
				xp -= pitPlayer.remainingXP;
				pitPlayer.remainingXP = 0;
				incrementLevel(player);
			} else {
				pitPlayer.remainingXP -= xp;
				xp = 0;
			}
			setXPBar(player, pitPlayer);
			playerData.set("xp", pitPlayer.remainingXP);
			playerData.set("prestige", pitPlayer.prestige);
			APlayerData.savePlayerData(player);
		}

	}

	public static void incrementLevel(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		if(!(pitPlayer.level < 120)) return;
		if(pitPlayer.remainingXP > 0) return;

		pitPlayer.level += 1;
		pitPlayer.remainingXP = (int) ((PrestigeValues.getXPForLevel(pitPlayer.level) * prestigeInfo.xpMultiplier));
		setXPBar(player, pitPlayer);

		ASound.play(player, Sound.LEVEL_UP, 1, 1);
		Misc.sendTitle(player, "&e&lLEVEL UP!", 40);
		Misc.sendSubTitle(player, prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level - 1) + (pitPlayer.level - 1) + prestigeInfo.getCloseBracket() + " &7\u279F " + prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level) + pitPlayer.level + prestigeInfo.getCloseBracket(), 40);
		AOutput.send(player,  "&e&lPIT LEVEL UP! " + prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level - 1) + (pitPlayer.level - 1) + prestigeInfo.getCloseBracket() + " &7\u279F " + prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level) + pitPlayer.level + prestigeInfo.getCloseBracket());

		FileConfiguration playerData = APlayerData.getPlayerData(player);
		playerData.set("level", pitPlayer.level);
		playerData.set("xp", pitPlayer.remainingXP);
		playerData.set("prestige", pitPlayer.prestige);
		APlayerData.savePlayerData(player);

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefix(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}
	}

	public static void addGold(Player player, int amount) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		pitPlayer.goldGrinded += amount;
		PitSim.VAULT.depositPlayer(player, amount);
		FileConfiguration playerData = APlayerData.getPlayerData(player);
		playerData.set("goldgrinded", pitPlayer.goldGrinded);
		APlayerData.savePlayerData(player);
	}


	public static void incrementPrestige(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		if(pitPlayer.level < 120) return;
		if(pitPlayer.goldGrinded < prestigeInfo.goldReq) return;
//		if(pitPlayer.playerKills < prestigeInfo.killReq) return;

		pitPlayer.prestige += 1;
		pitPlayer.level = 1;
		pitPlayer.goldGrinded = 0;
		PitSim.VAULT.withdrawPlayer(player, PitSim.VAULT.getBalance(player));
		pitPlayer.playerKills = 0;
		pitPlayer.remainingXP = (int) (PrestigeValues.getXPForLevel(1) * prestigeInfo.xpMultiplier);
		pitPlayer.renown += prestigeInfo.renownReward;
		pitPlayer.moonBonus = 0;

		FileConfiguration playerData = APlayerData.getPlayerData(player);
		playerData.set("goldgrinded", pitPlayer.goldGrinded);
		playerData.set("prestige", pitPlayer.prestige);
		playerData.set("level", pitPlayer.level);
		playerData.set("renown", pitPlayer.renown);
		playerData.set("playerkills", pitPlayer.playerKills);
		playerData.set("xp", pitPlayer.remainingXP);
		if(pitPlayer.prestige >= 33) playerData.set("moonbonus", pitPlayer.moonBonus);
		APlayerData.savePlayerData(player);

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefix(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}


		ASound.play(player, Sound.ENDERDRAGON_GROWL, 1, 1);
		Misc.sendTitle(player, "&e&lPRESTIGE!", 40);
		Misc.sendSubTitle(player, "&7You unlocked prestige &e" + AUtil.toRoman(pitPlayer.prestige), 40);
		String message2 = ChatColor.translateAlternateColorCodes('&', "&e&lPRESTIGE! %luckperms_prefix%%player_name% &7unlocked prestige &e" + AUtil.toRoman(pitPlayer.prestige) + "&7, gg!");
		Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player, message2));


	}

	public static void setXPBar(Player player, PitPlayer pitPlayer) {
		if(NonManager.getNon(player) != null) return;

		player.setLevel(pitPlayer.level);
		float remaining = pitPlayer.remainingXP;
		float total = (PrestigeValues.getXPForLevel(pitPlayer.level) / 10);

		player.setLevel(pitPlayer.level);
		float xp = (total - remaining) /  total;

		player.setExp(xp);

	}


}
