package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NoMegastreak extends Megastreak {
	public static NoMegastreak INSTANCE;

	public NoMegastreak() {
		super("&7No Megastreak", "nomegastreak", Integer.MAX_VALUE, 0, 0);
		INSTANCE = this;
	}

	@Override
	public String getPrefix(Player player) {
		return null;
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.REDSTONE_BLOCK)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&7Go on high streaks with no reward modifiers and no debuffs"
		);
	}

	@Override
	public String getSummary() {
		return null;
	}
}
