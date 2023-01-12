package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ProgressionManager implements Listener {
	public static List<SkillBranch> skillBranches = new ArrayList<>();
	public static List<MainProgressionPanel.MainProgressionUnlock> mainProgressionUnlocks = new ArrayList<>();

	public static void registerBranch(SkillBranch skillBranch) {
		skillBranches.add(skillBranch);
	}

	public static void registerMainUnlock(MainProgressionPanel.MainProgressionUnlock mainUnlock) {
		mainProgressionUnlocks.add(mainUnlock);
	}

	public MainProgressionPanel.MainProgressionUnlock getMainProgressionUnlock(int guiXPos, int guiYPos) {
		for(MainProgressionPanel.MainProgressionUnlock unlock : mainProgressionUnlocks)
			if(unlock.guiXPos == guiXPos && unlock.guiYPos == guiYPos) return unlock;
		return null;
	}

//	public UnlockState getUnlockState(MainProgressionPanel.MainProgressionUnlock unlock) {
//		if(unlock)
//	}

	public static <T extends SkillBranch> T getSkillBranch(Class<T> clazz) {
		for(SkillBranch skillBranch : skillBranches) if(skillBranch.getClass() == clazz) return (T) skillBranch;
		throw new RuntimeException();
	}
	
	public static void unlock(MainProgressionPanel.MainProgressionUnlock unlock, PitPlayer pitPlayer) {
		if(pitPlayer.darkzoneData.mainProgressionUnlocks.contains(unlock.id)) throw new RuntimeException();
		pitPlayer.darkzoneData.mainProgressionUnlocks.add(unlock.id);
	}
}
