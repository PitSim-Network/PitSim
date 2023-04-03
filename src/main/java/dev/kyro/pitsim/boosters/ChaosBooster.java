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
		super("Chaos Booster", "chaos", 15, ChatColor.GREEN);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.TNT)
				.setLore(new ALoreBuilder(
						"&7Doubles the number of bots",
						"&7in middle"
				)).getItemStack();
	}
}
