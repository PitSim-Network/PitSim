package net.pitsim.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.controllers.objects.PitPerk;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Dirty extends PitPerk {
	public static Dirty INSTANCE;

	public Dirty() {
		super("Dirty", "dirty");
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!hasPerk(killEvent.getKiller()) || !killEvent.isDeadPlayer()) return;;
		Misc.applyPotionEffect(killEvent.getKiller(), PotionEffectType.DAMAGE_RESISTANCE, 4 * 20, 1, true, false);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.DIRT, 1, 1)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&7Gain &9Resistance II &7(4s) on player or bot kill"
		);
	}

	@Override
	public String getSummary() {
		return "&aDirty &7is a perk that gives you &9Resistance II &7on player or bot kill";
	}
}
