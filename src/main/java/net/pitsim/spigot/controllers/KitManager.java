package net.pitsim.spigot.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.aitems.MysticFactory;
import net.pitsim.spigot.aitems.PitItem;
import net.pitsim.spigot.aitems.diamond.DiamondBoots;
import net.pitsim.spigot.aitems.diamond.DiamondChestplate;
import net.pitsim.spigot.aitems.diamond.DiamondHelmet;
import net.pitsim.spigot.controllers.objects.Kit;
import net.pitsim.spigot.enums.KitItem;
import net.pitsim.spigot.enums.MysticType;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.enums.PantColor;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class KitManager implements Listener {
	private static final Map<KitItem, Supplier<ItemStack>> kitItemMap = new HashMap<>();
	public static List<Kit> kits = new ArrayList<>();

	public static void registerKit(Kit kit) {
		kits.add(kit);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack dropped = event.getItemDrop().getItemStack();
		NBTItem nbtItem = new NBTItem(dropped);
		if(!nbtItem.hasKey(NBTTag.IS_PREMADE.getRef())) return;
		event.getItemDrop().remove();
		Sounds.NO.play(event.getPlayer());
		AOutput.send(event.getPlayer(), "&c&lITEM DELETED!&7 Dropped a pre-made item (use /kit to re-obtain)");
	}

	public static ItemStack getItem(KitItem kitItem) {
		ItemStack itemStack = kitItemMap.get(kitItem).get();
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem != null && pitItem.hasUUID) itemStack = pitItem.randomizeUUID(itemStack);
		return itemStack;
	}

	static {
		try {
			kitItemMap.put(KitItem.DIAMOND_HELMET, () -> ItemFactory.getItem(DiamondHelmet.class).getItem());
			kitItemMap.put(KitItem.DIAMOND_CHESTPLATE, () -> ItemFactory.getItem(DiamondChestplate.class).getItem());
			kitItemMap.put(KitItem.DIAMOND_BOOTS, () -> ItemFactory.getItem(DiamondBoots.class).getItem());

			kitItemMap.put(KitItem.EXE_SWEATY, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("exe"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("sweaty"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("shark"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&bXP Sword").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.SWEATY_GHEART, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.BLUE);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("sweaty"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("gheart"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ng"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&bXP Pants &7(Defence)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.SWEATY_ELEC, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.GREEN);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("sweaty"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("elec"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ng"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&bXP Pants &7(Mobility)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.EXE_MOCT_BOOST, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("exe"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("moct"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("boost"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&6Gold Sword &7(Efficiency)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.EXE_MOCT_SHARK, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("exe"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("moct"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("shark"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&6Gold Sword &7(Damage)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.MOCT_BOOST_BUMP, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.ORANGE);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("moct"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("boost"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bump"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&6Gold Pants &7(Efficiency)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.MOCT_BOOST_ELEC, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.YELLOW);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("moct"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("boost"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("elec"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&6Gold Pants &7(Mobility)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.BILL_STOMP_PUN, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("stomp"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pun"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&cDamage Billionaire &7(w/RGM)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.BILL_LS_CD, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cd"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&cPvP Lifesteal &7(w/RGM)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.CH_LS_GAB, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ch"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("gab"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&cPvP Lifesteal &7(w/Regularity)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.PERUN_GAMBLE_PUN, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("perun"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("gamble"), 1, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pun"), 3, false);
					return new AItemStackBuilder(itemStack).setName("&bTrue Damage &7(w/Regularity)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.PERUN_CHEAL_GAB, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("perun"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ch"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("gab"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&bTrue Damage &7(w/Regularity)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.RGM_MIRROR_PROT, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.BLUE);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mirror"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&9RGM Mirror &7(w/Billionaire)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.RGM_CF_PROT, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.RED);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cf"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&9RGM CF &7(w/Billionaire)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.REG_MIRROR_PROT, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.GREEN);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("reg"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mirror"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&aRegularity Mirror &7(w/Perun)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.REG_SOLI_PROT, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.ORANGE);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("reg"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("soli"), 1, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 3, false);
					return new AItemStackBuilder(itemStack).setName("&aRegularity Solitude &7(w/Perun)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.MLB_DRAIN, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 1, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("drain"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("fletching"), 3, false);
					return new AItemStackBuilder(itemStack).setName("&2MLB Drain &7(Combos)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.MLB_PIN, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 1, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pin"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("fletching"), 3, false);
					return new AItemStackBuilder(itemStack).setName("&2MLB Pin &7(Anti-Combos)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.MLB_WASP, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 1, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("wasp"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("fletching"), 3, false);
					return new AItemStackBuilder(itemStack).setName("&2MLB Wasp &7(Tanking)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.MLB_TELE, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 1, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("tele"), 3, false);
					return new AItemStackBuilder(itemStack).setName("&eMLB Telebow &7(Mobility)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.STREAKING_BILL_LS, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 2, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("shark"), 3, false);
					return new AItemStackBuilder(itemStack).setName("&cStreaking Lifesteal &7(Costs Gold)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.STREAKING_CH_LS, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ch"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("shark"), 2, false);
					return new AItemStackBuilder(itemStack).setName("&cStreaking Lifesteal &7(No-Cost)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

			kitItemMap.put(KitItem.VOLLEY_FTTS, () -> {
				try {
					ItemStack itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("volley"), 3, false);
					itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ftts"), 3, false);
					return new AItemStackBuilder(itemStack).setName("&bVolley FTTS &7(w/Electrolytes)").getItemStack();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});

		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
}
