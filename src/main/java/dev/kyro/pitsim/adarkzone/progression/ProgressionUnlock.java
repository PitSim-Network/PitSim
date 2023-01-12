package dev.kyro.pitsim.adarkzone.progression;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ProgressionUnlock {

	public abstract String getName();
	public abstract List<String> getDescription();

	public abstract ItemStack getDisplayStack();
}
