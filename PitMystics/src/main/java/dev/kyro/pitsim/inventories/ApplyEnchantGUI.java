package dev.kyro.pitsim.inventories;

//public class ApplyEnchantGUI extends AInventoryGUI {
//
//	public MainEnchantGUI mainEnchantGUI;
//	public AInventoryBuilder builder;
//	public Map.Entry<PitEnchant, Integer> previousEnchant;
//	public ItemStack mystic;
//
//	public ApplyEnchantGUI(MainEnchantGUI mainEnchantGUI, Map.Entry<PitEnchant, Integer> previousEnchant, ItemStack mystic) {
//		super("Choose an Enchant", 6);
//		this.mainEnchantGUI = mainEnchantGUI;
//		this.previousEnchant = previousEnchant;
//		this.mystic = mystic;
//
//		builder = new AInventoryBuilder(baseGUI)
//				.createBorder(Material.STAINED_GLASS_PANE, 2)
//				.setSlots(Material.BARRIER, 0, 45);
//
//		List<PitEnchant> applicableEnchants = EnchantManager.getEnchants(MysticType.getMysticType(mystic));
//		int count = 0;
//		for(int i = 0; count != applicableEnchants.size(); i++) {
//
//			if(i < 9 || i % 9 == 0 || i % 9 == 8) continue;
//
//			ItemStack displayItem = FreshCommand.getFreshItem(MysticType.getMysticType(mystic), null);
//			try {
//				displayItem = EnchantManager.addEnchant(displayItem, applicableEnchants.get(count++), 1, false);
//			} catch(Exception ignored) { }
//			baseGUI.setItem(i, displayItem);
//		}
//	}
//
//	@Override
//	public void onClick(InventoryClickEvent event) {
//
//		int slot = event.getSlot();
//		ItemStack clickedItem = event.getCurrentItem();
//		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(clickedItem);
//		if(event.getClickedInventory().getHolder() == this) {
//
//			if(slot == 45) {
//
//				mainEnchantGUI.baseGUI.setItem(37, mystic);
//				mainEnchantGUI.player.openInventory(mainEnchantGUI.getInventory());
//				return;
//			}
//
//			for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {
//
//				try {
//					if(previousEnchant != null) mystic = EnchantManager.addEnchant(mystic, previousEnchant.getKey(), 0, false);
//					mystic = EnchantManager.addEnchant(mystic, entry.getKey(), 3, false);
//				} catch(Exception ignored) { }
//				mainEnchantGUI.baseGUI.setItem(37, mystic);
//				mainEnchantGUI.player.openInventory(mainEnchantGUI.getInventory());
//				mainEnchantGUI.updateGUI();
//				return;
//			}
//		}
//		updateGUI();
//	}
//
//	@Override
//	public void onOpen(InventoryOpenEvent event) {
//
//	}
//
//	@Override
//	public void onClose(InventoryCloseEvent event) {
//
//	}
//
//	public void updateGUI() {
//
//		for(int i = 0; i < baseGUI.getSize(); i++) {
//			mainEnchantGUI.player.getOpenInventory().setItem(i, baseGUI.getItem(i));
//		}
//	}
//}
