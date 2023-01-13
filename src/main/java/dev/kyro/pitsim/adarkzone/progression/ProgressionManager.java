package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ProgressionManager implements Listener {
	public static List<SkillBranch> skillBranches = new ArrayList<>();
	public static List<MainProgressionUnlock> mainProgressionUnlocks = new ArrayList<>();

	static {
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
	}

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

	public static int getUnlockCost(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		int unlocks = pitPlayer.darkzoneData.mainProgressionUnlocks.size();
		int cost = (unlocks + 1) * 10;
		if(unlock instanceof MainProgressionMajorUnlock) cost *= 2;
		return cost;
	}

	public static String getUnlockCostFormatted(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		int cost = getUnlockCost(pitPlayer, unlock);
		return "&f" + cost + " soul" + (cost == 1 ? "" : "s");
	}

	public static <T extends SkillBranch> T getSkillBranch(Class<T> clazz) {
		for(SkillBranch skillBranch : skillBranches) if(skillBranch.getClass() == clazz) return (T) skillBranch;
		throw new RuntimeException();
	}

	public static void unlock(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id)) throw new RuntimeException();
		pitPlayer.darkzoneData.mainProgressionUnlocks.add(unlock.id);
		AOutput.send(pitPlayer.player, "&5&lDARKZONE!&7 You unlocked + " + unlock.getDisplayName());
	}

	public static boolean isUnlocked(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(unlock == null) return false;
		if(unlock instanceof MainProgressionStart) return true;
		return pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id);
	}
}
