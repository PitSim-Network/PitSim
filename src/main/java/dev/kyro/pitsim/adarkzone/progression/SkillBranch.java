package dev.kyro.pitsim.adarkzone.progression;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class SkillBranch {

	public abstract String getName();
	public abstract String getRefName();

	public abstract ItemStack getBaseStack(); // Just to simply process of having to have methods for both material and data
	public abstract List<String> getDescription();
}
