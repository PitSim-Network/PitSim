package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.objects.Kit;
import dev.kyro.pitsim.enums.KitItem;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager implements Listener {
	private static Map<KitItem, ItemStack> kitItemMap = new HashMap<>();
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
		ItemStack itemStack = kitItemMap.get(kitItem);
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem != null) itemStack = pitItem.randomizeUUID(itemStack);
		return itemStack;
	}

	static {
		try {
			ItemStack itemStack;

			itemStack = new AItemStackBuilder(Material.DIAMOND_HELMET)
					.getItemStack();
			kitItemMap.put(KitItem.DIAMOND_HELMET, itemStack);

			itemStack = new AItemStackBuilder(Material.DIAMOND_CHESTPLATE)
					.getItemStack();
			kitItemMap.put(KitItem.DIAMOND_CHESTPLATE, itemStack);

			itemStack = new AItemStackBuilder(Material.DIAMOND_BOOTS)
					.getItemStack();
			kitItemMap.put(KitItem.DIAMOND_BOOTS, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("exe"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("sweaty"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("shark"), 2, false);
			new AItemStackBuilder(itemStack).setName("&bXP Sword");
			kitItemMap.put(KitItem.EXE_SWEATY, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.BLUE);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("sweaty"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("gheart"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ng"), 2, false);
			new AItemStackBuilder(itemStack).setName("&bXP Pants &7(Defence)");
			kitItemMap.put(KitItem.SWEATY_GHEART, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.GREEN);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("sweaty"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("elec"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ng"), 2, false);
			new AItemStackBuilder(itemStack).setName("&bXP Pants &7(Mobility)");
			kitItemMap.put(KitItem.SWEATY_ELEC, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("exe"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("moct"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("boost"), 2, false);
			new AItemStackBuilder(itemStack).setName("&6Gold Sword &7(Efficiency)");
			kitItemMap.put(KitItem.EXE_MOCT_BOOST, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("exe"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("moct"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("shark"), 2, false);
			new AItemStackBuilder(itemStack).setName("&6Gold Sword &7(Damage)");
			kitItemMap.put(KitItem.EXE_MOCT_SHARK, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.ORANGE);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("moct"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("boost"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bump"), 2, false);
			new AItemStackBuilder(itemStack).setName("&6Gold Pants &7(Efficiency)");
			kitItemMap.put(KitItem.MOCT_BOOST_BUMP, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.YELLOW);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("moct"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("boost"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("elec"), 2, false);
			new AItemStackBuilder(itemStack).setName("&6Gold Pants &7(Mobility)");
			kitItemMap.put(KitItem.MOCT_BOOST_ELEC, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("stomp"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pun"), 2, false);
			new AItemStackBuilder(itemStack).setName("&cDamage Billionaire &7(w/RGM)");
			kitItemMap.put(KitItem.BILL_STOMP_LS, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cd"), 2, false);
			new AItemStackBuilder(itemStack).setName("&cPvP Lifesteal &7(w/RGM)");
			kitItemMap.put(KitItem.BILL_LS_PF, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ch"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("gab"), 2, false);
			new AItemStackBuilder(itemStack).setName("&cPvP Lifesteal &7(w/Regularity)");
			kitItemMap.put(KitItem.CH_LS, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("perun"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("gamble"), 1, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pun"), 3, false);
			new AItemStackBuilder(itemStack).setName("&bTrue Damage &7(w/Regularity)");
			kitItemMap.put(KitItem.PERUN_GAMBLE_STOMP, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("perun"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ch"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("gab"), 2, false);
			new AItemStackBuilder(itemStack).setName("&bTrue Damage &7(w/Regularity)");
			kitItemMap.put(KitItem.PERUN_CHEAL_CD, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.BLUE);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mirror"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 2, false);
			new AItemStackBuilder(itemStack).setName("&9RGM Mirror &7(w/Billionaire)");
			kitItemMap.put(KitItem.RGM_MIRROR_PROT, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.RED);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cf"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 2, false);
			new AItemStackBuilder(itemStack).setName("&9RGM CF &7(w/Billionaire)");
			kitItemMap.put(KitItem.RGM_CF_PROT, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.GREEN);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("reg"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mirror"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 2, false);
			new AItemStackBuilder(itemStack).setName("&aRegularity Mirror &7(w/Perun)");
			kitItemMap.put(KitItem.REG_MIRROR_PROT, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.ORANGE);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("reg"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("soli"), 1, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 3, false);
			new AItemStackBuilder(itemStack).setName("&aRegularity Solitude &7(w/Perun)");
			kitItemMap.put(KitItem.REG_SOLI_LASTSTAND, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 1, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("drain"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("fletching"), 3, false);
			new AItemStackBuilder(itemStack).setName("&2MLB Drain &7(Combos)");
			kitItemMap.put(KitItem.MLB_DRAIN, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 1, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pin"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("fletching"), 3, false);
			new AItemStackBuilder(itemStack).setName("&2MLB Pin &7(Anti-Combos)");
			kitItemMap.put(KitItem.MLB_PIN, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 1, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("wasp"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("fletching"), 3, false);
			new AItemStackBuilder(itemStack).setName("&2MLB Wasp &7(Tanking)");
			kitItemMap.put(KitItem.MLB_WASP, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 1, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("tele"), 3, false);
			new AItemStackBuilder(itemStack).setName("&eMLB Telebow &7(Mobility)");
			kitItemMap.put(KitItem.MLB_TELE, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 2, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("shark"), 3, false);
			new AItemStackBuilder(itemStack).setName("&cStreaking Lifesteal &7(Costs Gold)");
			kitItemMap.put(KitItem.STREAKING_BILL_LS, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.SWORD, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ch"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("shark"), 2, false);
			new AItemStackBuilder(itemStack).setName("&cStreaking Lifesteal &7(No-Cost)");
			kitItemMap.put(KitItem.STREAKING_CH_LS, itemStack);

			itemStack = MysticFactory.getFreshItem(MysticType.BOW, null);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("volley"), 3, false);
			itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ftts"), 3, false);
			new AItemStackBuilder(itemStack).setName("&bVolley FTTS &7(w/Electrolytes)");
			kitItemMap.put(KitItem.VOLLEY_FTTS, itemStack);

		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
}
