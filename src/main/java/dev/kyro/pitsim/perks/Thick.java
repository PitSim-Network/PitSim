package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.PerkEquipEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Thick extends PitPerk {
	public static Thick INSTANCE;

	public Thick() {
		super("Thick", "thick");
		INSTANCE = this;
	}

	@EventHandler
	public void onPerkEquip(PerkEquipEvent event) {
		Player player = event.getPlayer();

		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					pitPlayer.updateMaxHealth();
				} catch(Exception ignored) {
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.APPLE)
				.getItemStack();
	}

	@Override
	public PitLoreBuilder getBaseDescription() {
		return new PitLoreBuilder(
				"&7You have &c+2 max \u2764"
		);
	}

	@Override
	public String getSummary() {
		return "&aThick &7is a perk that grants you &c+2 max \u2764";
	}
}
