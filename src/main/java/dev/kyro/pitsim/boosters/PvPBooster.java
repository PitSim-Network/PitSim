package dev.kyro.pitsim.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PvPBooster extends Booster {
	public static PvPBooster INSTANCE;

	public PvPBooster() {
		super("PvP Booster", "pvp", 13, ChatColor.RED);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.GOLD_SWORD)
				.setLore(new PitLoreBuilder(
						"&7All players can use &3Jewel &7items without losing lives"
				)).getItemStack();
	}
}
