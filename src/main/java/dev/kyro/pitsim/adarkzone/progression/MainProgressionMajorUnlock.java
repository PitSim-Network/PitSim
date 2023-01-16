package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.inventory.ItemStack;

public class MainProgressionMajorUnlock extends MainProgressionUnlock {
	public SkillBranch skillBranch;

	public MainProgressionMajorUnlock(Class<? extends SkillBranch> clazz, int guiXPos, int guiYPos) {
		super(ProgressionManager.getSkillBranch(clazz).getRefName(), guiXPos, guiYPos);
		this.skillBranch = ProgressionManager.getSkillBranch(clazz);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, UnlockState unlockState) {
		return skillBranch.getMainDisplayStack(pitPlayer, this, unlockState);
	}

	@Override
	public String getDisplayName() {
		return skillBranch.getDisplayName();
	}
}