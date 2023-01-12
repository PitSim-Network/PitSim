package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.tutorial.HelpItemStacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class MainProgressionPanel extends AGUIPanel {

	public MainProgressionPanel(AGUI gui) {
		super(gui);

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 15, 45, 46, 47, 48, 49, 50, 51, 52);

		getInventory().setItem(53, HelpItemStacks.getMainProgressionStack());
	}

	@Override
	public String getName() {
		return "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Skill Tree";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public static class MainProgressionMajorUnlock extends MainProgressionUnlock {
		public SkillBranch skillBranch;

		public MainProgressionMajorUnlock(SkillBranch skillBranch, int guiXPos, int guiYPos) {
			super(skillBranch.getRefName(), guiXPos, guiYPos);
		}

		@Override
		public ItemStack getDisplayStack(UnlockState unlockState) {
			return skillBranch.getDisplayStack(unlockState);
		}
	}

	public static class MainProgressionMinorUnlock extends MainProgressionUnlock {
		public MainProgressionMinorUnlock(String id, int guiXPos, int guiYPos) {
			super(id, guiXPos, guiYPos);
		}

		@Override
		public ItemStack getDisplayStack(UnlockState unlockState) {
			return null;
		}
	}

	public static abstract class MainProgressionUnlock {
		public String id;
		public int guiXPos;
		public int guiYPos;

		public MainProgressionUnlock(String id, int guiXPos, int guiYPos) {
			this.id = id;
			this.guiXPos = guiXPos;
			this.guiYPos = guiYPos;
		}

		public abstract ItemStack getDisplayStack(UnlockState unlockState);
	}
}
