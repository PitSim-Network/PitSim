package dev.kyro.pitsim.controllers.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GoldenHelmet {

	public static List<GoldenHelmet> helmets = new ArrayList<>();
	public static Player owner;

	public ItemStack item;
	public int gold;



	public GoldenHelmet(ItemStack item) {
		this.item = item;

		NBTItem nbtItem = new NBTItem(item);
		this.gold = nbtItem.getInteger(NBTTag.GHELMET_GOLD.getRef());
	}


	public static GoldenHelmet getHelmet(ItemStack helmet, Player player) {

		GoldenHelmet goldenHelmet = null;
		for(GoldenHelmet testGoldenHelmet : helmets) {

			NBTItem nbtHelmet = new NBTItem(helmet);
			NBTItem storedHelmet = new NBTItem(testGoldenHelmet.item);

			if(!nbtHelmet.hasKey(NBTTag.IS_GHELMET.getRef())) return null;

			if(!storedHelmet.getString(NBTTag.GHELMET_UUID.getRef()).equals(nbtHelmet.getString(NBTTag.GHELMET_UUID.getRef()))) continue;
			goldenHelmet = testGoldenHelmet;
			break;
		}
		if(goldenHelmet == null) {

			goldenHelmet = new GoldenHelmet(helmet);
			helmets.add(goldenHelmet);
			owner = player;


		}

		owner = player;
		return goldenHelmet;
	}


	public void setLore() {
		if(getInventorySlot() == -1) return;
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Selected ability:");
		lore.add("");
		lore.add(ChatColor.GRAY + "Passives:");
		lore.add("");
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		lore.add(ChatColor.GRAY + "Gold: "  + ChatColor.GOLD + formatter.format(gold) + "g");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Hold and right-click to modify!");

		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);

		if(getInventorySlot() == -2) {
			owner.getInventory().setHelmet(item);
			return;
		}

		owner.getInventory().setItem(getInventorySlot(), item);

	}

	public int getInventorySlot() {
		for(int i = 0; i < owner.getInventory().getSize(); i++) {
			if(Misc.isAirOrNull(owner.getInventory().getItem(i))) continue;
			if(owner.getInventory().getItem(i).getType() == Material.GOLD_HELMET) {
				NBTItem helmetItem = new NBTItem(item);
				NBTItem playerItem = new NBTItem(owner.getInventory().getItem(i));

				if(!(helmetItem.getString(NBTTag.GHELMET_UUID.getRef()).equals(playerItem.getString(NBTTag.GHELMET_UUID.getRef())))) continue;

				return i;
			}
		}
		if(Misc.isAirOrNull(owner.getInventory().getHelmet())) return -1;
		if(owner.getInventory().getHelmet().getType() == Material.GOLD_HELMET) {

			NBTItem helmetItem = new NBTItem(item);
			NBTItem playerItem = new NBTItem(owner.getInventory().getHelmet());

			if(helmetItem.getString(NBTTag.GHELMET_UUID.getRef()).equals(playerItem.getString(NBTTag.GHELMET_UUID.getRef()))) return -2;
		}
		return -1;
	}

	public void depositGold(int gold) {
		this.gold += gold;
		setLore();
	}

}
