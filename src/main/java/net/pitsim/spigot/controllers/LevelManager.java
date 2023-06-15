package net.pitsim.spigot.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.battlepass.quests.CongratulatePrestigeQuest;
import net.pitsim.spigot.battlepass.quests.EarnRenownQuest;
import net.pitsim.spigot.battlepass.quests.GrindGoldQuest;
import net.pitsim.spigot.battlepass.quests.GrindXPQuest;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.killstreaks.NoKillstreak;
import net.pitsim.spigot.megastreaks.Overdrive;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.upgrades.FastPass;
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
		pitPlayer.remainingXP = (long) ((PrestigeValues.getXPForLevel(pitPlayer.level) * prestigeInfo.getXpMultiplier()));
		pitPlayer.updateXPBar();

		Sounds.LEVEL_UP.play(player);
		Misc.sendTitle(player, "&e&lLEVEL UP!", 40);
		Misc.sendSubTitle(player, prestigeInfo.getOpenBracket() + PrestigeValues.getLevelColor(pitPlayer.level - 1) +
				(pitPlayer.level - 1) + prestigeInfo.getCloseBracket() + " &7\u279F " + prestigeInfo.getOpenBracket() +
				PrestigeValues.getLevelColor(pitPlayer.level) + pitPlayer.level + prestigeInfo.getCloseBracket(), 40);
		AOutput.send(player, "&e&lPIT LEVEL UP! " + prestigeInfo.getOpenBracket() +
				PrestigeValues.getLevelColor(pitPlayer.level - 1) + (pitPlayer.level - 1) +
				prestigeInfo.getCloseBracket() + " &7\u279F " + prestigeInfo.getOpenBracket() +
				PrestigeValues.getLevelColor(pitPlayer.level) + pitPlayer.level + prestigeInfo.getCloseBracket());
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
		if(pitPlayer.goldGrinded < prestigeInfo.getGoldReq()) return;

		pitPlayer.prestige += 1;
		if(UpgradeManager.hasUpgrade(player, FastPass.INSTANCE)) {
			pitPlayer.level = 50;
			pitPlayer.remainingXP = (long) (PrestigeValues.getXPForLevel(50) * prestigeInfo.getXpMultiplier());
		} else {
			pitPlayer.level = 1;
			pitPlayer.remainingXP = (long) (PrestigeValues.getXPForLevel(1) * prestigeInfo.getXpMultiplier());
		}
		pitPlayer.goldGrinded = 0;
		pitPlayer.endKillstreak();
		pitPlayer.setMegastreak(Overdrive.INSTANCE);
		pitPlayer.gold = 0;
		pitPlayer.renown += prestigeInfo.getRenownReward();
		EarnRenownQuest.INSTANCE.gainRenown(pitPlayer, prestigeInfo.getRenownReward());
		pitPlayer.moonBonus = 0;
		pitPlayer.goldStack = 0;
		pitPlayer.killstreaks.set(1, NoKillstreak.INSTANCE);
		pitPlayer.killstreaks.set(2, NoKillstreak.INSTANCE);

		ChatTriggerManager.sendProgressionInfo(pitPlayer);
		ChatTriggerManager.sendPrestigeInfo(pitPlayer);
		ChatTriggerManager.sendPerksInfo(pitPlayer);

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
