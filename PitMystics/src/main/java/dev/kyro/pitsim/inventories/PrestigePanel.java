package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
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
		return "Prestige & Renown";
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
				//TODO: Re-enable killreq
				if(pitPlayer.level == 120 && pitPlayer.goldGrinded >= prestigeInfo.goldReq && pitPlayer.playerKills >= prestigeInfo.killReq) {
					if(pitPlayer.prestige == 50) {
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
		if(pitPlayer.prestige == 50) {
			prestigeLore.add(ChatColor.GREEN + "You've reached the maximum prestige, GG");
		} else {
			if(pitPlayer.prestige != 0) prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Current: &e" + AUtil.toRoman(pitPlayer.prestige)));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Required Level: " + prestigeInfo.getOpenBracket() + "&b&l120" + prestigeInfo.getCloseBracket()));
			DecimalFormat formatter = new DecimalFormat("#,###.#");
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Grinded: &6" +
					formatter.format(pitPlayer.goldGrinded) + "&7/&6" + formatter.format(prestigeInfo.goldReq) + "g"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Player Kills: &a" + pitPlayer.playerKills + "&7/" + (int) prestigeInfo.killReq));
			prestigeLore.add("");
			prestigeLore.add(ChatColor.GRAY + "Costs:");
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &c&lResets &blevel &cto 1"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &c&lResets &6gold &cto 0"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &c&lResets &cupgrades"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7&oRenown upgrades are kept"));
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7&oEnder chest is kept"));
			prestigeLore.add("");
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Reward: &e" + nextPrestigeInfo.renownReward + " &eRenown"));
			prestigeLore.add("");
			prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&7New prestige: &e" + AUtil.toRoman(pitPlayer.prestige + 1)));
			if(pitPlayer.prestige != 0) prestigeLore.add(ChatColor.translateAlternateColorCodes('&', "&b+" + (int) (100 * nextPrestigeInfo.xpMultiplier) + "&b% &7needed xp!"));
			prestigeLore.add("");
			//TODO: Re-enable killreq
			if(pitPlayer.level == 120 && pitPlayer.goldGrinded >= prestigeInfo.goldReq && pitPlayer.playerKills >= prestigeInfo.killReq) {
				prestigeLore.add(ChatColor.YELLOW + "Click to purchase!");
			} else {
				prestigeLore.add(ChatColor.RED + "Requirements not met!");
			}
		}
		prestigeMeta.setLore(prestigeLore);
		prestige.setItemMeta(prestigeMeta);
		getInventory().setItem(11, prestige);

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
		renownLore.add(ChatColor.translateAlternateColorCodes('&', "&7Renown &e" + pitPlayer.renown + " Renown"));
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
