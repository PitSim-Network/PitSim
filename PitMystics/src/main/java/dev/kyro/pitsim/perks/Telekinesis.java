package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Telekinesis extends PitPerk {

	public static Telekinesis INSTANCE;

	public Telekinesis() {
		super("Telekinesis", "telekinesis", new ItemStack(Material.ROTTEN_FLESH, 1, (short) 1), 20, false, "", INSTANCE);
		INSTANCE = this;
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Drops from mobs in the", "&5Darkzone &7go directly into", "&7your inventory").getLore();
	}
}
