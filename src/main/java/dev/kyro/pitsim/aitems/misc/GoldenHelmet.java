package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.HelmetSystem;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.HelmetManager;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GoldenHelmet extends PitItem {

	public GoldenHelmet() {
		hideExtra = true;
		unbreakable = true;

		itemEnchants.put(Enchantment.WATER_WORKER, 1);
	}

	@Override
	public String getNBTID() {
		return "mystic-bow";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("bow", "mysticbow"));
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
		nbtItem.setBoolean(NBTTag.IS_GHELMET.getRef(), true);
		nbtItem.setInteger(NBTTag.GHELMET_GOLD.getRef(), 0);
		nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), null);
		nbtItem.setString(NBTTag.GHELMET_UUID.getRef(), UUID.randomUUID().toString());
		itemStack = nbtItem.getItem();

		updateItem(itemStack);
		return itemStack;
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!isThisItem(itemStack)) throw new RuntimeException();

		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore("");
		HelmetAbility ability = HelmetManager.getAbility(itemStack);
		long gold = HelmetManager.getHelmetGold(itemStack);

		if(ability != null) {
			loreBuilder.addLore("&7Ability: &9" + ability.name);
			loreBuilder.addLore(ability.getDescription());
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
				.setLore(loreBuilder)
				.getItemStack();

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setLong(NBTTag.GHELMET_GOLD.getRef(), gold);
		if(ability != null) nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), ability.refName);
	}
}
