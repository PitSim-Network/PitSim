package net.pitsim.spigot.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.aitems.StaticPitItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arrow extends StaticPitItem {

	public Arrow() {
		destroyIfDroppedInSpawn = true;
	}

	@Override
	public String getNBTID() {
		return "arrow";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("arrow"));
	}

	@Override
	public Material getMaterial() {
		return Material.ARROW;
	}

	@Override
	public String getName() {
		return "&fArrow";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Just a plain Arrow"
		).getLore();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return false;
	}
}
