package dev.kyro.pitsim.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Booster;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChaosBooster extends Booster {
	public static ChaosBooster INSTANCE;

	public ChaosBooster() {
		super("Chaos Booster", "chaos", 16, ChatColor.GREEN);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		return new AItemStackBuilder(Material.TNT)
				.setLore(new ALoreBuilder(
						"&7Double the amount bots in middle"
				)).getItemStack();
	}
}
