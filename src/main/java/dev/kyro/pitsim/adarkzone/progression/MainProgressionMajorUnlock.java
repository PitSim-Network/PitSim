package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.inventory.ItemStack;

public class MainProgressionMajorUnlock extends MainProgressionUnlock {
	public SkillBranch skillBranch;

	public MainProgressionMajorUnlock(SkillBranch skillBranch, int guiXPos, int guiYPos) {
		super(skillBranch.getRefName(), guiXPos, guiYPos);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, UnlockState unlockState) {
		return skillBranch.getDisplayStack(pitPlayer, this, unlockState);
	}

	@Override
	public String getDisplayName() {
		return null;
	}
}