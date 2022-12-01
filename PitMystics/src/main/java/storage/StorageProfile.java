package storage;

import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.exceptions.DataNotLoadedException;
import dev.kyro.pitsim.exceptions.NoCommonEnchantException;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class StorageProfile {

	private ItemStack[][]enderChest;
	private ItemStack[] inventory;
	private ItemStack[] armor;

	private final Player player;

	public StorageProfile(Player player) {
		this.player = player;
	}

	public void setEnderchest(PluginMessage message) {
		List<String> strings = message.getStrings();
		UUID playerUUID = UUID.fromString(strings.get(0));
		strings.remove(0);

		int pages = message.getIntegers().get(0);
		enderChest = new ItemStack[27][pages];

		for(int i = 0; i < strings.size(); i++) {
			int page = i / 27;

			enderChest[i % 27][page] = deserialize(strings.get(i));
		}
	}

	public void setInventory(PluginMessage message) {
		List<String> strings = message.getStrings();
		UUID playerUUID = UUID.fromString(strings.get(0));
		strings.remove(0);

		inventory = new ItemStack[36];
		armor = new ItemStack[4];

		for(int i = 0; i < 36; i++) {
			inventory[i] = deserialize(strings.get(i));
		}

		for(int i = 0; i < 4; i++) {
			armor[i] = deserialize(strings.get(36 + i));
		}
	}

	public void saveEnderchest() {
		PluginMessage message = new PluginMessage().writeString("ENDERCHEST").writeString(player.getUniqueId().toString());
		for(ItemStack[] items : enderChest) {
			for(ItemStack item : items) {
				message.writeString(serialize(item));
			}
		}

		message.send();
	}

	public void saveInventory() {
		PluginMessage message = new PluginMessage().writeString("INVENTORY").writeString(player.getUniqueId().toString());
		for(ItemStack itemStack : inventory) {
			message.writeString(serialize(itemStack));
		}

		message.send();
	}

	public Player getPlayer() {
		return player;
	}

	public ItemStack[][] getEnderchest() {
		if(enderChest == null) {
			try {
				throw new DataNotLoadedException();
			} catch(DataNotLoadedException ignored) { }
		}

		return enderChest;
	}

	public ItemStack[] getInventory() {
		if(inventory == null) {
			try {
				throw new DataNotLoadedException();
			} catch(DataNotLoadedException ignored) { }
		}

		return inventory;
	}

	public ItemStack[] getArmor() {
		if(armor == null) {
			try {
				throw new DataNotLoadedException();
			} catch(DataNotLoadedException ignored) { }
		}

		return armor;
	}


	public static ItemStack deserialize(String string) {
		try {
			NBTTagCompound comp = MojangsonParser.parse(string);
			net.minecraft.server.v1_8_R3.ItemStack cis = net.minecraft.server.v1_8_R3.ItemStack.createStack(comp);
			return CraftItemStack.asBukkitCopy(cis);
		} catch (MojangsonParseException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String serialize(ItemStack item) {
		net.minecraft.server.v1_8_R3.ItemStack cis = CraftItemStack.asNMSCopy(item);
		NBTTagCompound comp = new NBTTagCompound();
		cis.save(comp);
		return comp.toString();
	}

}
