package dev.kyro.pitsim.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Booster;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SoulBooster extends Booster {
	public static SoulBooster INSTANCE;

	public SoulBooster() {
		super("Soul Booster", "darkzone", 17, ChatColor.WHITE);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		return new AItemStackBuilder(Material.GHAST_TEAR)
				.setLore(new ALoreBuilder(
						"&7Gain &f+" + getSoulsIncrease() + "% souls &7from mobs",
						"&7and bosses in the &5Darkzone"
				)).getItemStack();
	}

	public static int getSoulsIncrease() {
		return 50;
	}
}
