package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Explicious extends Killstreak {

	public static Explicious INSTANCE;

	public Explicious() {
		super("Explicious", "Explicious", 5, 0);
		INSTANCE = this;
	}


	@Override
	public void proc(Player player) {

	}

	@Override
	public void reset(Player player) {

	}

	@Override
	public ItemStack getDisplayItem() {

		AItemStackBuilder builder = new AItemStackBuilder(Material.FISHING_ROD);
		builder.setName("&c" + name);
		builder.setLore(new ALoreBuilder("&7Wanna free up this slot for", "&7some reason?"));

		return builder.getItemStack();
	}
}
