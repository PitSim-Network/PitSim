package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class Leap extends HelmetAbility {
	public Leap(Player player) {

		super(player,"Leap", "leap", false, 10);
	}


	@Override
	public void onActivate() {

	}

	@Override
	public void onDeactivate() {

	}

	@Override
	public void onProc() {
		Bukkit.broadcastMessage("Leap!");

	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Test", "&6Test");
	}

	@Override
	public ItemStack getDisplayItem() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		AItemStackBuilder builder = new AItemStackBuilder(Material.RABBIT_FOOT);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		loreBuilder.addLore("", "&7Cost: &6" + formatter.format(10000) + "g");
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}
}
