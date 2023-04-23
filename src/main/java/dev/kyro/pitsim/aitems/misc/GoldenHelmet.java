package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.HelmetSystem;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.HelmetManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MarketCategory;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoldenHelmet extends PitItem {

	public GoldenHelmet() {
		hasUUID = true;
		hasLastServer = true;
		hasDropConfirm = true;
		hideExtra = true;
		unbreakable = true;
		marketCategory = MarketCategory.MISC;
	}

	@Override
	public String getNBTID() {
		return "golden-helmet";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("helmet", "goldenhelmet", "ghelm"));
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	public Material getMaterial() {
		return Material.GOLD_HELMET;
	}

	public String getName() {
		return "&6Golden Helmet";
	}

	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Used in the mystic well",
				"",
				"&7Kept on death"
		).getLore();
	}

	public ItemStack getItem() {
		ItemStack itemStack = new AItemStackBuilder(getMaterial())
				.setName(getName())
				.setLore(getLore())
				.getItemStack();
		itemStack = buildItem(itemStack);

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setLong(NBTTag.GHELMET_GOLD.getRef(), 0L);
		nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), null);
		itemStack = nbtItem.getItem();

		updateItem(itemStack);
		return itemStack;
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!defaultUpdateItem(itemStack)) return;

		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore("");
		HelmetAbility ability = HelmetManager.getAbility(itemStack);
		long gold = HelmetManager.getHelmetGold(itemStack);

		if(ability != null) {
			loreBuilder.addLore("&7Ability: &9" + ability.name);
			if(PitSim.status.isOverworld()) {
				loreBuilder.addLore(ability.getDescription());
			} else {
				loreBuilder.addLore("&cDISABLED IN DARKZONE");
			}
		} else loreBuilder.addLore("&7Ability: &cNONE");
		loreBuilder.addLore("", "&7Passives:");
		int passives = 0;
		for(HelmetSystem.Passive passive : HelmetSystem.Passive.values()) {
			int level = HelmetSystem.getLevel(gold);
			int passiveLevel = HelmetSystem.getTotalStacks(passive, level - 1);

			if(passiveLevel == 0) continue;
			passives++;

			if(passive == HelmetSystem.Passive.DAMAGE_REDUCTION) {
				loreBuilder.addLore(passive.color + "-" + passiveLevel * passive.baseUnit + "% " + passive.refName);
				continue;
			}
			loreBuilder.addLore(passive.color + "+" + passiveLevel * passive.baseUnit + "% " + passive.refName);
		}
		if(passives == 0) loreBuilder.addLore("&cNONE");
		loreBuilder.addLore("", "&7Gold: &6" + HelmetManager.formatter.format(gold) + "g", "", "&eShift right-click to modify!");

		new AItemStackBuilder(itemStack)
				.setLore(loreBuilder);
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		NBTItem newNBTItem = new NBTItem(getItem());
		newNBTItem.setLong(NBTTag.GHELMET_GOLD.getRef(), nbtItem.getLong(NBTTag.GHELMET_GOLD.getRef()));
		newNBTItem.setString(NBTTag.GHELMET_ABILITY.getRef(), nbtItem.getString(NBTTag.GHELMET_ABILITY.getRef()));

		updateItem(newNBTItem.getItem());
		return newNBTItem.getItem();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.GHELMET_UUID.getRef());
	}
}
