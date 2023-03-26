package dev.kyro.pitsim.aitems.diamond;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiamondHelmet extends StaticPitItem implements TemporaryItem {

	public DiamondHelmet() {
		isShopDiamond = true;
	}

	@Override
	public String getNBTID() {
		return "diamond-boots";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("dhelmet"));
	}

	@Override
	public Material getMaterial() {
		return Material.DIAMOND_BOOTS;
	}

	@Override
	public String getName() {
		return "&7Diamond Helmet";
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
		return itemStack.getType() == Material.DIAMOND_HELMET && itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) == 0;
	}

	@Override
	public TemporaryType getTemporaryType() {
		return TemporaryType.LOST_ON_DEATH;
	}
}
