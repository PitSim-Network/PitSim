package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitPerk;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class NoPerk extends PitPerk {

	public static NoPerk INSTANCE;

	public NoPerk() {
		super("No Perk", new ItemStack(Material.DIAMOND_BLOCK, 1, (short) 0), 50);
		INSTANCE = this;
	}


	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("no perk").getLore();
	}
}
