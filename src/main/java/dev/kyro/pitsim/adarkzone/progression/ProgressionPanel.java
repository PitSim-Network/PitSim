package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.tutorial.HelpItemStacks;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ProgressionPanel extends AGUIPanel {
	public ProgressionGUI progressionGUI;
	public PitPlayer pitPlayer;
	public SkillBranch skillBranch;

	public ProgressionPanel(AGUI gui, SkillBranch skillBranch) {
		super(gui, true);
		this.progressionGUI = (ProgressionGUI) gui;
		this.pitPlayer = progressionGUI.pitPlayer;
		this.skillBranch = skillBranch;

		buildInventory();

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 15, 27, 28, 29, 30, 31, 32, 33, 34);
		getInventory().setItem(35, HelpItemStacks.getProgressionStack());

		setInventory();
	}

	@Override
	public String getName() {
		return skillBranch.getName();
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	public void setInventory() {
		updateInventory();
	}
}
