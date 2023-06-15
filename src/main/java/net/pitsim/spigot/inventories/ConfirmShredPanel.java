package net.pitsim.spigot.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.adarkzone.DarkzoneBalancing;
import net.pitsim.spigot.aitems.PitItem;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmShredPanel extends AGUIPanel {
	public ItemStack item;
	public DarkzoneBalancing.ShredValue shredValue;

	public ConfirmShredPanel(AGUI gui, ItemStack item, DarkzoneBalancing.ShredValue shredValue) {
		super(gui);
		this.item = item;
		this.shredValue = shredValue;

		ALoreBuilder loreBuilder = new ALoreBuilder(
				"&7Shredding the following Item: ",
				"&8&m------------------------",
				item.getItemMeta().getDisplayName()
		);
		loreBuilder.addLore(item.getItemMeta().getLore());
		loreBuilder.addLore("&8&m------------------------",
				"",
				"&7Gaining: &f" + shredValue.getLowSouls() + "&7-&f" + shredValue.getHighSouls() + " Souls", "",
				"&eClick to confirm Shred!"
		);

		AItemStackBuilder confirmBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 5)
				.setName("&a&lCONFIRM SHRED")
				.setLore(loreBuilder);
		getInventory().setItem(11, confirmBuilder.getItemStack());

		AItemStackBuilder cancelBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 14)
				.setName("&c&lCANCEL")
				.setLore(new ALoreBuilder(
						"&7Shredding: " + item.getItemMeta().getDisplayName(),
						"&7Gaining: &f" + shredValue.getLowSouls() + "&7-&f" + shredValue.getHighSouls() + " Souls", "",
						"&eClick to cancel purchase!"
				));
		getInventory().setItem(15, cancelBuilder.getItemStack());
	}

	@Override
	public String getName() {
		return "Confirm Purchase?";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getSlot() == 11) {
			int soulsGained = shredValue.getRandomSouls();

			player.getInventory().remove(item);
			NBTItem nbtItem = new NBTItem(item);
			boolean removed = false;

			for(int i = 0; i < player.getInventory().getContents().length; i++) {
				PitItem pitItem = ItemFactory.getItem(player.getInventory().getContents()[i]);
				if(pitItem == null || !pitItem.hasUUID) continue;
				NBTItem invNBTItem = new NBTItem(player.getInventory().getContents()[i]);
				if(invNBTItem.getString(NBTTag.ITEM_UUID.getRef()).equals(nbtItem.getString(NBTTag.ITEM_UUID.getRef()))) {
					player.getInventory().setItem(i, null);
					removed = true;
					break;
				}
			}

			if(!removed) throw new RuntimeException("Could not remove item from inventory!");

			player.updateInventory();

			Sounds.JEWEL_SHRED1.play(player);
			new BukkitRunnable() {
				@Override
				public void run() {
					Sounds.JEWEL_SHRED2.play(player);
				}
			}.runTaskLater(PitSim.INSTANCE, 10);

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			pitPlayer.giveSouls(soulsGained);
			pitPlayer.stats.itemsShredded++;

			AOutput.send(player, "&b&lSHOP!&7 Shredded &f" + item.getItemMeta().getDisplayName() + " &7for &f" + soulsGained + " Souls&7!");
			player.closeInventory();
		}

		if(event.getSlot() == 15) openPreviousGUI();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
