package net.pitsim.spigot.items.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.items.StaticPitItem;
import net.pitsim.spigot.enums.MarketCategory;
import net.pitsim.spigot.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AncientGemShard extends StaticPitItem {

	public AncientGemShard() {
		hasDropConfirm = true;
		marketCategory = MarketCategory.PURE_RELATED;
	}

	@Override
	public String getNBTID() {
		return "gem-shard";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("gemshard", "shard"));
	}

	@Override
	public Material getMaterial() {
		return Material.PRISMARINE_SHARD;
	}

	@Override
	public String getName() {
		return "&aAncient Gem Shard";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&eSpecial item",
				"&7A piece of a relic lost to time.",
				"&7Find enough shards and you may be",
				"&7able to craft an item of great power"
		).getLore();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.IS_SHARD.getRef());
	}
}
