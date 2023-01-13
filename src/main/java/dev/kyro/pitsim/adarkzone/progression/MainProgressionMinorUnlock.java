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
		int cost = ProgressionManager.getUnlockCost(pitPlayer, this);
		String costString = ProgressionManager.getUnlockCostFormatted(pitPlayer, this);
		ALoreBuilder loreBuilder = new ALoreBuilder();

		if(unlockState == UnlockState.LOCKED) {
			loreBuilder.addLore("&cCannot be unlocked yet");
		} else if(unlockState == UnlockState.NEXT_TO_UNLOCK) {
			loreBuilder.addLore("&7Unlock Cost: " + costString, "");
			if(pitPlayer.taintedSouls < cost) {
				loreBuilder.addLore("&cNot enough souls");
			} else {
				loreBuilder.addLore("&eClick to purchase!");
			}
		} else if(unlockState == UnlockState.UNLOCKED) {
			loreBuilder.addLore("&aAlready unlocked!");
		}

		return new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, unlockState.data)
				.setName(unlockState.chatColor + "Path Unlock")
				.setLore(loreBuilder)
				.getItemStack();
	}
}