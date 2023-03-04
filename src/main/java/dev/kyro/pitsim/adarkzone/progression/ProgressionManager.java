package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.damage.DamageBranch;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ProgressionManager implements Listener {
	public static List<SkillBranch> skillBranches = new ArrayList<>();
	public static List<MainProgressionUnlock> mainProgressionUnlocks = new ArrayList<>();

	static {
		registerBranch(new DamageBranch());

		registerMainUnlock(new MainProgressionStart("start", 1, 3));

		registerMainUnlock(new MainProgressionMinorUnlock("main-1", 2, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-2", 3, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-3", 4, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-4", 5, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-5", 6, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-6", 7, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-7", 8, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-8", 9, 3));

		registerMainUnlock(new MainProgressionMinorUnlock("top-1", 2, 2));
		registerMainUnlock(new MainProgressionMinorUnlock("top-2", 4, 2));
		registerMainUnlock(new MainProgressionMinorUnlock("top-3", 6, 2));
		registerMainUnlock(new MainProgressionMinorUnlock("top-4", 8, 2));
		registerMainUnlock(new MainProgressionMinorUnlock("bottom-1", 3, 4));
		registerMainUnlock(new MainProgressionMinorUnlock("bottom-2", 5, 4));
		registerMainUnlock(new MainProgressionMinorUnlock("bottom-3", 7, 4));
		registerMainUnlock(new MainProgressionMinorUnlock("bottom-4", 9, 4));

		registerMainUnlock(new MainProgressionMajorUnlock(DamageBranch.class, 2, 1));
	}

	/*
    Branches:
    damage &c - increased dmg vs mobs, increased dmg vs bosses
    first: unlock bosses
    last: increased damage vs mobs and bosses
    path 1: heal on mob kill
    path 2: 50% chance for an item to count as 2 when spawning a boss

    defense &9 - decreased damage from mobs & bosses, increased shield
    first: unlock shield
    last: shield takes x% less damage
    path 1: decreased damage from players?
    path 2: decrease time until shield resets

    souls &d/&f - increased soul chance from mobs, increased fresh drop chance?
    first: ability to t3 items
    last: ability to t4 items
    path 1: ability to fast travel for free
    path 2: decreased cost to tier 3 items

    mana &b - increased max mana, increased mana regen
    first: unlock mana
    last: spells cost -x% less mana
    path 1: mobs give +x mana on kill
    path 2: when your shield is down, regenerate mana x% faster

    potions &5 - decreased brew time, increased brewing "luck"
    first: +2 brewing slots
    last: +1 brewing ingredient slot
    path 1: unlock catalyst crafting
    path 2: unlock crafting of ingredient that increases potency or smth

    sacrifice &4 - x% more promotion lives, x% more renown
    first: unlock the ability to use totems
    last: increase the effect of all totems by x%
    path 1: unlock the fourth totem, wealth
    path 2: unlock the fifth totem, chaos
    */

	public static void registerBranch(SkillBranch skillBranch) {
		skillBranches.add(skillBranch);
	}

	public static void registerMainUnlock(MainProgressionUnlock mainUnlock) {
		mainProgressionUnlocks.add(mainUnlock);
	}

	public static MainProgressionUnlock getMainProgressionUnlock(int guiXPos, int guiYPos) {
		for(MainProgressionUnlock unlock : mainProgressionUnlocks)
			if(unlock.guiXPos == guiXPos && unlock.guiYPos == guiYPos) return unlock;
		return null;
	}

	public static UnlockState getUnlockState(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(isUnlocked(pitPlayer, unlock)) return UnlockState.UNLOCKED;
		if(
				isUnlocked(pitPlayer, getMainProgressionUnlock(unlock.guiXPos + 0, unlock.guiYPos + 1)) ||
						isUnlocked(pitPlayer, getMainProgressionUnlock(unlock.guiXPos + 0, unlock.guiYPos - 1)) ||
						isUnlocked(pitPlayer, getMainProgressionUnlock(unlock.guiXPos + 1, unlock.guiYPos + 0)) ||
						isUnlocked(pitPlayer, getMainProgressionUnlock(unlock.guiXPos - 1, unlock.guiYPos + 0))
		) return UnlockState.NEXT_TO_UNLOCK;
		return UnlockState.LOCKED;
	}

	public static UnlockState getUnlockState(PitPlayer pitPlayer, SkillBranch.MajorProgressionUnlock unlock) {
		if(isUnlocked(pitPlayer, unlock)) return UnlockState.UNLOCKED;
		if(unlock.position == SkillBranch.MajorUnlockPosition.FIRST) {
			return UnlockState.NEXT_TO_UNLOCK;
		} else if(unlock.position == SkillBranch.MajorUnlockPosition.LAST) {
			if(isFullyUnlocked(pitPlayer, unlock.skillBranch.firstPath) && isFullyUnlocked(pitPlayer, unlock.skillBranch.secondPath))
				return UnlockState.NEXT_TO_UNLOCK;
		} else {
			SkillBranch.Path path = unlock.getAssociatedPath();
			if(getUnlockedLevel(pitPlayer, path) == 3) return UnlockState.NEXT_TO_UNLOCK;
		}
		return UnlockState.LOCKED;
	}

	public static UnlockState getUnlockState(PitPlayer pitPlayer, SkillBranch.Path unlock, int level) {
		if(isUnlocked(pitPlayer, unlock, level)) return UnlockState.UNLOCKED;
		if(level == 1) {
			if(isUnlocked(pitPlayer, unlock.skillBranch.firstUnlock)) return UnlockState.NEXT_TO_UNLOCK;
		} else if(level == 4) {
			if(isUnlocked(pitPlayer, unlock.getAssociatedUnlock())) return UnlockState.NEXT_TO_UNLOCK;
		} else {
			if(isUnlocked(pitPlayer, unlock, level - 1)) return UnlockState.NEXT_TO_UNLOCK;
		}
		return UnlockState.LOCKED;
	}

	public static int getUnlockCost(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		int unlocks = pitPlayer.darkzoneData.mainProgressionUnlocks.size();
		int cost = (unlocks + 1) * 10;
		if(unlock instanceof MainProgressionMajorUnlock) cost *= 2;
		return cost;
	}

	public static String getUnlockCostFormatted(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		int cost = getUnlockCost(pitPlayer, unlock);
		return formatSouls(cost);
	}

	public static String getUnlockCostFormatted(SkillBranch.MajorProgressionUnlock unlock) {
		int cost = unlock.getCost();
		return formatSouls(cost);
	}

	public static String getUnlockCostFormatted(SkillBranch.Path unlock, int level) {
		int cost = unlock.getCost(level);
		return formatSouls(cost);
	}

	public static String formatSouls(int souls) {
		return "&f" + souls + " soul" + (souls == 1 ? "" : "s");
	}

	public static void addPurchaseCostLore(ALoreBuilder loreBuilder, UnlockState unlockState, int currentSouls, int cost, boolean alwaysDisplayCost) {
		String costString = formatSouls(cost);
		if(unlockState == UnlockState.LOCKED) {
			if(alwaysDisplayCost) loreBuilder.addLore("&7Unlock Cost: " + costString, "");
			loreBuilder.addLore("&cCannot be unlocked yet");
		} else if(unlockState == UnlockState.NEXT_TO_UNLOCK) {
			loreBuilder.addLore("&7Unlock Cost: " + costString, "");
			if(currentSouls < cost) {
				loreBuilder.addLore("&cNot enough souls");
			} else {
				loreBuilder.addLore("&eClick to purchase!");
			}
		} else if(unlockState == UnlockState.UNLOCKED) {
			loreBuilder.addLore("&aAlready unlocked!");
		}
	}

	public static <T extends SkillBranch> T getSkillBranch(Class<T> clazz) {
		for(SkillBranch skillBranch : skillBranches) if(skillBranch.getClass() == clazz) return (T) skillBranch;
		throw new RuntimeException();
	}

	public static void unlock(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id)) throw new RuntimeException();
		pitPlayer.darkzoneData.mainProgressionUnlocks.add(unlock.id);
		Sounds.SUCCESS.play(pitPlayer.player);

		AOutput.send(pitPlayer.player, "&5&lDARKZONE!&7 You unlocked " + unlock.getDisplayName());
	}

	public static void unlock(PitPlayer pitPlayer, SkillBranch.MajorProgressionUnlock unlock) {
		pitPlayer.darkzoneData.skillBranchUnlocks.putIfAbsent(unlock.skillBranch.getRefName(), new DarkzoneData.SkillBranchData());
		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(unlock.skillBranch.getRefName());
		if(skillBranchData.majorUnlocks.contains(unlock.getRefName())) throw new RuntimeException();
		skillBranchData.majorUnlocks.add(unlock.getRefName());

		Sounds.SUCCESS.play(pitPlayer.player);
		AOutput.send(pitPlayer.player, "&5&lDARKZONE!&7 You unlocked " + unlock.getDisplayName());
	}

	public static void unlockNext(PitPlayer pitPlayer, SkillBranch.Path unlock) {
		pitPlayer.darkzoneData.skillBranchUnlocks.putIfAbsent(unlock.skillBranch.getRefName(), new DarkzoneData.SkillBranchData());
		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(unlock.skillBranch.getRefName());
		int currentLevel = skillBranchData.pathUnlocks.getOrDefault(unlock.getRefName(), 0);
		skillBranchData.pathUnlocks.put(unlock.getRefName(), currentLevel + 1);

		Sounds.SUCCESS.play(pitPlayer.player);
		AOutput.send(pitPlayer.player, "&5&lDARKZONE!&7 You unlocked " + unlock.getDisplayName() + " " + AUtil.toRoman(currentLevel + 1));
	}

	public static boolean isUnlocked(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(pitPlayer == null || unlock == null) return false;
		if(unlock instanceof MainProgressionStart) return true;
		return pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id);
	}

	public static boolean isFullyUnlocked(PitPlayer pitPlayer, SkillBranch.Path path) {
		return isUnlocked(pitPlayer, path, 6);
	}

	public static boolean isUnlocked(PitPlayer pitPlayer, SkillBranch.Path path, int level) {
		if(pitPlayer == null || path == null) return false;
		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(path.skillBranch.getRefName());
		if(skillBranchData == null || !skillBranchData.pathUnlocks.containsKey(path.getRefName())) return false;
		return skillBranchData.pathUnlocks.get(path.getRefName()) >= level;
	}

	public static int getUnlockedLevel(PitPlayer pitPlayer, SkillBranch.Path path) {
		if(pitPlayer == null || path == null) return 0;
		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(path.skillBranch.getRefName());
		if(skillBranchData == null) return 0;
		return skillBranchData.pathUnlocks.getOrDefault(path.getRefName(), 0);
	}

	public static double getUnlockedEffect(PitPlayer pitPlayer, SkillBranch.Path path, String refName) {
		int currentLvl = getUnlockedLevel(pitPlayer, path);
		if(currentLvl == 0) return 0;
		for(SkillBranch.Path.EffectData effectData : path.effectData) {
			if(!effectData.refName.equalsIgnoreCase(refName)) continue;
			int total = 0;
			for(int i = 0; i < currentLvl; i++) total += effectData.values[i];
			return total;
		}
		return 0;
	}

	public static boolean isUnlocked(PitPlayer pitPlayer, SkillBranch.MajorProgressionUnlock unlock) {
		if(pitPlayer == null || unlock == null) return false;
		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(unlock.skillBranch.getRefName());
		if(skillBranchData == null) return false;
		return skillBranchData.majorUnlocks.contains(unlock.getRefName());
	}
}
