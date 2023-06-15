package net.pitsim.spigot.items.diamond;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.items.StaticPitItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiamondBoots extends StaticPitItem {

	public DiamondBoots() {
		isShopDiamond = true;
	}

	@Override
	public String getNBTID() {
		return "diamond-boots";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("dboots"));
	}

	@Override
	public Material getMaterial() {
		return Material.DIAMOND_BOOTS;
	}

	@Override
	public String getName() {
		return "&8Diamond Boots";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Default armor piece",
				"",
				"&7Kept on death"
		).getLore();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return itemStack.getType() == Material.DIAMOND_BOOTS && itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) == 0;
	}
}
