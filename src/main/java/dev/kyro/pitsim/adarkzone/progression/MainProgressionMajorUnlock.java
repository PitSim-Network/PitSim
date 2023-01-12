package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import org.bukkit.inventory.ItemStack;

public class MainProgressionMajorUnlock extends MainProgressionUnlock {
	public SkillBranch skillBranch;

	public MainProgressionMajorUnlock(SkillBranch skillBranch, int guiXPos, int guiYPos) {
		super(skillBranch.getRefName(), guiXPos, guiYPos);
	}

	@Override
	public ItemStack getDisplayStack(UnlockState unlockState) {
		return skillBranch.getDisplayStack(unlockState);
	}

	@Override
	public int getUnlockCost() {
		return skillBranch.getUnlockCost();
	}
}