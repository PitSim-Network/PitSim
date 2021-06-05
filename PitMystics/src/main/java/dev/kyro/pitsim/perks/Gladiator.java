package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitPerk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Gladiator extends PitPerk {

	public static Gladiator INSTANCE;

	public Gladiator() {
		super("Gladiator", new ItemStack(Material.BONE, 1, (short) 0), 13);
		INSTANCE = this;
	}


	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Receive &9-3% &7damage per", "&7nearby player.", "", "&712 blocks range.", "&7Minimum 3, max 10 players.").getLore();
	}
}
