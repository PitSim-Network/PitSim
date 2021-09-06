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
		super("Thick", "thick", new ItemStack(Material.APPLE, 1, (short) 0), 14, false, "", INSTANCE);
		INSTANCE = this;
	}

	@EventHandler
	public void onPerkEquip(PerkEquipEvent event) {
		Player player = event.getPlayer();
//		PitPerk perk = event.getPerk();
//		PitPerk replacedPerk = event.getReplacedPerk();

		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					pitPlayer.updateMaxHealth();
				} catch(Exception ignored) { }
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7You have &c+2 Max \u2764&7.").getLore();
	}
}
