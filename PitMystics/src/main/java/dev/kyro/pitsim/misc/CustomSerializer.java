package dev.kyro.pitsim.misc;

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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomSerializer {
	public static String serialize(ItemStack i) {
		String[] parts = new String[7];
		parts[0] = i.getType().name();
		parts[1] = Integer.toString(i.getAmount());
		parts[2] = String.valueOf(i.getDurability());
		parts[3] = i.getItemMeta().getDisplayName();
		parts[4] = String.valueOf(i.getData().getData());
		parts[5] = getEnchants(i);
		parts[6] = getNBT(i);
		return StringUtils.join(parts, ";");
	}

	public static String getEnchants(ItemStack i) {
		List<String> e = new ArrayList<String>();
		Map<Enchantment, Integer> en = i.getEnchantments();
		for (Enchantment t : en.keySet()) {
			e.add(t.getName() + ":" + en.get(t));
		}
		return StringUtils.join(e, ",");
	}

	public static String getLore(ItemStack i) {
		List<String> e = i.getItemMeta().getLore();
		return StringUtils.join(e, ",");
	}

	public static String getNBT(ItemStack i) {

		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(i);
		NBTTagCompound compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
		return compound.toString();

	}

	public static ItemStack setNBT(ItemStack i, String NBT) {

		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(i);
		try {
			NBTTagCompound compound = MojangsonParser.parse(NBT);

			nmsStack.setTag(compound);
		} catch (MojangsonParseException e1) {
			e1.printStackTrace();
		}

		return CraftItemStack.asBukkitCopy(nmsStack);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack deserialize(String p) {
		String[] a = p.split(";");
		System.out.println(Arrays.toString(a));
		ItemStack i = new ItemStack(Material.getMaterial(a[0]), Integer.parseInt(a[1]));
		i.setDurability((short) Integer.parseInt(a[2]));
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(a[3]);
		i.setItemMeta(meta);
		MaterialData data = i.getData();
		data.setData((byte) Integer.parseInt(a[4]));
		i.setData(data);
		if (!a[6].isEmpty()) {
			i = setNBT(i, a[6]);
		}
		if (!a[5].isEmpty()) {
			String[] parts = a[5].split(",");
			for (String s : parts) {
				String label = s.split(":")[0];
				String amplifier = s.split(":")[1];
				Enchantment type = Enchantment.getByName(label);
				if (type == null)
					continue;
				int f;
				try {
					f = Integer.parseInt(amplifier);
				} catch (Exception ex) {
					continue;
				}
				i.addUnsafeEnchantment(type, f);
			}
		}
		return i;
	}
}