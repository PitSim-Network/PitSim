package dev.kyro.pitsim.inventories.view;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.perks.NoPerk;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MainViewPanel extends AGUIPanel {
	public ViewGUI viewGUI;

	public MainViewPanel(AGUI gui) {
		super(gui);
		viewGUI = (ViewGUI) gui;

		Player target = viewGUI.target;
		PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
		getInventory().setItem(0, target.getEquipment().getHelmet());
		getInventory().setItem(9, target.getEquipment().getChestplate());
		getInventory().setItem(18, target.getEquipment().getLeggings());
		getInventory().setItem(27, target.getEquipment().getBoots());
		for(int i = 0; i < 9; i++) {
			ItemStack itemStack = target.getInventory().getItem(i);
			getInventory().setItem(i + 36, itemStack);
		}

		ALoreBuilder skullLore = new ALoreBuilder();
		String rankMessage = ChatColor.translateAlternateColorCodes('&', "&7&8[%luckperms_primary_group_name%&8] %luckperms_prefix%" + target.getName());
		ItemStack skull = new AItemStackBuilder(Material.SKULL_ITEM, 1, 3)
				.setName(PlaceholderAPI.setPlaceholders(target, rankMessage))
				.setLore(skullLore)
				.getItemStack();
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(target.getName());
		skull.setItemMeta(skullMeta);
		getInventory().setItem(11, skull);

		for(int i = 0; i < pitTarget.pitPerks.size(); i++) {
			PitPerk pitPerk = pitTarget.pitPerks.get(i);
			ItemStack itemStack = pitPerk.getDisplayItem();
			new AItemStackBuilder(itemStack)
					.setName("&a" + itemStack.getItemMeta().getDisplayName());
			if(pitPerk.getClass() == NoPerk.class) {
				new AItemStackBuilder(itemStack)
						.setLore(new ALoreBuilder("&7No perk selected!"));
			}
			getInventory().setItem(i + 13, itemStack);
		}

		ALoreBuilder killstreakLore = new ALoreBuilder();
		for(Killstreak killstreak : pitTarget.killstreaks) {
			if(killstreak.getClass() != NoKillstreak.class) {
				killstreakLore.addLore("&7Every &c" + killstreak.killInterval + " &7kills: &a" + killstreak.name);
			}
		}
		killstreakLore.addLore("&7Megastreak: &a" + pitTarget.megastreak.getRawName());
		ItemStack killstreaks = new AItemStackBuilder(pitTarget.megastreak.guiItem())
				.setName("&aKillstreaks")
				.setLore(killstreakLore)
				.getItemStack();
		getInventory().setItem(23, killstreaks);

		ItemStack inventory = new AItemStackBuilder(Material.CHEST)
				.setName("&aInventory")
				.setLore(new ALoreBuilder(
						"&7Check out this player's",
						"&7inventory"
				))
				.getItemStack();
		getInventory().setItem(24, inventory);
	}

	@Override
	public String getName() {
		return ((ViewGUI) gui).target.getName() + "'s Profile";
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
	}

	@Override
	public void onOpen(InventoryOpenEvent event) { }

	@Override
	public void onClose(InventoryCloseEvent event) { }
}
