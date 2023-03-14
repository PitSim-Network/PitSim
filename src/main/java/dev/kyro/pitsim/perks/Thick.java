package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.PerkEquipEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Thick extends PitPerk {

	public static Thick INSTANCE;

	public Thick() {
		super("Thick", "thick", new ItemStack(Material.APPLE, 1, (short) 0), 14, false, "", INSTANCE, false);
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
	public List<String> getDescription() {
		return new ALoreBuilder("&7You have &c+2 max \u2764&7.").getLore();
	}

	@Override
	public String getSummary() {
		return "&aThick &7is a perk that grants you &c+2 max \u2764";
	}
}
