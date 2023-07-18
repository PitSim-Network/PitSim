package net.pitsim.spigot.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.controllers.PostPrestigeManager;
import net.pitsim.spigot.controllers.PrestigeValues;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Formatter;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.tutorial.HelpItemStacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrestigePanel extends AGUIPanel {

	DecimalFormat formatter = new DecimalFormat("#,###.#");

	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
	PrestigeValues.PrestigeInfo nextPrestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige + 1);
	public PrestigeGUI prestigeGUI;

	public PrestigePanel(AGUI gui) {
		super(gui);
		prestigeGUI = (PrestigeGUI) gui;
	}

	@Override
	public String getName() {
		return "&bPrestige &7and &eRenown";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			if(slot == 11) {
				if(pitPlayer.level == 120 && pitPlayer.goldGrinded >= prestigeInfo.getGoldReq()) {
					if(pitPlayer.prestige == PrestigeValues.MAX_PRESTIGE) {
						AOutput.error(player, "&aYou are already the maximum prestige!");
						Sounds.NO.play(player);
						return;
					}

					player.closeInventory();
					openPanel(prestigeGUI.prestigeConfirmPanel);
				} else {
					AOutput.error(player, "&cYou do not meet the requirments to prestige!");
					Sounds.NO.play(player);
				}
			}
			if(slot == 15) {
				RenownShopGUI renownShopGUI = new RenownShopGUI(player);
				renownShopGUI.open();
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		ItemStack prestige = new ItemStack(Material.DIAMOND);
		ItemMeta prestigeMeta = prestige.getItemMeta();
		List<String> prestigeLore = new ArrayList<>();
		prestigeMeta.setDisplayName(ChatColor.AQUA + "Prestige");
		if(pitPlayer.prestige == PrestigeValues.MAX_PRESTIGE) {
			prestigeLore.add(ChatColor.GREEN + "You've reached the maximum prestige, GG");

			if(pitPlayer.overflowXP > 0) {
				prestigeLore.add("");
				prestigeLore.add(ChatColor.YELLOW + "Post-Prestige Progress:");
				prestigeLore.add(ChatColor.GRAY + "Next Unlock: " + ChatColor.AQUA + formatter.format(PostPrestigeManager.getNextUnlockDisplayXP(player)) + " XP");
				prestigeLore.add(ChatColor.GRAY + "You Have: " + ChatColor.AQUA + formatter.format(pitPlayer.overflowXP) + " XP");
				prestigeLore.add("");
				prestigeLore.add(ChatColor.translateAlternateColorCodes('&', PostPrestigeManager.getProgressionString(player)));
			}
			prestigeLore.add("");
			prestigeLore.add(ChatColor.RED + "You may not prestige any further!");

		} else {
			if(pitPlayer.prestige != 0)
				prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Current: &e" + AUtil.toRoman(pitPlayer.prestige)));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Required Level: " + prestigeInfo.getOpenBracket() + "&b&l120" + prestigeInfo.getCloseBracket()));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Grinded: &6" +
					formatter.format(pitPlayer.goldGrinded) + "&7/&6" + formatter.format(prestigeInfo.getGoldReq()) + "g"));
			prestigeLore.add("");
			prestigeLore.add(ChatColor.GRAY + "Costs:");
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &c&lResets &blevel &cto 1"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &c&lResets &6gold &cto 0"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &c&lResets &cupgrades"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7&oRenown upgrades are kept"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7&oEnder chest is kept"));
			prestigeLore.add("");
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Reward: &e" + prestigeInfo.getRenownReward() + " &eRenown"));
			prestigeLore.add("");
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7New prestige: &e" + AUtil.toRoman(pitPlayer.prestige + 1)));
			if(pitPlayer.prestige != 0)
				prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&b+" + (int) (100 * nextPrestigeInfo.getXpMultiplier()) + "&b% &7needed xp!"));
			prestigeLore.add("");
			if(pitPlayer.level == 120 && pitPlayer.goldGrinded >= prestigeInfo.getGoldReq()) {
				prestigeLore.add(ChatColor.YELLOW + "Click to purchase!");
			} else {
				prestigeLore.add(ChatColor.RED + "Requirements not met!");
			}
		}
		prestigeMeta.setLore(prestigeLore);
		prestige.setItemMeta(prestigeMeta);
		getInventory().setItem(11, prestige);

		getInventory().setItem(26, HelpItemStacks.getPrestigeItemStack());

		ItemStack renown = new ItemStack(Material.BEACON);
		ItemMeta renownMeta = renown.getItemMeta();
		List<String> renownLore = new ArrayList<>();
		renownMeta.setDisplayName(ChatColor.YELLOW + "Renown Shop");
		renownLore.add(ChatColor.translateAlternateColorCodes('&', "&7Use &eRenown &7earned from"));
		renownLore.add(ChatColor.translateAlternateColorCodes('&', "&bPrestige &7to unlock unique"));
		renownLore.add(ChatColor.translateAlternateColorCodes('&', "&7upgrades!"));
		renownLore.add("");
		renownLore.add(ChatColor.translateAlternateColorCodes('&', "&7&oThese upgrades are safe"));
		renownLore.add(ChatColor.translateAlternateColorCodes('&', "&7&ofrom prestige reset."));
		renownLore.add("");
		renownLore.add(ChatColor.translateAlternateColorCodes('&', "&7Renown &e" + Formatter.formatRenown(pitPlayer.renown)));
		renownLore.add("");
		renownLore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to browse!"));
		renownMeta.setLore(renownLore);
		renown.setItemMeta(renownMeta);
		getInventory().setItem(15, renown);

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
