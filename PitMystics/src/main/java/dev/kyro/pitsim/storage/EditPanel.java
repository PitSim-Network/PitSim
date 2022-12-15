package dev.kyro.pitsim.storage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.ProxyMessaging;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class EditPanel extends AGUIPanel {

	public EditSession session;

	public EditPanel(AGUI gui, EditSession session) {
		super(gui);
		this.session = session;
	}

	@Override
	public String getName() {
		return "Choose Edit Mode";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;

		int slot = event.getSlot();
		if(slot == 11) {
			if(session.isPlayerOnline() && session.getPlayerServer().equals(PitSim.serverName)) {
				if(Bukkit.getPlayer(session.getPlayerUUID()) == null) {
					AOutput.error(session.getStaffMember(), "&cThe player is no longer online!");
					session.respond(EditType.CANCELED);
				} else session.respond(EditType.ONLINE);
			} else if(session.isPlayerOnline()) {
				String[] server = session.getPlayerServer().split("-");
				if(!session.getPlayerServer().contains("pitsim") && !session.getPlayerServer().contains("darkzone")) return;
				if(server.length != 2) return;
				int serverNum = Integer.parseInt(server[1]);

				if(server[0].equalsIgnoreCase("pitsim")) {
					ProxyMessaging.switchPlayer(player, serverNum);
					session.respond(EditType.CANCELED);
				}

				if(server[0].equalsIgnoreCase("darkzone")) {
					ProxyMessaging.darkzoneSwitchPlayer(player, serverNum);
					session.respond(EditType.CANCELED);
				}
			} else {
				session.respond(EditType.CANCELED);
				player.closeInventory();
			}
		} else if(slot == 15) {
			if(!session.isPlayerOnline()) {
				session.respond(EditType.OFFLINE);
			} else session.respond(EditType.OFFLINE_KICK);
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		AItemStackBuilder onlineBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 5);
		if(session.isPlayerOnline() && PitSim.serverName.equals(session.getPlayerServer())) {
			onlineBuilder.setName("&a&lONLINE EDIT");
			ALoreBuilder onlineLore = new ALoreBuilder();
			onlineLore.addLore("&7The player is currently on this instance", "&7You may edit their inventory without issues",
					"", "&aClick to Edit Inventory");
			onlineBuilder.setLore(onlineLore);
		} else if(session.isPlayerOnline()) {
			ALoreBuilder onlineLore = new ALoreBuilder();
			onlineBuilder.getItemStack().setDurability((short) 4);
			onlineBuilder.setName("&e&lONLINE EDIT");
			onlineLore.addLore("&7The player is currently in another instance", "&7You cannot edit their inventory from here",
					"", "&e&lClick to transfer to the player's instance");
			onlineBuilder.setLore(onlineLore);
		} else {
			ALoreBuilder onlineLore = new ALoreBuilder();
			onlineBuilder.getItemStack().setDurability((short) 14);
			onlineBuilder.setName("&c&lONLINE EDIT");
			onlineLore.addLore("&7The player is currently offline", "&7You cannot edit their inventory from here",
					"", "&c&lClick to Close this Menu");
			onlineBuilder.setLore(onlineLore);
		}
		getInventory().setItem(11, onlineBuilder.getItemStack());

		AItemStackBuilder offlineBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 5);
		if(!session.isPlayerOnline()) {
			offlineBuilder.setName("&a&lOFFLINE EDIT");
			ALoreBuilder offlineLore = new ALoreBuilder();
			offlineLore.addLore("&7The player is not playing pitsim", "&7You may edit their inventory without issues",
					"", "&aClick to Edit Inventory");
			offlineBuilder.setLore(offlineLore);
		} else {
			ALoreBuilder offlineLore = new ALoreBuilder();
			offlineBuilder.getItemStack().setDurability((short) 4);
			offlineBuilder.setName("&e&lOFFLINE EDIT");
			offlineLore.addLore("&7The player is currently online", "&7They will have to leave to be edited",
					"", "&eClick to Kick and Edit Inventory");
			offlineBuilder.setLore(offlineLore);
		}
		getInventory().setItem(15, offlineBuilder.getItemStack());
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(!session.hasResponded) session.respond(EditType.CANCELED);
	}
}
