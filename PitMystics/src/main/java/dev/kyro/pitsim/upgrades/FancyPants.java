package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FancyPants extends RenownUpgrade {
	public FancyPants() {
		super("Fancy Pants", "FANCY_PANTS", 10, 25, 15, false, 0);
	}

	@Override
	public ItemStack getDisplayItem(Player player, boolean isCustomPanel) {
		ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Your pants now &eglow&7."));
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, isCustomPanel));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public AGUIPanel getCustomPanel() {return null;}

	@Override
	public List<Integer> getTierCosts() {
		return null;
	}

//
//	@EventHandler
//	public void onSwap(ArmorEquipEvent event) {
//		event.setAsync(true);
//		if(NonManager.getNon(event.getPlayer()) != null) return;
//		Bukkit.broadcastMessage("Equipped");
//		if(event.getArmorType() == ArmorType.LEGGINGS && event.getNewArmor().getType() == Material.LEATHER_LEGGINGS) {
//			Bukkit.broadcastMessage("True");
//			if(!event.getNewArmor().getItemMeta().hasEnchant(Enchantment.ARROW_FIRE)) {
//				Bukkit.broadcastMessage("True2");
//				ItemMeta meta = event.getNewArmor().getItemMeta();
//				meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
//				event.getNewArmor().setItemMeta(meta);
//			}
//		}
//	}
//
//	@EventHandler
//	public void onRemove(ArmorRemoveEvent event) {
//		event.setAsync(true);
//		if(NonManager.getNon(event.getPlayer()) != null) return;
//		Bukkit.broadcastMessage("Unequipped");
//		if(event.getArmorType() == ArmorType.LEGGINGS && event.getPrevious().getType() == Material.LEATHER_LEGGINGS) {
//			Bukkit.broadcastMessage("True");
//			if(event.getPrevious().getItemMeta().hasEnchant(Enchantment.ARROW_FIRE)) {
//				Bukkit.broadcastMessage("True2");
//				ItemMeta meta = event.getPrevious().getItemMeta();
//				meta.removeEnchant(Enchantment.ARROW_FIRE);
//				event.getPrevious().setItemMeta(meta);
//			}
//		}
//	}


//	@EventHandler
//	public void onEquip(InventoryClickEvent event) {
////		Bukkit.broadcastMessage(event.getSlot() + "");
////		Bukkit.broadcastMessage(event.getCurrentItem().toString());
////		Bukkit.broadcastMessage(event.getCursor().toString());
//		if(event.getSlot() == 37) {
//			if(event.getCursor().getType() == Material.LEATHER_LEGGINGS && !event.getCursor().getItemMeta().hasEnchant(Enchantment.ARROW_FIRE)) {
//				ItemMeta meta = event.getCursor().getItemMeta();
//				meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
//				event.getCursor().setItemMeta(meta);
//			}
//
//		}
//
////		if(event.getCursor().getType() == Material.LEATHER_LEGGINGS && event.getCursor().getItemMeta().hasEnchant(Enchantment.ARROW_FIRE)) {
////			ItemMeta meta = event.getCursor().getItemMeta();
////			meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
////			event.getCursor().setItemMeta(meta);
////		}
//
//
//
//	}
}
