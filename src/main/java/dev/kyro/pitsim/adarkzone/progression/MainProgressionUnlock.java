package dev.kyro.pitsim.adarkzone.progression;


import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
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

	public abstract String getDisplayName();
	public abstract ItemStack getDisplayStack(PitPlayer pitPlayer, UnlockState unlockState);

	public int getSlot() {
		return (guiYPos - 1) * 9 + guiXPos - 1;
	}
}