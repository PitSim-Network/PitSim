package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NoPerk extends PitPerk {
	public static NoPerk INSTANCE;

	public NoPerk() {
		super("No Perk", "none");
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.DIAMOND_BLOCK)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&7Are you hardcore enough that you don't need any perk in this slot?"
		);
	}

	@Override
	public String getSummary() {
		return null;
	}
}
