package net.pitsim.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.objects.PitPerk;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.events.PerkEquipEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
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
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				pitPlayer.updateMaxHealth();
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.APPLE)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&7You have &c+2 max \u2764"
		);
	}

	@Override
	public String getSummary() {
		return "&aThick &7is a perk that grants you &c+2 max \u2764";
	}
}
