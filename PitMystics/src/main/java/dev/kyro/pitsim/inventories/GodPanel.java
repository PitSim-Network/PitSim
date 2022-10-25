package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.ingredients.*;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.*;
import dev.kyro.pitsim.upgrades.ShardHunter;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class GodPanel extends AGUIPanel {
	public GodGUI godGUI;

	public GodPanel(AGUI gui) {
		super(gui);
		this.godGUI = (GodGUI) gui;
		cancelClicks = false;

		ItemStack itemStack;
		NBTItem nbtItem;

		try {
			itemStack = Base64.itemFrom64(Constant.PITSIM_CRATE);
			itemStack.setAmount(64);
			addItem(itemStack);
			itemStack = Base64.itemFrom64(Constant.TAINTED_CRATE);
			itemStack.setAmount(64);
			addItem(itemStack);
			itemStack = Base64.itemFrom64(Constant.PITSIM_BUNDLE);
			itemStack.setAmount(64);
			addItem(itemStack);
			itemStack = Base64.itemFrom64(Constant.SMALL_POUCH);
			itemStack.setAmount(64);
			addItem(itemStack);
			itemStack = Base64.itemFrom64(Constant.LARGE_POUCH);
			itemStack.setAmount(64);
			addItem(itemStack);
		} catch(Exception ignored) {}

		addItem(FreshCommand.getFreshItem(MysticType.TAINTED_SCYTHE, null));
		addItem(FreshCommand.getFreshItem(MysticType.TAINTED_CHESTPLATE, null));

		itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
		itemStack = ItemManager.enableDropConfirm(itemStack);
		nbtItem = new NBTItem(itemStack);
		nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
		EnchantManager.setItemLore(nbtItem.getItem(), player);
		addItem(nbtItem.getItem());

		itemStack = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
		itemStack = ItemManager.enableDropConfirm(itemStack);
		nbtItem = new NBTItem(itemStack);
		nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
		EnchantManager.setItemLore(nbtItem.getItem(), player);
		addItem(nbtItem.getItem());

		itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
		itemStack = ItemManager.enableDropConfirm(itemStack);
		nbtItem = new NBTItem(itemStack);
		nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
		EnchantManager.setItemLore(nbtItem.getItem(), player);
		addItem(nbtItem.getItem());

		addItem(ProtArmor.getArmor("helmet"));
		addItem(ProtArmor.getArmor("chestplate"));
		addItem(ProtArmor.getArmor("leggings"));
		addItem(ProtArmor.getArmor("boots"));

		addItem(FunkyFeather.getFeather(64));
		addItem(ChunkOfVile.getVile(64));
		addItem(ShardHunter.getGemItem(64));
		addItem(ShardHunter.getShardItem(64));
		addItem(YummyBread.getBread(64, false));
		addItem(YummyBread.getBread(64, true));

		itemStack = RottenFlesh.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = Bone.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = SpiderEye.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = Gunpowder.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = FermentedSpiderEye.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = MagmaCream.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = RawPork.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = WitherSkull.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = IronIngot.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);
		itemStack = EnderPearl.INSTANCE.getItem();
		itemStack.setAmount(64);
		addItem(itemStack);

		BrewingIngredient enderPearl = BrewingIngredient.getIngredientFromTier(10);
		assert enderPearl != null;
		for(int i = 0; i < 10; i++) {
			addItem(PotionManager.createPotion(BrewingIngredient.getIngredientFromTier(i + 1), enderPearl, enderPearl));
			addItem(PotionManager.createSplashPotion(BrewingIngredient.getIngredientFromTier(i + 1), enderPearl, enderPearl));
		}
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
