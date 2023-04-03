package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.APagedGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ChatTriggerManager;
import dev.kyro.pitsim.controllers.CombatManager;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.PerkEquipEvent;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.perks.NoPerk;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ApplyPerkPanel extends APagedGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public PerkGUI perkGUI;
	public TaggedItem noPerkItem;
	public int perkNum;

	public ApplyPerkPanel(AGUI gui) {
		super(gui);
		perkGUI = (PerkGUI) gui;
		addBackButton(getRows() * 9 - 5);
		buildInventory();
	}

	@Override
	public void addItems() {
		for(PitPerk pitPerk : PerkManager.pitPerks) {
			if(pitPerk == NoPerk.INSTANCE) continue;
			addItem(createSupplier(pitPerk), createConsumer(pitPerk));
		}
		noPerkItem = addTaggedItem(getRows() * 9 - 4, createSupplier(NoPerk.INSTANCE), createConsumer(NoPerk.INSTANCE));
	}

	public Supplier<ItemStack> createSupplier(PitPerk pitPerk) {
		return () -> pitPerk.getDisplayStack(player, PitPerk.DisplayItemType.APPLY_PERK_PANEL);
	}

	public Consumer<InventoryClickEvent> createConsumer(PitPerk pitPerk) {
		return event -> {
			if(!PerkManager.isUnlocked(player, pitPerk)) {
				AOutput.error(player, "&c&lERROR!&7 This perk needs to be unlocked in the renown shop");
				Sounds.ERROR.play(player);
				return;
			}

			if(CombatManager.isInCombat(player)) {
				AOutput.error(player, "&c&lERROR!&7 You cannot do this while in combat");
				Sounds.ERROR.play(player);
				return;
			}

			for(PitPerk testPerk : perkGUI.getActivePerks()) {
				if(testPerk != pitPerk || testPerk == NoPerk.INSTANCE) continue;
				Sounds.ERROR.play(player);
				AOutput.error(perkGUI.player, "&cThat perk is already equipped");
				return;
			}

			PitPerk oldPerk = perkGUI.getActivePerk(perkNum);
			PerkEquipEvent equipEvent = new PerkEquipEvent(pitPerk, player, oldPerk);
			Bukkit.getPluginManager().callEvent(equipEvent);
			if(equipEvent.isCancelled()) return;

			Sounds.SUCCESS.play(player);
			perkGUI.setPerk(pitPerk, perkNum);
			ChatTriggerManager.sendPerksInfo(pitPlayer);
			openPreviousGUI();
		};
	}

	@Override
	public void setInventory() {
		super.setInventory();
		noPerkItem.setItem();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7, false);
	}

	@Override
	public String getName() {
		return "&aChoose a Perk";
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		setInventory();
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
