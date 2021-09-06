package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NoPerk extends PitPerk {

	public static NoPerk INSTANCE;

	public NoPerk() {
		super("No Perk", "none", new ItemStack(Material.DIAMOND_BLOCK, 1, (short) 0), 50, false, "", INSTANCE);
		INSTANCE = this;
	}


	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("no perk").getLore();
	}
}
