package net.pitsim.spigot.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.Booster;
import net.pitsim.spigot.misc.PitLoreBuilder;
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
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.GHAST_TEAR)
				.setLore(new PitLoreBuilder(
						"&7Gain &f+" + getSoulsIncrease() + "% souls &7from mobs and bosses in the &5Darkzone"
				)).getItemStack();
	}

	public static int getSoulsIncrease() {
		return 50;
	}
}
