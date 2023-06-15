package net.pitsim.spigot.aitems.mystics;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.aitems.MysticFactory;
import net.pitsim.spigot.aitems.StaticPitItem;
import net.pitsim.spigot.aitems.TemporaryItem;
import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.MarketCategory;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaintedScythe extends StaticPitItem implements TemporaryItem {

	public TaintedScythe() {
		hasUUID = true;
		hasLastServer = true;
		hasDropConfirm = true;
		hideExtra = true;
		unbreakable = true;
		hasEnchantGlint = true;
		isMystic = true;
		marketCategory = MarketCategory.DARKZONE_GEAR;
	}

	@Override
	public String getNBTID() {
		return "tainted-scythe";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("scythe", "taintedscythe"));
	}

	@Override
	public Material getMaterial() {
		return Material.GOLD_HOE;
	}

	@Override
	public String getName() {
		return "&5Fresh Tainted Scythe";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Used in the tainted well",
				"",
				"&7Kept on death"

		).getLore();
	}

	@Override
	public ItemStack getItem() {
		ItemStack itemStack = new AItemStackBuilder(getMaterial())
				.setName(getName())
				.setLore(getLore())
				.getItemStack();
		itemStack = buildItem(itemStack);

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.addCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		return nbtItem.getItem();
	}

	@Override
	public ItemStack getItem(int amount) {
		if(amount == 1) return getItem();
		throw new RuntimeException();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!defaultUpdateItem(itemStack)) return;
		boolean hasLives = MysticFactory.hasLives(itemStack);

		NBTItem nbtItem = new NBTItem(itemStack);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef());
		if(enchantNum == 0) {
			new AItemStackBuilder(itemStack)
					.setName(getName())
					.setLore(getLore());
			return;
		}

		if(getLives(itemStack) == 0 && hasLives) {
			itemStack.setType(Material.STONE_HOE);
			Misc.removeEnchantGlint(itemStack);
		} else {
			itemStack.setType(Material.GOLD_HOE);
			Misc.addEnchantGlint(itemStack);
		}

		EnchantManager.setItemLore(itemStack, null);
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		pitPlayer.giveSouls(30);
		return null;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.TAINTED_TIER.getRef()) && (itemStack.getType() == Material.GOLD_HOE || itemStack.getType() == Material.STONE_HOE);
	}

	@Override
	public TemporaryType getTemporaryType() {
		return TemporaryType.LOOSES_LIVES_ON_DEATH;
	}
}
