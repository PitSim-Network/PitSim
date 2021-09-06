package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Dirty extends PitPerk {

	public static Dirty INSTANCE;

	public Dirty() {
		super("Dirty", "dirty", new ItemStack(Material.DIRT, 1, (short) 1), 11, false, "", INSTANCE);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		if(!playerHasUpgrade(killEvent.killer)) return;

		Misc.applyPotionEffect(killEvent.killer, PotionEffectType.DAMAGE_RESISTANCE, 4 * 20, 1, true, false);
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Gain Resistance II (4s) on kill.").getLore();
	}
}
