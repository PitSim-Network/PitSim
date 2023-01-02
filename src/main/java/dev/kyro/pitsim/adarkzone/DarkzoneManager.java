package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.adarkzone.sublevels.ZombieSubLevel;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DarkzoneManager implements Listener {
	public static List<SubLevel> subLevels = new ArrayList<>();

	static {
		registerSubLevel(new ZombieSubLevel());
	}

	public static PitEquipment getDefaultEquipment() {
		return new PitEquipment()
				.held(new ItemStack(Material.DIAMOND_SWORD))
				.helmet(new ItemStack(Material.DIAMOND_SWORD))
				.chestplate(new ItemStack(Material.DIAMOND_SWORD))
				.leggings(new ItemStack(Material.DIAMOND_SWORD))
				.leggings(new ItemStack(Material.DIAMOND_SWORD))
				.boots(new ItemStack(Material.DIAMOND_SWORD));
	}

	public static void registerSubLevel(SubLevel subLevel) {
		subLevels.add(subLevel);
	}

	public static SubLevel getSubLevel(Class<? extends SubLevel> clazz) {
		for(SubLevel subLevel : subLevels) if(subLevel.getClass() == clazz) return subLevel;
		return null;
	}
}
