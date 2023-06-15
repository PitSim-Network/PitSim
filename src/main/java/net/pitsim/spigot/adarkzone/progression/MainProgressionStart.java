package net.pitsim.spigot.adarkzone.progression;


import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.adarkzone.notdarkzone.UnlockState;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MainProgressionStart extends MainProgressionUnlock {

	public MainProgressionStart(String id, int guiXPos, int guiYPos) {
		super(id, guiXPos, guiYPos);
	}

	@Override
	public String getDisplayName() {
		return "&5Progression Start";
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, UnlockState unlockState) {
		return new AItemStackBuilder(Material.BEACON, 1)
				.setName("&5Progression Start")
				.setLore(new ALoreBuilder(
						"&7Start your journey here and",
						"&7build your skillset"
				))
				.getItemStack();
	}
}