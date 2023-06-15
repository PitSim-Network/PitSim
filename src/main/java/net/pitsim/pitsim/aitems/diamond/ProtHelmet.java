package net.pitsim.pitsim.aitems.diamond;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.aitems.StaticPitItem;
import net.pitsim.pitsim.aitems.TemporaryItem;
import net.pitsim.pitsim.enums.MarketCategory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProtHelmet extends StaticPitItem implements TemporaryItem {

	public ProtHelmet() {
		hasDropConfirm = true;
		hideExtra = true;
		isProtDiamond = true;
		marketCategory = MarketCategory.MISC;

		itemEnchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
	}

	@Override
	public String getNBTID() {
		return "protection-helmet";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("p1helmet", "helmet"));
	}

	@Override
	public Material getMaterial() {
		return Material.DIAMOND_HELMET;
	}

	@Override
	public String getName() {
		return "&bProtection I Helmet";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7A relic back from when knights",
				"&7had shining armor",
				"",
				"&cLost on death"
		).getLore();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return itemStack.getType() == Material.DIAMOND_HELMET && itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) != 0;
	}

	@Override
	public TemporaryItem.TemporaryType getTemporaryType() {
		return TemporaryType.LOST_ON_DEATH;
	}
}
