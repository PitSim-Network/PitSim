package net.pitsim.spigot.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.Booster;
import net.pitsim.spigot.misc.PitLoreBuilder;
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
						"&7All players can use items without losing lives. Also prevents losing &fsouls &7on death"
				)).getItemStack();
	}
}
