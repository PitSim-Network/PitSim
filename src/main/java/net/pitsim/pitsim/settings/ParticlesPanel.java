package net.pitsim.pitsim.settings;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ParticlesPanel extends AGUIPanel {
	public SettingsGUI settingsGUI;

	public static ItemStack backItem;
	public ItemStack auraItem;
	public ItemStack trailsItem;

	static {
		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&cBack")
				.setLore(new ALoreBuilder(
						"&7Click to go to the previous screen"
				))
				.getItemStack();
	}

	public ParticlesPanel(AGUI gui) {
		super(gui);
		settingsGUI = (SettingsGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);
		getInventory().setItem(22, backItem);

		setItems();
	}

	public void setItems() {
		auraItem = new AItemStackBuilder(Material.BEACON)
				.setName("&aAuras")
				.setLore(new ALoreBuilder(
						"&7Click to toggle aura particles",
						"",
						"&7State: " + Misc.getStateMessage(settingsGUI.pitPlayer.playerSettings.auraParticles)
				))
				.getItemStack();
		getInventory().setItem(10, auraItem);

		trailsItem = new AItemStackBuilder(Material.FIREWORK)
				.setName("&eParticle Trails")
				.setLore(new ALoreBuilder(
						"&7Click to toggle particle trails",
						"",
						"&7State: " + Misc.getStateMessage(settingsGUI.pitPlayer.playerSettings.trailParticles)
				))
				.getItemStack();
		getInventory().setItem(11, trailsItem);
	}

	@Override
	public String getName() {
		return ChatColor.GREEN + "" + ChatColor.BOLD + "Particle Settings";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(slot == 10) {
			settingsGUI.pitPlayer.playerSettings.auraParticles = !settingsGUI.pitPlayer.playerSettings.auraParticles;
			informPlayer("&a&lAuras", settingsGUI.pitPlayer.playerSettings.auraParticles);
		} else if(slot == 11) {
			settingsGUI.pitPlayer.playerSettings.trailParticles = !settingsGUI.pitPlayer.playerSettings.trailParticles;
			informPlayer("&e&lParticle Trails", settingsGUI.pitPlayer.playerSettings.trailParticles);
		} else if(slot == 22) {
			openPreviousGUI();
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public void informPlayer(String name, boolean state) {
		setItems();
		Sounds.SUCCESS.play(player);
		AOutput.send(player, "&e&lSETTINGS!&7 The setting " + name + "&7 has been " + Misc.getStateMessage(state));
	}
}
