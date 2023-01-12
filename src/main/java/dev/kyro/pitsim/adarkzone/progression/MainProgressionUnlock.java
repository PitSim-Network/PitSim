package dev.kyro.pitsim.adarkzone.progression;


import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import org.bukkit.inventory.ItemStack;

public abstract class MainProgressionUnlock {
	public String id;
	public int guiXPos;
	public int guiYPos;

	public MainProgressionUnlock(String id, int guiXPos, int guiYPos) {
		this.id = id;
		this.guiXPos = guiXPos;
		this.guiYPos = guiYPos;
	}

	public abstract ItemStack getDisplayStack(UnlockState unlockState);
	public abstract int getUnlockCost();

	public String getUnlockCostFormatted() {
		return "&f" + getUnlockCost() + " soul" + (getUnlockCost() == 1 ? "" : "s");
	}
}