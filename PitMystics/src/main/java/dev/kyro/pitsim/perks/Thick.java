package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitPerk;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.PerkEquipEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Thick extends PitPerk {

	public static Thick INSTANCE;

	public Thick() {
		super("Thick", new ItemStack(Material.APPLE, 1, (short) 0), 14);
		INSTANCE = this;
	}

	@EventHandler
	public void onPerkEquip(PerkEquipEvent event) {
		PitPerk perk = event.getPerk();
		Player player = event.getPlayer();
		PitPerk replacedPerk = event.getReplacedPerk();

		if(perk == INSTANCE) {
			player.setMaxHealth(player.getMaxHealth() + 4);
			player.setHealth(player.getMaxHealth());
		}

		if(replacedPerk == INSTANCE) {
			player.setMaxHealth(player.getMaxHealth() - 4);
		}
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7You have &c+2 Max \u2764&7.").getLore();
	}
}
