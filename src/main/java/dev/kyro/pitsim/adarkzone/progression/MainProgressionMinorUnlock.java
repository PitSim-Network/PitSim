package dev.kyro.pitsim.adarkzone.progression;


import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MainProgressionMinorUnlock extends MainProgressionUnlock {

	public MainProgressionMinorUnlock(String id, int guiXPos, int guiYPos) {
		super(id, guiXPos, guiYPos);
	}

	@Override
	public String getDisplayName() {
		return "&5Progression Path";
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, UnlockState unlockState) {
		String costString = ProgressionManager.getUnlockCostFormatted(pitPlayer, this);
		return new AItemStackBuilder(Material.STAINED_GLASS_PANE, unlockState.data)
				.setName(unlockState.chatColor + "Minor Progression Unlock")
				.setLore(new ALoreBuilder(
						"&7Unlock Cost: " + costString + ""
				))
				.getItemStack();
	}
}