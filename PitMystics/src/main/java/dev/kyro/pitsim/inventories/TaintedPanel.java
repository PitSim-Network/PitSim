package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class TaintedPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public TaitedGUI taitedGUI;

	public TaintedPanel(AGUI gui) {
		super(gui);
		taitedGUI = (TaitedGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Boosters";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		ItemStack soul = new ItemStack(Material.INK_SACK, 7);
		ItemMeta soulMeta = soul.getItemMeta();
		soulMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "TAINTED SOULS");
		List<String> soulLore = new ArrayList<>();
		soulLore.add(ChatColor.GRAY + "The lost souls of fallen warriors");
		soulLore.add("");
		soulLore.add(ChatColor.GRAY + "Obtained by:");
		soulLore.add(ChatColor.DARK_GRAY + "- " + ChatColor.LIGHT_PURPLE + "Killing Bosses");
		soulLore.add(ChatColor.DARK_GRAY + "- " + ChatColor.LIGHT_PURPLE + "Shredding " + ChatColor.DARK_AQUA + "JEWEL! " + ChatColor.LIGHT_PURPLE + " Items");
		soulLore.add(ChatColor.DARK_GRAY + "- " + ChatColor.LIGHT_PURPLE + "Opening " + ChatColor.DARK_PURPLE + " Tainted Crates");
		soulLore.add("");
		soulLore.add(ChatColor.GRAY + "You have " + ChatColor.GREEN + countSouls(player) + ChatColor.GRAY + " Souls");
		soulMeta.setLore(soulLore);
		soul.setItemMeta(soulMeta);
		getInventory().setItem(13, soul);

		ItemStack shred = new ItemStack(Material.GOLD_SWORD);
		ItemMeta shredMeta = shred.getItemMeta();
		shredMeta.setDisplayName(ChatColor.YELLOW + "SHRED JEWEL ITEMS");
		List<String> shredLore = new ArrayList<>();
		shredLore.add(ChatColor.GRAY + "Turn " + ChatColor.DARK_AQUA + "JEWEL! " + ChatColor.GRAY + "items into");
		shredLore.add(ChatColor.WHITE + "Tainbt");

	}

	public static int countSouls(Player player) {
		int count = 0;
		for (ItemStack itemStack : player.getInventory()) {
			if(Misc.isAirOrNull(itemStack)) continue;

			NBTItem nbtItem = new NBTItem(itemStack);
			if(nbtItem.hasKey(NBTTag.IS_TAINTED_SOUL.getRef())) count++;
		}
		return count;
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
