package storage;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.exceptions.DataNotLoadedException;
import dev.kyro.pitsim.exceptions.NoCommonEnchantException;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class StorageProfile {

	private Inventory[]enderChest;
	private ItemStack[] inventory;
	private ItemStack[] armor;

	private final Player player;

	private PluginMessage enderchestSave;
	private PluginMessage inventorySave;

	private BukkitTask enderchestSaveTask;
	private BukkitTask inventorySaveTask;

	private boolean saving = false;

	public StorageProfile(Player player) {
		this.player = player;
	}

	public void setEnderchest(PluginMessage message) {
		List<String> strings = message.getStrings();
		UUID playerUUID = UUID.fromString(strings.get(0));
		strings.remove(0);

		int pages = message.getIntegers().get(0);
		enderChest = new Inventory[pages];
		for(int i = 0; i < pages; i++) {
			enderChest[i] = player.getServer().createInventory(null, 27, "Enderchest - Page " + (i + 1));
		}

		for(int i = 0; i < strings.size(); i++) {
			int page = i / 27;

			Inventory inventory = enderChest[page];
			inventory.setItem(i % 27, deserialize(strings.get(i)));
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
		message.writeString(PitSim.serverName);
		for(Inventory items : enderChest) {
			for(ItemStack item : items) {
				message.writeString(serialize(item));
			}
		}

		enderchestSave = message;
		saving = true;

		enderchestSaveTask = new BukkitRunnable() {
			@Override
			public void run() {
				player.kickPlayer("§cYour playerdata failed to save. Please report this issue");
			}
		}.runTaskLater(PitSim.INSTANCE, 40);

		message.send();
	}

	public void saveInventory() {
		PluginMessage message = new PluginMessage().writeString("INVENTORY").writeString(player.getUniqueId().toString());
		message.writeString(PitSim.serverName);
		for(ItemStack itemStack : inventory) {
			message.writeString(serialize(itemStack));
		}

		inventorySave = message;
		saving = true;

		inventorySaveTask = new BukkitRunnable() {
			@Override
			public void run() {
				player.kickPlayer("§cYour playerdata failed to save. Please report this issue");
			}
		}.runTaskLater(PitSim.INSTANCE, 40);

		message.send();
	}

	public Player getPlayer() {
		return player;
	}

	public Inventory getEnderchest(int page) {
		if(enderChest == null) {
			try {
				throw new DataNotLoadedException();
			} catch(DataNotLoadedException ignored) { }
		}

		return enderChest[page - 1];
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

	public boolean hasData() {
		return enderChest != null && inventory != null && armor != null;
	}

	public boolean isSaving() {
		return saving;
	}

	protected void receiveSaveConfirmation(PluginMessage message) {
		if(message.getStrings().get(0).equals("ENDERCHEST SAVE")) {
			enderchestSaveTask.cancel();
		} else if(message.getStrings().get(0).equals("INVENTORY SAVE")) {
			inventorySaveTask.cancel();
		}

		saving = false;
	}

}
