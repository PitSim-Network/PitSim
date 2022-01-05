package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HelmetAbilityPanel extends AGUIPanel {

	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	ItemStack goldenHelmet = getHelm();
	public HelmetGUI helmetGUI;
	public HelmetAbilityPanel(AGUI gui) {
		super(gui);
		helmetGUI = (HelmetGUI) gui;

	}

	@Override
	public String getName() {
		return "Choose an Ability";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			ItemStack helm = player.getItemInHand();

			if(slot == 22) {
				openPreviousGUI();
			}

			if(slot == 9) {
				NBTItem nbtItem = new NBTItem(goldenHelmet);
				nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), null);
				GoldenHelmet.setLore(nbtItem.getItem());
				player.getInventory().setItemInHand(nbtItem.getItem());

				player.setItemInHand(nbtItem.getItem());
				if(GoldenHelmet.abilities.containsKey(player)) GoldenHelmet.abilities.get(player).unload();
				GoldenHelmet.abilities.remove(player);
				Sounds.SUCCESS.play(player);
				openPreviousGUI();
			}

			for(HelmetAbility helmetAbility : HelmetAbility.helmetAbilities) {
				if(slot != helmetAbility.slot) continue;

				ItemStack goldenHelmet = getHelm();

				if(goldenHelmet == null) {
					player.closeInventory();
					return;
				}
				HelmetAbility currentAbility = getAbility(goldenHelmet);
				if(currentAbility != null && currentAbility.refName.equals(helmetAbility.refName)) {
					AOutput.error(player, "&aYou already have that ability selected!");
					Sounds.NO.play(player);
					return;
				}

				Sounds.SUCCESS.play(player);
				NBTItem nbtItem = new NBTItem(getHelm());
				nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), helmetAbility.refName);

				GoldenHelmet.setLore(nbtItem.getItem());
				player.getInventory().setItemInHand(nbtItem.getItem());
				openPreviousGUI();
			}

		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		for(HelmetAbility helmetAbility : HelmetAbility.helmetAbilities) {
			AItemStackBuilder builder = new AItemStackBuilder(helmetAbility.getDisplayItem());
			ALoreBuilder loreBuilder = new ALoreBuilder();

			ItemStack goldenHelmet = getHelm();
			if(goldenHelmet == null) {
				player.closeInventory();
				return;
			}
			loreBuilder.addLore(helmetAbility.getDescription());
			HelmetAbility currentAbility = getAbility(goldenHelmet);
			if(currentAbility != null) {
				if(!currentAbility.refName.equals(helmetAbility.refName)) {
					builder.setName("&e" + helmetAbility.name);
					loreBuilder.addLore("", "&eClick to select!");
				} else {
					builder.setName("&a" + helmetAbility.name);
					loreBuilder.addLore("", "&aAlready selected!");
					ItemMeta meta = builder.getItemStack().getItemMeta();
					meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					builder.getItemStack().setItemMeta(meta);
				}
			} else {
				builder.setName("&e" + helmetAbility.name);
				loreBuilder.addLore("", "&eClick to select!");
			}
			builder.setLore(loreBuilder);
			getInventory().setItem(helmetAbility.slot, builder.getItemStack());
		}

		AItemStackBuilder builder = new AItemStackBuilder(Material.BARRIER);
		builder.setName("&cNone");
		ALoreBuilder loreBuilder = new ALoreBuilder("", "&cClick to remove ability!");
		builder.setLore(loreBuilder);
		getInventory().setItem(9, builder.getItemStack());

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "To Modify Helmet");
		meta.setLore(lore);
		back.setItemMeta(meta);

		getInventory().setItem(22, back);

	}

	public HelmetAbility getAbility(ItemStack helmet) {
		return GoldenHelmet.getAbility(helmet);
	}




	@Override
	public void onClose(InventoryCloseEvent event) {
		GoldenHelmet.setLore(goldenHelmet);
	}

	public ItemStack getHelm() {

		if(Misc.isAirOrNull(player.getItemInHand())) return null;

		NBTItem nbtItem = new NBTItem(player.getItemInHand());
		if(!nbtItem.hasKey(NBTTag.GHELMET_UUID.getRef())) return null;

		return nbtItem.getItem();
	}

}


