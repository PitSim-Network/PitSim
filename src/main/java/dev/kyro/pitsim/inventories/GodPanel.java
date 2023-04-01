package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.diamond.*;
import dev.kyro.pitsim.aitems.misc.*;
import dev.kyro.pitsim.aitems.mobdrops.*;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.HelmetManager;
import dev.kyro.pitsim.enums.DiscordLogChannel;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Base64;
import dev.kyro.pitsim.misc.Constant;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public
class GodPanel extends AGUIPanel {
	public GodGUI godGUI;
	public List<ItemStack> items = new ArrayList<>();
	public int page = 1;

	public GodPanel(AGUI gui) {
		super(gui);
		this.godGUI = (GodGUI) gui;
		cancelClicks = false;

		ItemStack itemStack;
		NBTItem nbtItem;

		try {
			itemStack = Base64.deserialize(Constant.PITSIM_CRATE);
			itemStack.setAmount(64);
			addItem(itemStack);
			itemStack = Base64.deserialize(Constant.TAINTED_CRATE);
			itemStack.setAmount(64);
			addItem(itemStack);
			itemStack = Base64.deserialize(Constant.PITSIM_BUNDLE);
			itemStack.setAmount(64);
			addItem(itemStack);
			itemStack = Base64.deserialize(Constant.SMALL_POUCH);
			itemStack.setAmount(64);
			addItem(itemStack);
			itemStack = Base64.deserialize(Constant.LARGE_POUCH);
			itemStack.setAmount(64);
			addItem(itemStack);
		} catch(Exception ignored) {}

		addItem(MysticFactory.getJewelItem(MysticType.SWORD));
		addItem(MysticFactory.getJewelItem(MysticType.BOW));
		addItem(MysticFactory.getJewelItem(MysticType.PANTS));
		addItem(MysticFactory.getFreshItem(MysticType.TAINTED_SCYTHE, null));
		addItem(MysticFactory.getFreshItem(MysticType.TAINTED_CHESTPLATE, null));

		itemStack = ItemFactory.getItem(GoldenHelmet.class).getItem();
		itemStack = HelmetManager.depositGold(itemStack, 0);
		addItem(itemStack);
		itemStack = ItemFactory.getItem(GoldenHelmet.class).getItem();
		itemStack = HelmetManager.depositGold(itemStack, 20_000_000);
		addItem(itemStack);
		itemStack = ItemFactory.getItem(GoldenHelmet.class).getItem();
		itemStack = HelmetManager.depositGold(itemStack, 2_000_000_000);
		addItem(itemStack);

		addItem(ItemFactory.getItem(DiamondLeggings.class).getItem(1));
		addItem(ItemFactory.getItem(ProtHelmet.class).getItem(1));
		addItem(ItemFactory.getItem(ProtChestplate.class).getItem(1));
		addItem(ItemFactory.getItem(ProtLeggings.class).getItem(1));
		addItem(ItemFactory.getItem(ProtBoots.class).getItem(1));

		addItem(ItemFactory.getItem(FunkyFeather.class).getItem(64));
		addItem(ItemFactory.getItem(CorruptedFeather.class).getItem(64));
		addItem(ItemFactory.getItem(ChunkOfVile.class).getItem(64));
		addItem(ItemFactory.getItem(TotallyLegitGem.class).getItem(64));
		addItem(ItemFactory.getItem(AncientGemShard.class).getItem(64));
		addItem(ItemFactory.getItem(YummyBread.class).getItem(64));
		addItem(ItemFactory.getItem(VeryYummyBread.class).getItem(64));

		addItem(ItemFactory.getItem(RottenFlesh.class).getItem(64));
		addItem(ItemFactory.getItem(Bone.class).getItem(64));
		addItem(ItemFactory.getItem(SpiderEye.class).getItem(64));
		addItem(ItemFactory.getItem(Leather.class).getItem(64));
		addItem(ItemFactory.getItem(BlazeRod.class).getItem(64));
		addItem(ItemFactory.getItem(RawPork.class).getItem(64));
		addItem(ItemFactory.getItem(Charcoal.class).getItem(64));
		addItem(ItemFactory.getItem(Gunpowder.class).getItem(64));
		addItem(ItemFactory.getItem(IronIngot.class).getItem(64));
		addItem(ItemFactory.getItem(EnderPearl.class).getItem(64));

		BrewingIngredient enderPearl = BrewingIngredient.getIngredientFromTier(10);
		assert enderPearl != null;
		for(int i = 0; i < 10; i++) {
			addItem(PotionManager.createPotion(BrewingIngredient.getIngredientFromTier(i + 1), enderPearl, enderPearl));
			addItem(PotionManager.createSplashPotion(BrewingIngredient.getIngredientFromTier(i + 1), enderPearl, enderPearl));
		}

		setInventory();
	}

	public void addItem(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack, true);
		nbtItem.setBoolean(NBTTag.IS_GOD.getRef(), true);
		items.add(itemStack);
	}

	public int getMaxPages() {
		return (items.size() - 1) / 45 + 1;
	}

	public void setInventory() {
		getInventory().clear();

		for(int i = (page - 1) * 45; i < page * 45; i++) {
			if(i == items.size()) break;
			ItemStack itemStack = items.get(i);
			getInventory().addItem(itemStack);
		}

		for(int i = 45; i < 54; i++) {
			ItemStack paneStack = new AItemStackBuilder(Material.STAINED_GLASS_PANE, 1, 7)
					.getItemStack();
			getInventory().setItem(i, paneStack);
		}

		if(page != 1) {
			ItemStack previousStack = new AItemStackBuilder(Material.ARROW)
					.setName("&cPrevious Page")
					.getItemStack();
			getInventory().setItem(45, previousStack);
		}
		if(page != getMaxPages()) {
			ItemStack nextStack = new AItemStackBuilder(Material.ARROW)
					.setName("&cNext Page")
					.getItemStack();
			getInventory().setItem(53, nextStack);
		}
		updateInventory();
	}

	@Override
	public String getName() {
		return "" + ChatColor.YELLOW + ChatColor.BOLD + "God Menu";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;

		int slot = event.getSlot();
		if(slot >= 45) event.setCancelled(true);

		if(slot == 45) {
			if(page == 1) return;
			page--;
			setInventory();
		} else if(slot == 53) {
			if(page == getMaxPages()) return;
			page++;
			setInventory();
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(Misc.isKyro(player.getUniqueId()) || Misc.isWiji(player.getUniqueId())) return;
		List<String> messageLines = new ArrayList<>();
		for(int i = 0; i < 36; i++) {
			ItemStack itemStack = player.getInventory().getItem(i);
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null) continue;
			NBTItem nbtItem = new NBTItem(itemStack, true);
			if(!nbtItem.hasKey(NBTTag.IS_GOD.getRef())) continue;

			nbtItem.removeKey(NBTTag.IS_GOD.getRef());
			player.getInventory().setItem(i, itemStack);
			messageLines.add("> `" + itemStack.getAmount() + "x " + pitItem.getNBTID() +
					(pitItem.hasUUID ? " (" + nbtItem.getString(NBTTag.ITEM_UUID.getRef()) + ")" : "") + "`");
		}
		player.updateInventory();

		if(messageLines.isEmpty() || PitSim.isDev()) return;
		messageLines.add(0, "`" + player.getName() + "`");
		Misc.logToDiscord(DiscordLogChannel.GOD_MENU_LOG_CHANNEL, String.join("\n", messageLines));
	}
}
