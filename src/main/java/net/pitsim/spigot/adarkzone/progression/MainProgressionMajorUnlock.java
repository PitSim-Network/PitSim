package net.pitsim.spigot.adarkzone.progression;

import net.pitsim.spigot.adarkzone.notdarkzone.UnlockState;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.inventory.ItemStack;

public class MainProgressionMajorUnlock extends MainProgressionUnlock {
	public SkillBranch skillBranch;

	public MainProgressionMajorUnlock(Class<? extends SkillBranch> clazz, int index) {
		super(ProgressionManager.getSkillBranch(clazz).getRefName(), ProgressionManager.branchSlots.get(index).guiXPos,
				ProgressionManager.branchSlots.get(index).guiYPos);
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