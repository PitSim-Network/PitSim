package dev.kyro.pitsim.battlepass.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.battlepass.PassManager;
import dev.kyro.pitsim.battlepass.PitSimPass;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class PassPanel extends AGUIPanel {
	public PassGUI passGUI;

	public static ItemStack previousPageItem;
	public static ItemStack questItem;
	public static ItemStack purchaseItem;
	public static ItemStack passInfo;
	public static ItemStack nextPageItem;

	public int page;

	static {
		previousPageItem = new AItemStackBuilder(Material.PAPER)
				.setName("&f&lPrevious Page")
				.setLore(new ALoreBuilder(
						"&7Click to view the previous page"
				))
				.getItemStack();

		questItem = new AItemStackBuilder(Material.BOOK_AND_QUILL)
				.setName("&e&lQuests")
				.setLore(new ALoreBuilder(
						"&7Click to view your quests"
				))
				.getItemStack();

		purchaseItem = new AItemStackBuilder(Material.DOUBLE_PLANT)
				.setName("&6&lPit&e&lSim &7&lPass")
				.setLore(new ALoreBuilder(
						"&7Click to be taken to the",
						"&7store to purchase the pass"
				))
				.getItemStack();

		passInfo = new AItemStackBuilder(Material.BEACON)
				.setName("&b&lPass Info")
				.setLore(new ALoreBuilder(
						"&7put pass info here"
				))
				.getItemStack();

		nextPageItem = new AItemStackBuilder(Material.PAPER)
				.setName("&f&lNext Page")
				.setLore(new ALoreBuilder(
						"&7Click to view the next page"
				))
				.getItemStack();
	}

	public PassPanel(AGUI gui) {
		super(gui);
		passGUI = (PassGUI) gui;

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 7, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);

		getInventory().setItem(10, previousPageItem);
		getInventory().setItem(12, questItem);
		getInventory().setItem(13, purchaseItem);
		getInventory().setItem(14, passInfo);
		getInventory().setItem(16, nextPageItem);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		page = pitPlayer.getPassData(PassManager.currentPass.startDate).getCompletedTiers() / 9 + 1;
		setPage(pitPlayer, page);
	}

	public void setPage(PitPlayer pitPlayer, int page) {
		int passTier = pitPlayer.getPassData(PassManager.currentPass.startDate).getCompletedTiers();

		for(int i = 0; i < 9; i++) {
			int tier = (page - 1) * 9 + i + 1;
			if(tier > PassManager.currentPass.tiers) {
				getInventory().setItem(i + 27, new ItemStack(Material.AIR));
				getInventory().setItem(i + 36, new ItemStack(Material.AIR));
				getInventory().setItem(i + 45, new ItemStack(Material.AIR));
				continue;
			}

//			Set up the glass panes
			if(tier - 1 > passTier) {
				getInventory().setItem(i + 36, new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 8)
						.setName("&c&lTier " + tier)
						.setLore(new ALoreBuilder(
								"&7You have not unlocked this tier yet"
						))
						.getItemStack());
			} else if(tier - 1 == passTier && !PassManager.hasCompletedPass(pitPlayer)) {
				getInventory().setItem(i + 36, new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 1)
						.setName("&6&lTier " + tier)
						.setLore(new ALoreBuilder(
								"&7You are currently on this tier"
						))
						.getItemStack());
			} else {
				getInventory().setItem(i + 36, new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5)
						.setName("&a&lTier " + tier)
						.setLore(new ALoreBuilder(
								"&7You have unlocked this tier"
						))
						.getItemStack());
			}

			if(PassManager.hasReward(PitSimPass.RewardType.PREMIUM, tier)) {
				boolean hasClaimed = PassManager.hasClaimedReward(pitPlayer, PitSimPass.RewardType.PREMIUM, tier);
				ItemStack itemStack = PassManager.currentPass.premiumPassRewards.get(tier).getDisplayItem(hasClaimed);
				if(PassManager.canClaimReward(pitPlayer, PitSimPass.RewardType.FREE, tier)) {
					Misc.addEnchantGlint(itemStack);
				} else if(hasClaimed) {
					itemStack.setType(Material.BARRIER);
				}
				getInventory().setItem(i + 27, itemStack);
			} else {
				getInventory().setItem(i + 27, new ItemStack(Material.AIR));
			}
			if(PassManager.hasReward(PitSimPass.RewardType.FREE, tier)) {
				boolean hasClaimed = PassManager.hasClaimedReward(pitPlayer, PitSimPass.RewardType.FREE, tier);
				ItemStack itemStack = PassManager.currentPass.freePassRewards.get(tier).getDisplayItem(hasClaimed);
				if(PassManager.canClaimReward(pitPlayer, PitSimPass.RewardType.FREE, tier)) {
					Misc.addEnchantGlint(itemStack);
				} else if(hasClaimed) {
					itemStack.setType(Material.BARRIER);
				}
				getInventory().setItem(i + 45, itemStack);
			} else {
				getInventory().setItem(i + 45, new ItemStack(Material.AIR));
			}
		}
	}

	@Override
	public String getName() {
		return ChatColor.GOLD + "" + ChatColor.BOLD + "Pit" + ChatColor.YELLOW + "" + ChatColor.BOLD + "Sim " + ChatColor.AQUA + "" + ChatColor.BOLD + "Pass";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		PitPlayer pitPlayer = passGUI.pitPlayer;
		int slot = event.getSlot();

		if(slot == 16) {
			if(page < PassManager.currentPass.tiers / 9 + 1) {
				setPage(pitPlayer, ++page);
			} else {
				Sounds.NO.play(player);
			}
		} else if(slot == 10) {
			if(page > 1) {
				setPage(pitPlayer, --page);
			} else {
				Sounds.NO.play(player);
			}
		} else if(slot >= 27 && slot <= 35) {
			int clickedReward = (page - 1) * 9 + slot - 26;
			if(!PassManager.hasReward(PitSimPass.RewardType.PREMIUM, clickedReward)) return;
			if(PassManager.canClaimReward(pitPlayer, PitSimPass.RewardType.PREMIUM, clickedReward)) {
				PassManager.claimReward(pitPlayer, PitSimPass.RewardType.PREMIUM, clickedReward);

				ItemStack itemStack = PassManager.currentPass.premiumPassRewards.get(clickedReward).getDisplayItem(true);
				itemStack.setType(Material.BARRIER);
				getInventory().setItem(slot, itemStack);
			} else {
				Sounds.NO.play(player);
			}
		} else if(slot >= 45 && slot <= 53) {
			int clickedReward = (page - 1) * 9 + slot - 44;
			if(!PassManager.hasReward(PitSimPass.RewardType.FREE, clickedReward)) return;
			if(PassManager.canClaimReward(pitPlayer, PitSimPass.RewardType.FREE, clickedReward)) {
				PassManager.claimReward(pitPlayer, PitSimPass.RewardType.FREE, clickedReward);

				ItemStack itemStack = PassManager.currentPass.freePassRewards.get(clickedReward).getDisplayItem(true);
				itemStack.setType(Material.BARRIER);
				getInventory().setItem(slot, itemStack);
			} else {
				Sounds.NO.play(player);
			}
		} else if(slot == 12) {
			openPanel(passGUI.questPanel);
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
