package net.pitsim.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.aitems.misc.GoldenHelmet;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.controllers.objects.HelmetAbility;
import net.pitsim.pitsim.controllers.objects.HelmetManager;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.NBTTag;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class HelmetAbilityPanel extends AGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public GoldenHelmet pitHelmet = ItemFactory.getItem(GoldenHelmet.class);
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
		ItemStack helmetStack = player.getItemInHand();
		if(!pitHelmet.isThisItem(helmetStack)) throw new RuntimeException();

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot == 22) {
				openPreviousGUI();
				return;
			}
			if(slot == 9) {
				NBTItem nbtItem = new NBTItem(helmetStack);
				nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), null);
				helmetStack = nbtItem.getItem();

				pitHelmet.updateItem(helmetStack);
				player.setItemInHand(helmetStack);

				if(HelmetManager.abilities.containsKey(player)) HelmetManager.abilities.get(player).unload();
				HelmetManager.abilities.remove(player);
				Sounds.SUCCESS.play(player);
				openPreviousGUI();
				return;
			}

			for(HelmetAbility helmetAbility : HelmetAbility.helmetAbilities) {
				if(slot != helmetAbility.slot) continue;

				HelmetAbility currentAbility = getAbility(helmetStack);
				if(currentAbility != null && currentAbility.refName.equals(helmetAbility.refName)) {
					AOutput.error(player, "&aYou already have that ability selected!");
					Sounds.NO.play(player);
					return;
				}

				NBTItem nbtItem = new NBTItem(helmetStack);
				nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), helmetAbility.refName);
				helmetStack = nbtItem.getItem();

				pitHelmet.updateItem(helmetStack);
				player.setItemInHand(helmetStack);

				Sounds.SUCCESS.play(player);
				openPreviousGUI();
			}

		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		ItemStack helmetStack = player.getItemInHand();
		if(!pitHelmet.isThisItem(helmetStack)) throw new RuntimeException();

		for(HelmetAbility helmetAbility : HelmetAbility.helmetAbilities) {
			AItemStackBuilder builder = new AItemStackBuilder(helmetAbility.getDisplayStack());
			ALoreBuilder loreBuilder = new ALoreBuilder();

			loreBuilder.addLore(helmetAbility.getDescription());
			HelmetAbility currentAbility = getAbility(helmetStack);
			if(currentAbility != null) {
				if(!currentAbility.refName.equals(helmetAbility.refName)) {
					builder.setName("&e" + helmetAbility.name);
					loreBuilder.addLore("", "&eClick to select!");
				} else {
					builder.setName("&a" + helmetAbility.name);
					loreBuilder.addLore("", "&aAlready selected!");
					Misc.addEnchantGlint(builder.getItemStack());
				}
			} else {
				builder.setName("&e" + helmetAbility.name);
				loreBuilder.addLore("", "&eClick to select!");
			}
			builder.setLore(loreBuilder);
			getInventory().setItem(helmetAbility.slot, builder.getItemStack());
		}

		AItemStackBuilder builder = new AItemStackBuilder(Material.BARRIER)
				.setName("&cNone")
				.setLore(new ALoreBuilder(
						"",
						"&cClick to remove ability!"
				));
		getInventory().setItem(9, builder.getItemStack());

		ItemStack back = new AItemStackBuilder(Material.ARROW)
				.setName("&aGo Back")
				.setLore(new ALoreBuilder(
						"&7To Modify Helmet"
				))
				.getItemStack();
		getInventory().setItem(22, back);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	public HelmetAbility getAbility(ItemStack helmet) {
		return HelmetManager.getAbility(helmet);
	}
}

