package dev.kyro.pitsim.acosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SolidCape extends PitCosmetic {

	public SolidCape() {
		super("&fSolid Cape", "solidcape", CosmeticType.CAPE);
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				System.out.println("running");
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		runnable.cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BANNER, 1, 15)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
