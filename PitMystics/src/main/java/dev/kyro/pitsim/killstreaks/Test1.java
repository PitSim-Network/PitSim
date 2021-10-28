package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Test1 extends Killstreak {

	public static Test1 INSTANCE;

	public Test1() {
		super("Test1", "Test1", 5, 5);
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

		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLD_BLOCK);
		builder.setName("&c" + name);
		builder.setLore(new ALoreBuilder("&7Wanna free up this slot for", "&7some reason?"));

		return builder.getItemStack();
	}
}
