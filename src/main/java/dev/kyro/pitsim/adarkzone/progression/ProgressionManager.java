package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.*;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.inventories.GenericConfirmationPanel;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProgressionManager implements Listener {
	public static final List<SkillBranch> skillBranches = new ArrayList<>();
	public static final List<MainProgressionUnlock> mainProgressionUnlocks = new ArrayList<>();
	public static final List<PathGUILocation> branchSlots = new ArrayList<>();

	static {
		branchSlots.add(new PathGUILocation(2, 1));
		branchSlots.add(new PathGUILocation(3, 5));
		branchSlots.add(new PathGUILocation(4, 1));
		branchSlots.add(new PathGUILocation(5, 5));
		branchSlots.add(new PathGUILocation(6, 1));
		branchSlots.add(new PathGUILocation(7, 5));
		branchSlots.add(new PathGUILocation(8, 1));
		branchSlots.add(new PathGUILocation(9, 5));

		registerBranch(new DamageBranch());
		registerBranch(new DefenceBranch());
		registerBranch(new SoulBranch());
		registerBranch(new ManaBranch());
		registerBranch(new AltarBranch());
//		registerBranch(new BrewingBranch()); // TODO: Remove fake itemstack from MainProgressionPanel

		registerMainUnlock(new MainProgressionStart("start", 1, 3));

		registerMainUnlock(new MainProgressionMinorUnlock("main-1", 2, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-2", 3, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-3", 4, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-4", 5, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-5", 6, 3));
		registerMainUnlock(new MainProgressionMinorUnlock("main-6", 7, 3));
//		registerMainUnlock(new MainProgressionMinorUnlock("main-7", 8, 3));
//		registerMainUnlock(new MainProgressionMinorUnlock("main-8", 9, 3));

		registerMainUnlock(new MainProgressionMinorUnlock("top-1", 2, 2));
		registerMainUnlock(new MainProgressionMinorUnlock("top-2", 4, 2));
		registerMainUnlock(new MainProgressionMinorUnlock("top-3", 6, 2));
//		registerMainUnlock(new MainProgressionMinorUnlock("top-4", 8, 2));
		registerMainUnlock(new MainProgressionMinorUnlock("bottom-1", 3, 4));
		registerMainUnlock(new MainProgressionMinorUnlock("bottom-2", 5, 4));
		registerMainUnlock(new MainProgressionMinorUnlock("bottom-3", 7, 4));
//		registerMainUnlock(new MainProgressionMinorUnlock("bottom-4", 9, 4));

		int index = 0;
		registerMainUnlock(new MainProgressionMajorUnlock(DamageBranch.class, index++));
		registerMainUnlock(new MainProgressionMajorUnlock(DefenceBranch.class, index++));
		registerMainUnlock(new MainProgressionMajorUnlock(SoulBranch.class, index++));
		registerMainUnlock(new MainProgressionMajorUnlock(ManaBranch.class, index++));
		registerMainUnlock(new MainProgressionMajorUnlock(AltarBranch.class, index++));
//		registerMainUnlock(new MainProgressionMajorUnlock(BrewingBranch.class, index++));
	}

	/*
    Branches:
    damage &c - increased dmg vs mobs, increased dmg vs bosses
    first: unlock bosses
    last: increased damage vs mobs and bosses
    path 1: heal on mob kill
    path 2: 50% chance for an item to count as 2 when spawning a boss

    defence &9 - decreased damage from mobs & bosses, increased shield
    first: unlock shield
    last: shield takes x% less damage
    path 1: decreased damage from players?
    path 2: decrease time until shield resets

    souls &d/&f - increased soul chance from mobs, increased fresh drop chance? // TODO (second)
    first: ability to t3 items // TODO
    last: ability to t4 items // TODO
    path 1: ability to fast travel for free // TODO
    path 2: decreased cost to tier 3 items // TODO (pull reduction % from soul branch)

    mana &b - increased max mana, increased mana regen
    first: unlock mana
    last: spells cost -x% less mana
    path 1: mobs give +x mana on kill
    path 2: when your shield is down, regenerate mana x% faster

    brewing &5 - decreased brew time, increased brewing "luck" // TODO (first, second)
    first: +1 brewing slot (3rd is premium) // TODO
    last: +1 brewing ingredient slot // TODO
    path 1: unlock potion pouch // TODO
    path 2: unlock catalyst crafting // TODO

    altar &4 - x% more promotion lives, x% more renown // TODO (first, second)
    first: unlock the ability to use pedestal // TODO
    last: increase the effect of all pedestal by x% // TODO
    path 1: unlock the fourth pedestal, wealth // TODO
    path 2: unlock the fifth pedestal, turmoil // TODO
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
		if(isUnlocked(pitPlayer, unlock.skillBranch, unlock.position)) return UnlockState.UNLOCKED;
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
			if(isUnlocked(pitPlayer, unlock.skillBranch, SkillBranch.MajorUnlockPosition.FIRST)) return UnlockState.NEXT_TO_UNLOCK;
		} else if(level == 4) {
			if(isUnlocked(pitPlayer, unlock.getAssociatedUnlock().skillBranch, unlock.getAssociatedUnlock().position)) return UnlockState.NEXT_TO_UNLOCK;
		} else {
			if(isUnlocked(pitPlayer, unlock, level - 1)) return UnlockState.NEXT_TO_UNLOCK;
		}
		return UnlockState.LOCKED;
	}

	public static int getUnlockCost(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		int unlocks = pitPlayer.darkzoneData.mainProgressionUnlocks.size();
		int cost = (unlocks + 1) * DarkzoneBalancing.MAIN_PROGRESSION_COST_PER;
		if(unlock instanceof MainProgressionMajorUnlock) cost *= DarkzoneBalancing.MAIN_PROGRESSION_MAJOR_MULTIPLIER;
		return cost;
	}

	public static String getUnlockCostFormatted(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		int cost = getUnlockCost(pitPlayer, unlock);
		return Formatter.formatSouls(cost);
	}

	public static String getUnlockCostFormatted(SkillBranch.MajorProgressionUnlock unlock) {
		int cost = ProgressionManager.getInitialSoulCost(unlock);
		return Formatter.formatSouls(cost);
	}

	public static String getUnlockCostFormatted(SkillBranch.Path unlock, int level) {
		int cost = ProgressionManager.getInitialSoulCost(unlock, level);
		return Formatter.formatSouls(cost);
	}

	public static void addPurchaseCostLore(Object object, ALoreBuilder loreBuilder, UnlockState unlockState,
										   int currentSouls, int cost, boolean alwaysDisplayCost) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0");
		String costString = Formatter.formatSouls(cost);
		if(unlockState == UnlockState.LOCKED) {
			if(alwaysDisplayCost) loreBuilder.addLore(
					"&7Unlock Cost: " + costString,
					"&7Current Souls: &f" + decimalFormat.format(currentSouls),
					"");
			loreBuilder.addLore("&cCannot be unlocked yet");
		} else if(unlockState == UnlockState.NEXT_TO_UNLOCK) {
			loreBuilder.addLore(
					"&7Unlock Cost: " + costString,
					"&7Current Souls: &f" + decimalFormat.format(currentSouls),
					""
			);
			if(currentSouls < cost) {
				loreBuilder.addLore("&cNot enough souls");
			} else {
				loreBuilder.addLore("&eClick to purchase!");
			}
		} else if(unlockState == UnlockState.UNLOCKED) {
			if(object instanceof MainProgressionMajorUnlock) {
				loreBuilder.addLore("&eClick to open!");
			} else {
				loreBuilder.addLore("&aAlready unlocked!");
			}
		}
	}

	public static <T extends SkillBranch> T getSkillBranch(Class<T> clazz) {
		for(SkillBranch skillBranch : skillBranches) if(skillBranch.getClass() == clazz) return (T) skillBranch;
		throw new RuntimeException();
	}

	public static void unlock(MainProgressionPanel previousPanel, PitPlayer pitPlayer, MainProgressionUnlock unlock, int cost) {
		if(pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id)) throw new RuntimeException();

		Consumer<GenericConfirmationPanel> confirm = panel -> {
			pitPlayer.darkzoneData.mainProgressionUnlocks.add(unlock.id);

			Sounds.SUCCESS.play(pitPlayer.player);
			AOutput.send(pitPlayer.player, "&5&lDARKZONE!&7 You unlocked " + unlock.getDisplayName() + " &7for " + Formatter.formatSouls(cost));
			if(previousPanel != null) {
				previousPanel.player.openInventory(previousPanel.getInventory());
				previousPanel.setInventory();
			}
		};
		ALoreBuilder confirmLore = new ALoreBuilder(
				"&7Purchasing: " + unlock.getDisplayName(),
				"&7Cost: &f" + Formatter.formatSouls(cost)
		);

		promptForUnlock(previousPanel, confirmLore, confirm);
	}

	public static void unlock(SkillBranchPanel previousPanel, PitPlayer pitPlayer, SkillBranch.MajorProgressionUnlock unlock, int cost) {
		pitPlayer.darkzoneData.skillBranchUnlocks.putIfAbsent(unlock.skillBranch.getRefName(), new DarkzoneData.SkillBranchData());

		Consumer<GenericConfirmationPanel> confirm = panel -> {
			DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(unlock.skillBranch.getRefName());
			if(skillBranchData.majorUnlocks.contains(unlock.getRefName())) throw new RuntimeException();
			skillBranchData.majorUnlocks.add(unlock.getRefName());

			Sounds.SUCCESS.play(pitPlayer.player);
			AOutput.send(pitPlayer.player, "&5&lDARKZONE!&7 You unlocked " + unlock.getDisplayName() + " &7for " + Formatter.formatSouls(cost));
			if(previousPanel != null) {
				previousPanel.player.openInventory(previousPanel.getInventory());
				previousPanel.setInventory();
			}
		};
		ALoreBuilder confirmLore = new ALoreBuilder(
				"&7Purchasing: " + unlock.getDisplayName(),
				"&7Cost: &f" + Formatter.formatSouls(cost)
		);

		promptForUnlock(previousPanel, confirmLore, confirm);
	}

	public static void unlockNext(SkillBranchPanel previousPanel, PitPlayer pitPlayer, SkillBranch.Path unlock, int cost) {
		pitPlayer.darkzoneData.skillBranchUnlocks.putIfAbsent(unlock.skillBranch.getRefName(), new DarkzoneData.SkillBranchData());

		Consumer<GenericConfirmationPanel> confirm = panel -> {
			DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(unlock.skillBranch.getRefName());
			int currentLevel = skillBranchData.pathUnlocks.getOrDefault(unlock.getRefName(), 0);
			skillBranchData.pathUnlocks.put(unlock.getRefName(), currentLevel + 1);

			Sounds.SUCCESS.play(pitPlayer.player);
			AOutput.send(pitPlayer.player, "&5&lDARKZONE!&7 You unlocked " + unlock.getDisplayName() + " " +
					AUtil.toRoman(currentLevel + 1) + " &7for " + Formatter.formatSouls(cost));
			if(previousPanel != null) {
				previousPanel.player.openInventory(previousPanel.getInventory());
				previousPanel.setInventory();
			}
		};
		ALoreBuilder confirmLore = new ALoreBuilder(
				"&7Purchasing: " + unlock.getDisplayName(),
				"&7Cost: &f" + Formatter.formatSouls(cost)
		);

		promptForUnlock(previousPanel, confirmLore, confirm);
	}

	public static void promptForUnlock(AGUIPanel previousPanel, ALoreBuilder confirmLore, Consumer<GenericConfirmationPanel> confirm) {
		if(PitSim.isDev()) {
			confirm.accept(null);
			return;
		}

		Consumer<GenericConfirmationPanel> cancel = AGUIPanel::openPreviousGUI;
		ALoreBuilder cancelLore = new ALoreBuilder(
				"&7Click to cancel"
		);

		Misc.promptForConfirmation(previousPanel, ChatColor.DARK_PURPLE, confirmLore, cancelLore, confirm, cancel);
	}

	public static boolean isUnlocked(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(pitPlayer == null || unlock == null || !PitSim.status.isDarkzone()) return false;
		if(unlock instanceof MainProgressionStart) return true;
		return pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id);
	}

	public static boolean isFullyUnlocked(PitPlayer pitPlayer, SkillBranch.Path path) {
		return isUnlocked(pitPlayer, path, 6);
	}

	public static boolean isUnlocked(PitPlayer pitPlayer, SkillBranch.Path path, int level) {
		if(pitPlayer == null || path == null || !PitSim.status.isDarkzone()) return false;
		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(path.skillBranch.getRefName());
		if(skillBranchData == null || !skillBranchData.pathUnlocks.containsKey(path.getRefName())) return false;
		return skillBranchData.pathUnlocks.get(path.getRefName()) >= level;
	}

	public static int getUnlockedLevel(PitPlayer pitPlayer, SkillBranch.Path path) {
		if(pitPlayer == null || path == null || !PitSim.status.isDarkzone()) return 0;
		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(path.skillBranch.getRefName());
		if(skillBranchData == null) return 0;
		return skillBranchData.pathUnlocks.getOrDefault(path.getRefName(), 0);
	}

	public static double getUnlockedEffectAsValue(PitPlayer pitPlayer, SkillBranch skillBranch, SkillBranch.PathPosition position, String refName) {
		int total = 0;
		for(Double value : getUnlockedEffectAsList(pitPlayer, skillBranch, position, refName)) total += value;
		return total;
	}

	public static List<Double> getUnlockedEffectAsList(PitPlayer pitPlayer, SkillBranch skillBranch, SkillBranch.PathPosition position, String refName) {
		List<Double> valueList = new ArrayList<>();
		if(pitPlayer == null || skillBranch == null || position == null || !PitSim.status.isDarkzone()) return valueList;
		SkillBranch.Path path;
		switch(position) {
			case FIRST_PATH:
				path = skillBranch.firstPath;
				break;
			case SECOND_PATH:
				path = skillBranch.secondPath;
				break;
			default:
				throw new RuntimeException();
		}
		int currentLvl = getUnlockedLevel(pitPlayer, path);
		if(currentLvl == 0) return valueList;
		for(SkillBranch.Path.EffectData effectData : path.effectData) {
			if(!effectData.refName.equalsIgnoreCase(refName)) continue;
			for(int i = 0; i < currentLvl; i++) valueList.add(effectData.values[i]);
			break;
		}
		return valueList;
	}

	public static boolean isUnlocked(PitPlayer pitPlayer, SkillBranch skillBranch, SkillBranch.MajorUnlockPosition position) {
		if(pitPlayer == null || skillBranch == null || position == null || !PitSim.status.isDarkzone()) return false;
		DarkzoneData.SkillBranchData skillBranchData = pitPlayer.darkzoneData.skillBranchUnlocks.get(skillBranch.getRefName());
		if(skillBranchData == null) return false;
		SkillBranch.MajorProgressionUnlock unlock;
		switch(position) {
			case FIRST:
				unlock = skillBranch.firstUnlock;
				break;
			case LAST:
				unlock = skillBranch.lastUnlock;
				break;
			case FIRST_PATH:
				unlock = skillBranch.firstPathUnlock;
				break;
			case SECOND_PATH:
				unlock = skillBranch.secondPathUnlock;
				break;
			default:
				throw new RuntimeException();
		}
		return skillBranchData.majorUnlocks.contains(unlock.getRefName());
	}

	public static int getInitialSoulCost(SkillBranch.MajorProgressionUnlock unlock) {
		double multiplier = getMultiplier(unlock.skillBranch.index);
		DarkzoneBalancing.SkillUnlockCost unlockCost;
		switch(unlock.position) {
			case FIRST:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.FIRST;
				break;
			case LAST:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.LAST;
				break;
			case FIRST_PATH:
			case SECOND_PATH:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.MIDDLE;
				break;
			default:
				throw new RuntimeException();
		}
		return (int) (unlockCost.getCost() * multiplier);
	}

	public static int getInitialSoulCost(SkillBranch.Path path, int level) {
		double multiplier = getMultiplier(path.skillBranch.index);
		DarkzoneBalancing.SkillUnlockCost unlockCost;
		switch(level) {
			case 1:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.PATH_1;
				break;
			case 2:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.PATH_2;
				break;
			case 3:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.PATH_3;
				break;
			case 4:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.PATH_4;
				break;
			case 5:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.PATH_5;
				break;
			case 6:
				unlockCost = DarkzoneBalancing.SkillUnlockCost.PATH_6;
				break;
			default:
				throw new RuntimeException();
		}
		return (int) (unlockCost.getCost() * multiplier);
	}

	public static double getMultiplier(int index) {
		return DarkzoneBalancing.BRANCH_DIFFICULTY_MULTIPLIER_INCREASE * index + 1;
	}

	public static class PathGUILocation {
		public int guiXPos;
		public int guiYPos;

		public PathGUILocation(int guiXPos, int guiYPos) {
			this.guiXPos = guiXPos;
			this.guiYPos = guiYPos;
		}
	}
}
