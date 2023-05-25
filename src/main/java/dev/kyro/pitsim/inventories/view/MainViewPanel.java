package dev.kyro.pitsim.inventories.view;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticguilds.Guild;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.DisplayItemType;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.storage.EnderchestPanel;
import dev.kyro.pitsim.storage.StorageProfile;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MainViewPanel extends AGUIPanel {
	public ViewGUI viewGUI;

	public MainViewPanel(AGUI gui) {
		super(gui, true);
		viewGUI = (ViewGUI) gui;
		buildInventory();

		StorageProfile target = viewGUI.target;
		PitPlayer pitTarget = viewGUI.getPitPlayer();
		assert pitTarget != null;

		ItemStack[] armor = target.getArmor();
		getInventory().setItem(0, armor[3]);
		getInventory().setItem(9, armor[2]);
		getInventory().setItem(18, armor[1]);
		getInventory().setItem(27, armor[0]);

		ItemStack[] inventory = target.getInventory();
		for(int i = 0; i < 9; i++) {
			ItemStack itemStack = inventory[i];
			getInventory().setItem(i + 36, itemStack);
		}

		Guild guild = ViewGUI.getGuild(viewGUI.uuid);
		ALoreBuilder skullLore = new ALoreBuilder("&7Guild: " + (guild != null ?
				guild.color + guild.name + (guild.tag.isEmpty() ? "" : " &8(" + guild.color + "#" + guild.tag + "&8)") : "&cNone"));
		ItemStack skull = new AItemStackBuilder(Material.SKULL_ITEM, 1, 3)
				.setName(Misc.getRankString(viewGUI.uuid) + " " + viewGUI.name)
				.setLore(skullLore)
				.getItemStack();
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(viewGUI.name);
		skull.setItemMeta(skullMeta);
		getInventory().setItem(11, skull);

		for(int i = 0; i < pitTarget.pitPerks.size(); i++) {
			PitPerk pitPerk = pitTarget.pitPerks.get(i);
			ItemStack itemStack = pitPerk.getDisplayStack(player, DisplayItemType.VIEW_PANEL);
			getInventory().setItem(i + 13, itemStack);
		}

		ALoreBuilder killstreakLore = new ALoreBuilder();
		for(Killstreak killstreak : pitTarget.killstreaks) {
			if(killstreak instanceof NoKillstreak) continue;
			killstreakLore.addLore("&7Every &c" + killstreak.killInterval + " &7kills: &a" + killstreak.displayName);
		}
		killstreakLore.addLore("&7Megastreak: &a" + pitTarget.getMegastreak().getRefName());
		ItemStack killstreaks = new AItemStackBuilder(pitTarget.getMegastreak().getDisplayStack(player, DisplayItemType.VIEW_PANEL))
				.setName("&aKillstreaks")
				.setLore(killstreakLore)
				.getItemStack();
		getInventory().setItem(23, killstreaks);

		ItemStack inventoryDisplay = new AItemStackBuilder(Material.CHEST)
				.setName("&aInventory")
				.setLore(new ALoreBuilder(
						"&7Check out this player's",
						"&7inventory"
				))
				.getItemStack();
		getInventory().setItem(24, inventoryDisplay);
		ItemStack enderChestDisplay = new AItemStackBuilder(Material.ENDER_CHEST)
				.setName("&5Ender Chest")
				.setLore(new ALoreBuilder(
						"&7Check out this player's",
						"&7ender chest"
				))
				.getItemStack();
		getInventory().setItem(25, enderChestDisplay);
	}

	@Override
	public String getName() {
		return (viewGUI.name + "'s Profile");
	}

	@Override
	public int getRows() {
		return 5;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();
		if(slot == 24) {
			openPanel(viewGUI.inventoryViewPanel);
		}
		if(slot == 25) {
			openPanel(new EnderchestPanel(viewGUI, viewGUI.target));
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
