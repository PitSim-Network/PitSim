package dev.kyro.pitsim.adarkzone.progression;


import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MainProgressionMinorUnlock extends MainProgressionUnlock {
	public int cost;

	public MainProgressionMinorUnlock(String id, int guiXPos, int guiYPos, int cost) {
		super(id, guiXPos, guiYPos);
		this.cost = cost;
	}

	@Override
	public ItemStack getDisplayStack(UnlockState unlockState) {
		return new AItemStackBuilder(Material.STAINED_GLASS_PANE, unlockState.data)
				.setName(unlockState.chatColor + "Minor Progression Unlock")
				.setLore(new ALoreBuilder(
						"&7Unlock Cost: " + getUnlockCostFormatted() + ""
				))
				.getItemStack();
	}

	@Override
	public int getUnlockCost() {
		return cost;
	}
}