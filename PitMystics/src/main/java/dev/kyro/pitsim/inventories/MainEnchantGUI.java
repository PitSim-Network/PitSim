package dev.kyro.pitsim.inventories;

//public class MainEnchantGUI extends AInventoryGUI {
//
//	public AInventoryBuilder builder;
//	public Player player;
//	public boolean inSubGUI = false;
//
//	public MainEnchantGUI(Player player) {
//		super("Enchant GUI", 6);
//		this.player = player;
//
//		AItemStackBuilder openEnchant = new AItemStackBuilder(Material.ENCHANTMENT_TABLE);
//		builder = new AInventoryBuilder(baseGUI)
//				.setSlots(Material.STAINED_GLASS_PANE, 5, 0, 1, 2, 9, 11, 18, 19, 20)
//				.setSlots(Material.STAINED_GLASS_PANE, 4, 3, 4, 5, 12, 14, 21, 22, 23)
//				.setSlots(Material.STAINED_GLASS_PANE, 14, 6, 7, 8, 15, 17, 24, 25, 26)
//				.setSlots(Material.STAINED_GLASS_PANE, 15, 27, 28, 29, 36, 38, 45, 46, 47)
//				.setSlots(Material.STAINED_GLASS_PANE, 7, 30, 31, 32, 33, 34, 35, 39, 42, 44, 48, 49, 50, 51, 52, 53);
//
//		baseGUI.setItem(10, openEnchant.getItemStack());
//		baseGUI.setItem(13, openEnchant.getItemStack());
//		baseGUI.setItem(16, openEnchant.getItemStack());
//	}
//
//	@Override
//	public void onClick(InventoryClickEvent event) {
//
//		int slot = event.getSlot();
//		ItemStack clickedItem = event.getCurrentItem();
//		ItemStack mystic = event.getInventory().getItem(37);
//		if(event.getClickedInventory().getHolder() == this) {
//
//			if(slot == 10 || slot == 13 || slot == 16) {
//
//				if(Misc.isAirOrNull(mystic)) {
//					return;
//				}
//
//				inSubGUI = true;
//				Map.Entry<PitEnchant, Integer> displayEnchant = getDisplayEnchant(clickedItem);
//				player.openInventory(new ApplyEnchantGUI(this, displayEnchant, mystic).getInventory());
//				return;
//			}
//
//			if(slot == 37) {
//
//				baseGUI.setItem(37, new ItemStack(Material.AIR));
//				if(!FreshCommand.isFresh(mystic)) player.getInventory().addItem(mystic);
//			}
//
//			if(slot == 40) {
//
//				if(Misc.isAirOrNull(mystic)) {
//
//					baseGUI.setItem(37, FreshCommand.getFreshItem(MysticType.SWORD, null));
//				} else {
//					AOutput.error(player, "Already an item in the mystic well");
//					return;
//				}
//			}
//			if(slot == 41) {
//
//				if(Misc.isAirOrNull(mystic)) {
//
//					baseGUI.setItem(37, FreshCommand.getFreshItem(MysticType.BOW, null));
//				} else {
//					AOutput.error(player, "Already an item in the mystic well");
//					return;
//				}
//			}
//			if(slot == 43) {
//
//				if(Misc.isAirOrNull(mystic)) {
//
//					baseGUI.setItem(37, FreshCommand.getFreshItem(MysticType.PANTS, PantColor.RED));
//				} else {
//					AOutput.error(player, "Already an item in the mystic well");
//					return;
//				}
//			}
//		} else {
//
//			if(clickedItem.getType() == Material.AIR) return;
//			NBTItem nbtItem = new NBTItem(clickedItem);
//			if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return;
//
//			if(!Misc.isAirOrNull(mystic)) {
//				AOutput.error(player, "Already an item in the mystic well");
//				return;
//			}
//
//			mystic = event.getClickedInventory().getItem(slot);
//			event.getClickedInventory().setItem(slot, new ItemStack(Material.AIR));
//			baseGUI.setItem(37, mystic);
//		}
//		updateGUI();
//	}
//
//	@Override
//	public void onOpen(InventoryOpenEvent event) {
//
//		inSubGUI = false;
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				updateGUI();
//			}
//		}.runTaskLater(PitSim.INSTANCE, 1L);
//	}
//
//	@Override
//	public void onClose(InventoryCloseEvent event) {
//
//		if(inSubGUI) return;
//		ItemStack mystic = event.getInventory().getItem(37);
//		if(!Misc.isAirOrNull(mystic) && !FreshCommand.isFresh(mystic)) {
//			player.getInventory().addItem(mystic);
//		}
//	}
//
//	public void updateGUI() {
//
//		ItemStack mystic = baseGUI.getItem(37);
//		if(Misc.isAirOrNull(mystic)) {
//
//			baseGUI.setItem(40, FreshCommand.getFreshItem(MysticType.SWORD, null));
//			baseGUI.setItem(41, FreshCommand.getFreshItem(MysticType.BOW, null));
//			builder.setSlots(Material.CACTUS, 0, 43);
//
//			builder.setSlots(Material.ENCHANTMENT_TABLE, 0, 10, 13, 16);
//		} else {
//
//			builder.setSlots(Material.BARRIER, 0, 40, 41, 43);
//
//			NBTItem nbtItem = new NBTItem(mystic);
//			NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.PIT_ENCHANT_ORDER.getRef());
//
//			for(int i = 0; i < 3; i++) {
//				if(enchantOrder.size() < i + 1) {
//					baseGUI.setItem(10 + (3 * i), new ItemStack(Material.ENCHANTMENT_TABLE));
//					continue;
//				}
//
//				ItemStack displayMystic = FreshCommand.getFreshItem(MysticType.getMysticType(mystic), PantColor.RED);
//				PitEnchant pitEnchant = EnchantManager.getEnchant(enchantOrder.get(i));
//				assert pitEnchant != null;
//				try {
//					displayMystic = EnchantManager.addEnchant(displayMystic, pitEnchant, EnchantManager.getEnchantLevel(mystic, pitEnchant), false);
//				} catch(Exception ignored) { }
//
//				baseGUI.setItem(10 + (3 * i), displayMystic);
//			}
//		}
//
//		for(int i = 0; i < baseGUI.getSize(); i++) {
//			player.getOpenInventory().setItem(i, baseGUI.getItem(i));
//		}
//	}
//
//	public static Map.Entry<PitEnchant, Integer> getDisplayEnchant(ItemStack mystic) {
//
//		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(mystic);
//		for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {
//
//			return entry;
//		}
//		return null;
//	}
//}
