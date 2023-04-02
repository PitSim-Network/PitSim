package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.misc.AncientGemShard;
import dev.kyro.pitsim.aitems.misc.TotallyLegitGem;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
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

public class ShardHunterPanel extends AGUIPanel {

	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	RenownUpgrade upgrade = null;
	public RenownShopGUI renownShopGUI;

	public ShardHunterPanel(AGUI gui) {
		super(gui);
		renownShopGUI = (RenownShopGUI) gui;

	}

	@Override
	public String getName() {
		return "Shardhunter";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();

		if(event.getClickedInventory().getHolder() == this) {
			assert upgrade != null;

			if(slot == 15) {
				if(upgrade.prestigeReq > pitPlayer.prestige) {
					AOutput.error(player, "&cYou are too low level to acquire this!");
					Sounds.NO.play(player);
					return;
				}
				if(upgrade.isTiered()) {
					if(upgrade.getMaxTiers() != UpgradeManager.getTier(player, upgrade) && upgrade.getTierCosts().get(UpgradeManager.getTier(player, upgrade)) > pitPlayer.renown) {
						AOutput.error(player, "&cYou do not have enough renown!");
						Sounds.NO.play(player);
						return;
					}
					if(UpgradeManager.getTier(player, upgrade) < upgrade.getMaxTiers()) {
						RenownShopGUI.purchaseConfirmations.put(player, upgrade);
						openPanel(renownShopGUI.renownShopConfirmPanel);
					} else {
						AOutput.error(player, "&aYou already unlocked the last upgrade!");
						Sounds.NO.play(player);
					}
				} else if(!UpgradeManager.hasUpgrade(player, upgrade)) {
					if(upgrade.getUnlockCost() > pitPlayer.renown) {
						AOutput.error(player, "&cYou do not have enough renown!");
						Sounds.NO.play(player);
						return;
					}
					RenownShopGUI.purchaseConfirmations.put(player, upgrade);
					openPanel(renownShopGUI.renownShopConfirmPanel);
				} else {
					AOutput.error(player, "&aYou already unlocked this upgrade!");
					Sounds.NO.play(player);
				}
			}
			if(slot == 11) {
				if(!Misc.removeItems(player, 64, (pitItem, itemStack) -> pitItem instanceof AncientGemShard)) {
					AOutput.error(player, "&cYou do not have enough shards to craft this!");
					Sounds.NO.play(player);
					return;
				}

				AUtil.giveItemSafely(player, ItemFactory.getItem(TotallyLegitGem.class).getItem(1), true);
				player.closeInventory();
				Sounds.GEM_CRAFT.play(player);
				AOutput.send(player, "&d&lITEM CRAFTED!&7 Received &aTotally Legit Gem&7!");

			}
			if(slot == 22) {
				openPanel(renownShopGUI.getHomePanel());
			}
			updateInventory();
		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		for(RenownUpgrade renownUpgrade : UpgradeManager.upgrades) {
			if(renownUpgrade.refName.equals("SHARDHUNTER")) upgrade = renownUpgrade;
		}

		int shards = Misc.getItemCount(player, false, (pitItem, itemStack) -> pitItem instanceof AncientGemShard);
		double percent = shards / 64.0;
		String progressBar = AUtil.createProgressBar("|", ChatColor.GREEN, ChatColor.GRAY, 25, percent);

		ItemStack gem = new ItemStack(Material.WORKBENCH);
		ItemMeta meta = gem.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Craft Totally Legit Gem");
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Use &f64 &aAncient Gem Shards &7to craft"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7a&a Totally Legit Gem&7. (Gain a 9th token"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7on &dHidden Jewel &7items)"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Your progress:"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a" + shards + "&7/64 ") + progressBar + "&8 (" + (int) percent + "%)");
		lore.add("");
		if(shards >= 64) lore.add(ChatColor.YELLOW + "Click to craft!");
		else lore.add(ChatColor.RED + "Not enough shards!");
		meta.setLore(lore);
		gem.setItemMeta(meta);

		getInventory().setItem(11, gem);
		getInventory().setItem(15, upgrade.getDisplayItem(player));

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> backLore = new ArrayList<>();
		backLore.add(ChatColor.GRAY + "To Renown Shop");
		backMeta.setLore(backLore);
		back.setItemMeta(backMeta);

		getInventory().setItem(22, back);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
