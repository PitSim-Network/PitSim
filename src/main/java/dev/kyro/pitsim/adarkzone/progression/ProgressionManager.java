package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ProgressionManager implements Listener {
	public static List<SkillBranch> skillBranches = new ArrayList<>();
	public static List<MainProgressionUnlock> mainProgressionUnlocks = new ArrayList<>();

	static {
		registerMainUnlock(new MainProgressionMinorUnlock("1", 4, 3, 100));
	}

	public static void registerBranch(SkillBranch skillBranch) {
		skillBranches.add(skillBranch);
	}

	public static void registerMainUnlock(MainProgressionUnlock mainUnlock) {
		mainProgressionUnlocks.add(mainUnlock);
	}

	public MainProgressionUnlock getMainProgressionUnlock(int guiXPos, int guiYPos) {
		for(MainProgressionUnlock unlock : mainProgressionUnlocks)
			if(unlock.guiXPos == guiXPos && unlock.guiYPos == guiYPos) return unlock;
		return null;
	}

	public UnlockState getUnlockState(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(isUnlocked(pitPlayer, unlock)) return UnlockState.UNLOCKED;
		if(
				isUnlocked(pitPlayer, getMainProgressionUnlock(unlock.guiXPos + 0, unlock.guiYPos + 1)) ||
				isUnlocked(pitPlayer, getMainProgressionUnlock(unlock.guiXPos + 0, unlock.guiYPos - 1)) ||
				isUnlocked(pitPlayer, getMainProgressionUnlock(unlock.guiXPos + 1, unlock.guiYPos + 0)) ||
				isUnlocked(pitPlayer, getMainProgressionUnlock(unlock.guiXPos - 1, unlock.guiYPos + 0))
		) return UnlockState.NEXT_TO_UNLOCK;
		return UnlockState.LOCKED;
	}

	public static <T extends SkillBranch> T getSkillBranch(Class<T> clazz) {
		for(SkillBranch skillBranch : skillBranches) if(skillBranch.getClass() == clazz) return (T) skillBranch;
		throw new RuntimeException();
	}

	public static void unlock(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id)) throw new RuntimeException();
		pitPlayer.darkzoneData.mainProgressionUnlocks.add(unlock.id);
	}

	public static boolean isUnlocked(PitPlayer pitPlayer, MainProgressionUnlock unlock) {
		if(unlock == null) return false;
		return pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id);
	}
}
