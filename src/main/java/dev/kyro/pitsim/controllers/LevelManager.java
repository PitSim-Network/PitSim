package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.CongratulatePrestigeQuest;
import dev.kyro.pitsim.battlepass.quests.EarnRenownQuest;
import dev.kyro.pitsim.battlepass.quests.GrindGoldQuest;
import dev.kyro.pitsim.battlepass.quests.GrindXPQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.megastreaks.Overdrive;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class LevelManager {

	public static void addXP(Player player, long xpToAdd) {
		if(!(NonManager.getNon(player) == null)) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		long addedXP = 0;
		while(true) {
			if(pitPlayer.level >= 120) {
				pitPlayer.remainingXP = 0;
				break;
			}
			if(pitPlayer.remainingXP <= xpToAdd) {
				xpToAdd -= pitPlayer.remainingXP;
				addedXP += pitPlayer.remainingXP;
				pitPlayer.remainingXP = 0;
				incrementLevel(player);
			} else {
				pitPlayer.remainingXP -= xpToAdd;
				addedXP += xpToAdd;
				break;
			}
			pitPlayer.updateXPBar();
		}
		if(addedXP != 0) {
			ChatTriggerManager.sendProgressionInfo(pitPlayer);
			GrindXPQuest.INSTANCE.gainXP(pitPlayer, addedXP);
		}
	}

	public static void incrementLevel(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		if(!(pitPlayer.level < 120)) return;
		if(pitPlayer.remainingXP > 0) return;

		pitPlayer.level += 1;
		pitPlayer.remainingXP = (long) ((PrestigeValues.getXPForLevel(pitPlayer.level) * prestigeInfo.xpMultiplier));
		pitPlayer.updateXPBar();

		Sounds.LEVEL_UP.play(player);
		Misc.sendTitle(player, "&e&lLEVEL UP!", 40);
		Misc.sendSubTitle(player, prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level - 1) + (pitPlayer.level - 1) + prestigeInfo.getCloseBracket() + " &7\u279F " + prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level) + pitPlayer.level + prestigeInfo.getCloseBracket(), 40);
		AOutput.send(player, "&e&lPIT LEVEL UP! " + prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level - 1) + (pitPlayer.level - 1) + prestigeInfo.getCloseBracket() + " &7\u279F " + prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level) + pitPlayer.level + prestigeInfo.getCloseBracket());

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}
	}

	public static void addGold(Player player, int amount) {
		if(!PlayerManager.isRealPlayer(player)) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		pitPlayer.gold += amount;
		pitPlayer.goldGrinded += amount;

		ChatTriggerManager.sendProgressionInfo(pitPlayer);
		GrindGoldQuest.INSTANCE.gainGold(pitPlayer, amount);
	}

	public static void addGoldReq(Player player, double amount) {
		if(!PlayerManager.isRealPlayer(player)) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		pitPlayer.goldGrinded += amount;

		ChatTriggerManager.sendProgressionInfo(pitPlayer);
	}

	public static void incrementPrestige(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		if(pitPlayer.level < 120) return;
		if(pitPlayer.goldGrinded < prestigeInfo.goldReq) return;

		pitPlayer.prestige += 1;
		if(UpgradeManager.hasUpgrade(player, "FAST_PASS")) {
			pitPlayer.level = 50;
			pitPlayer.remainingXP = (long) (PrestigeValues.getXPForLevel(50) * prestigeInfo.xpMultiplier);
		} else {
			pitPlayer.level = 1;
			pitPlayer.remainingXP = (long) (PrestigeValues.getXPForLevel(1) * prestigeInfo.xpMultiplier);
		}
		pitPlayer.goldGrinded = 0;
		if(pitPlayer.megastreak != null) pitPlayer.megastreak.stop();
		pitPlayer.megastreak = new Overdrive(pitPlayer);
		pitPlayer.endKillstreak();
		pitPlayer.gold = 0;
		pitPlayer.renown += prestigeInfo.renownReward;
		EarnRenownQuest.INSTANCE.gainRenown(pitPlayer, prestigeInfo.renownReward);
		pitPlayer.moonBonus = 0;
		pitPlayer.goldStack = 0;
		pitPlayer.killstreaks.set(1, NoKillstreak.INSTANCE);
		pitPlayer.killstreaks.set(2, NoKillstreak.INSTANCE);

		ChatTriggerManager.sendProgressionInfo(pitPlayer);
		ChatTriggerManager.sendPrestigeInfo(pitPlayer);
		ChatTriggerManager.sendPerksInfo(pitPlayer);

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		Sounds.PRESTIGE.play(player);
		Misc.sendTitle(player, "&e&lPRESTIGE!", 40);
		Misc.sendSubTitle(player, "&7You unlocked prestige &e" + AUtil.toRoman(pitPlayer.prestige), 40);

		onPrestige(Misc.getDisplayName(player), pitPlayer.prestige);

		new PluginMessage()
				.writeString("PRESTIGE")
				.writeString(PitSim.serverName)
				.writeString(Misc.getDisplayName(player))
				.writeInt(pitPlayer.prestige)
				.send();
	}

	public static void onPrestige(String displayName, int prestige) {
		AOutput.broadcast("&e&lPRESTIGE! " + displayName + " &7unlocked prestige &e" + AUtil.toRoman(prestige) + "&7, gg!");
		CongratulatePrestigeQuest.updateRecentlyPrestiged();
	}
}
