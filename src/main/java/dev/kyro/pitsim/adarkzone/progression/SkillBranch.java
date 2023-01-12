package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkillBranch {
	public static List<Integer> pathRows = Arrays.asList(1, 3);

	public MajorProgressionUnlock firstUnlock;
	public MajorProgressionUnlock lastUnlock;

	public Path firstPath;
	public Path secondPath;

	public SkillBranch(MajorProgressionUnlock firstUnlock, MajorProgressionUnlock lastUnlock) {
		this.firstUnlock = firstUnlock;
		this.lastUnlock = lastUnlock;

		this.firstPath = createFirstPath();
		this.secondPath = createSecondPath();
	}

	public abstract String getName();
	public abstract String getRefName();

	public abstract Path createFirstPath();
	public abstract Path createSecondPath();

	public abstract ItemStack getBaseStack(); // Just to simply process of having to have methods for both material and data
	public abstract List<String> getDescription();
	public abstract int getUnlockCost();

	public ItemStack getDisplayStack(UnlockState unlockState) {
		String displayName = "";
		ALoreBuilder loreBuilder = new ALoreBuilder();

		return new AItemStackBuilder(getBaseStack())
				.setName(displayName)
				.setLore(loreBuilder)
				.getItemStack();
	}

	public static class Path {
		public List<ProgressionUnlock> unlocks = new ArrayList<>();

		public Path() {

		}

		public Path addUnlock(ProgressionUnlock unlock) {
			unlocks.add(unlock);
			return this;
		}
	}
}
