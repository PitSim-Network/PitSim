package net.pitsim.spigot.inventories;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.APagedGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.darkzone.DarkzoneBalancing;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.tutorial.NPCCheckpoint;
import net.pitsim.spigot.tutorial.TutorialObjective;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TaintedShopPanel extends APagedGUIPanel {

	public TaintedShopPanel(AGUI gui) {
		super(gui);

		addBackButton(getRows() * 9 - 5);
		buildInventory();
	}

	@Override
	public void addItems() {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		for(DarkzoneBalancing.ShopItem value : DarkzoneBalancing.ShopItem.values()) {
			ItemStack itemStack = value.getItemStack();
			ItemMeta itemMeta = itemStack.getItemMeta();
			int cost = isTutorial(value) ? 0 : value.getSoulCost();
			ALoreBuilder lore = new ALoreBuilder(itemMeta.getLore());
			lore.addLore("",
					"&7Cost: &f" + (isTutorial(value) ? "&m" : "") + cost + " Soul" + Misc.s(cost) + (isTutorial(value) ? "&a&l FREE!" : ""),
					"&7You have: &f" + pitPlayer.taintedSouls + " Soul" + Misc.s(pitPlayer.taintedSouls),
					"",
					pitPlayer.taintedSouls < cost ? "&cNot enough souls!" : "&eClick to purchase!"
			);
			itemMeta.setLore(lore.getLore());
			itemStack.setItemMeta(itemMeta);

			addItem(() -> itemStack, event -> {
				int updatedCost = isTutorial(value) ? 0 : value.getSoulCost();

				if(pitPlayer.taintedSouls < updatedCost) {
					Sounds.NO.play(player);
					return;
				}

				if(Misc.getEmptyInventorySlots(player) < 1) {
					Sounds.NO.play(player);
					AOutput.error(player, "You don't have enough space in your inventory!");
					player.closeInventory();
					return;
				}

				Sounds.RENOWN_SHOP_PURCHASE.play(player);

				pitPlayer.taintedSouls -= updatedCost;
				ItemStack item = value.getItemStack();

				if(isTutorial(value)) {
					NPCCheckpoint.removeTutorialItems(player);
				}

				if(isTutorial(value)) ItemFactory.setTutorialItem(item, true);
				AUtil.giveItemSafely(player, item);
				setInventory();
			});
		}
	}

	@Override
	public void setInventory() {
		super.setInventory();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7, false);
	}

	@Override
	public String getName() {
		return "Tainted Shop";
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public boolean isTutorial(DarkzoneBalancing.ShopItem item) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.darkzoneTutorial.tutorialNPC == null || pitPlayer.darkzoneTutorial.tutorialNPC.getCheckpoint() == null) return false;


		return pitPlayer.darkzoneTutorial.isActive() && item == DarkzoneBalancing.ShopItem.DIAMOND_LEGGINGS &&
				pitPlayer.darkzoneTutorial.tutorialNPC.getCheckpoint().objective == TutorialObjective.MARKET_SHOP
				&& !pitPlayer.darkzoneTutorial.data.completedObjectives.contains(TutorialObjective.MARKET_SHOP);
	}
}
