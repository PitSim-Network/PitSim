package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NoKillstreak extends Killstreak {

	public static NoKillstreak INSTANCE;

	public NoKillstreak() {
		super("No killstreak", "NoKillstreak", 0, 0);
		INSTANCE = this;
	}


	@Override
	public void proc(Player player) {

	}

	@Override
	public void reset(Player player) {

	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLD_BLOCK);
		builder.setName("&c" + name);
		builder.setLore(new ALoreBuilder("&7Wanna free up this slot for", "&7some reason?"));

		return builder.getItemStack();
	}
}
