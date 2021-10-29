package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Test2 extends Killstreak {

	public static Test2 INSTANCE;

	public Test2() {
		super("Test2", "Test2", 5, 5);
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

		AItemStackBuilder builder = new AItemStackBuilder(Material.ENDER_PEARL);
		builder.setName("&c" + name);
		builder.setLore(new ALoreBuilder("&7Wanna free up this slot for", "&7some reason?"));

		return builder.getItemStack();
	}
}
