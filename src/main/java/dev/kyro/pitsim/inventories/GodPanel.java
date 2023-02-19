package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.misc.*;
import dev.kyro.pitsim.aitems.mobdrops.*;
import dev.kyro.pitsim.aitems.prot.ProtBoots;
import dev.kyro.pitsim.aitems.prot.ProtChestplate;
import dev.kyro.pitsim.aitems.prot.ProtHelmet;
import dev.kyro.pitsim.aitems.prot.ProtLeggings;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.HelmetManager;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.misc.Base64;
import dev.kyro.pitsim.misc.Constant;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public
class GodPanel extends AGUIPanel {
	public GodGUI godGUI;

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
		addItem(ItemFactory.getItem(Gunpowder.class).getItem(64));
		addItem(ItemFactory.getItem(RottenFlesh.class).getItem(64));
		addItem(ItemFactory.getItem(Leather.class).getItem(64));
		addItem(ItemFactory.getItem(Charcoal.class).getItem(64));
		addItem(ItemFactory.getItem(IronIngot.class).getItem(64));
		addItem(ItemFactory.getItem(EnderPearl.class).getItem(64));

//		BrewingIngredient enderPearl = BrewingIngredient.getIngredientFromTier(10);
//		assert enderPearl != null;
//		for(int i = 0; i < 10; i++) {
//			addItem(PotionManager.createPotion(BrewingIngredient.getIngredientFromTier(i + 1), enderPearl, enderPearl));
//			addItem(PotionManager.createSplashPotion(BrewingIngredient.getIngredientFromTier(i + 1), enderPearl, enderPearl));
//		}
	}

	public void addItem(ItemStack itemStack) {
		getInventory().addItem(itemStack);
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
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
