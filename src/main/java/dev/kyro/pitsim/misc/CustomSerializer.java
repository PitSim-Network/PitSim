package dev.kyro.pitsim.misc;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.ItemManager;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomSerializer {
	public static String serialize(ItemStack itemStack) {
		String[] parts = new String[7];
		parts[0] = itemStack.getType().name();
		parts[1] = Integer.toString(itemStack.getAmount());
		parts[2] = String.valueOf(itemStack.getDurability());
		parts[3] = itemStack.getItemMeta().getDisplayName();
		parts[4] = String.valueOf(itemStack.getData().getData());
		parts[5] = getEnchants(itemStack);
		parts[6] = getNBT(itemStack);
		return StringUtils.join(parts, "\t");
	}

	public static String getEnchants(ItemStack itemStack) {
		List<String> enchants = new ArrayList<>();
		Map<Enchantment, Integer> enchantMap = itemStack.getEnchantments();
		for(Enchantment enchant : enchantMap.keySet()) {
			enchants.add(enchant.getName() + ":" + enchantMap.get(enchant));
		}
		return StringUtils.join(enchants, ",");
	}

	public static String getLore(ItemStack itemStack) {
		List<String> lore = itemStack.getItemMeta().getLore();
		return StringUtils.join(lore, ",");
	}

	public static String getNBT(ItemStack itemStack) {
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
		return compound.toString();
	}

	public static ItemStack setNBT(ItemStack itemStack, String NBT) {
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		try {
			NBTTagCompound compound = MojangsonParser.parse(NBT);

			nmsStack.setTag(compound);
		} catch(MojangsonParseException exception) {
			exception.printStackTrace();
		}

		return CraftItemStack.asBukkitCopy(nmsStack);
	}

	public static ItemStack deserializeDirectly(String itemString) {
		return deserialize(itemString, null, false);
	}

	public static ItemStack deserializeFromPlayerData(String itemString, UUID informUUID) {
		return deserialize(itemString, informUUID, true);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack deserialize(String itemString, UUID informUUID, boolean updateItem) {
		if(itemString == null || itemString.isEmpty()) return new ItemStack(Material.AIR);
		String[] stringArr = itemString.split("\t");
		ItemStack itemStack = new ItemStack(Material.getMaterial(stringArr[0]), Integer.parseInt(stringArr[1]));
		itemStack.setDurability((short) Integer.parseInt(stringArr[2]));
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(stringArr[3]);
		itemStack.setItemMeta(meta);
		MaterialData data = itemStack.getData();
		data.setData((byte) Integer.parseInt(stringArr[4]));
		itemStack.setData(data);
		if(!stringArr[6].isEmpty()) {
			itemStack = setNBT(itemStack, stringArr[6]);
		}
		if(!stringArr[5].isEmpty()) {
			String[] parts = stringArr[5].split(",");
			for(String enchantAndLevel : parts) {
				String label = enchantAndLevel.split(":")[0];
				String levelString = enchantAndLevel.split(":")[1];
				Enchantment type = Enchantment.getByName(label);
				if(type == null)
					continue;
				int level;
				try {
					level = Integer.parseInt(levelString);
				} catch(Exception ex) {
					continue;
				}
				itemStack.addUnsafeEnchantment(type, level);
			}
		}

		if(updateItem) {
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem != null) {
				ItemStack oldStack = itemStack.clone();
				pitItem.updateItem(itemStack);
				if(!pitItem.hasLastServer || pitItem.getLastServer(oldStack) == PitSim.status) {
					if(informUUID != null && !oldStack.equals(itemStack)) {
						ItemManager.updatedItems.putIfAbsent(informUUID, new ArrayList<>());
						ItemManager.updatedItems.get(informUUID).add(itemStack);
					}
				}
			}
		}
		return itemStack;
	}
}