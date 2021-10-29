package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KillstreakPanel extends AGUIPanel {

	public static Map<Killstreak, Integer> killstreakSlots = new HashMap<>();

	public PerkGUI perkGUI;
	public KillstreakPanel(AGUI gui) {
		super(gui);
		perkGUI = (PerkGUI) gui;

	}

	@Override
	public String getName() {
		return "Killstreaks";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		int killstreakSlot = perkGUI.killstreakSlot;

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 49) {
				openPreviousGUI();
				return;
			}

			for(Map.Entry<Killstreak, Integer> entry : killstreakSlots.entrySet()) {

				if(entry.getValue() == slot) {
					Killstreak killstreak = entry.getKey();

					if(!killstreak.refName.equals("NoKillstreak")) {

						if(hasKillstreakEquipped(player, killstreak)) {
							AOutput.error(player, "&cThat killstreak is already equipped");
							Sounds.ERROR.play(player);
							return;
						}
						if(pitPlayer.prestige < killstreak.prestige) {
							AOutput.error(player, "&cYou aren't high enough prestige to use this!");
							Sounds.ERROR.play(player);
							return;
						}


					}

					Killstreak previousKillstreak = getKillstreakFromInterval(player, killstreak.killInterval);
					for(int i = 0; i < pitPlayer.killstreaks.size(); i++) {
						if(previousKillstreak != null && pitPlayer.killstreaks.get(i).refName.equals(previousKillstreak.refName)) {
							if(i == killstreakSlot - 1) continue;
							if(previousKillstreak.refName.equals("NoKillstreak")) continue;
							pitPlayer.killstreaks.set(i, NoKillstreak.INSTANCE);
							perkGUI.saveKillstreak(NoKillstreak.INSTANCE, i + 1);
							AOutput.error(player, "&c&lDISABLED! &7Disabled &a" + previousKillstreak.name + " &7because you cannot have two killstreaks with the same kill interval!");
						}
					}


					pitPlayer.killstreaks.set(killstreakSlot - 1, killstreak);
					perkGUI.saveKillstreak(killstreak, killstreakSlot);
					Sounds.SUCCESS.play(player);
					openPreviousGUI();


				}
			}

		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);

		AItemStackBuilder builder5Kills = new AItemStackBuilder(Material.ITEM_FRAME);
		builder5Kills.setName("&c5 Kills");
		if(getKillstreakFromInterval(player, 5) != null) {
			builder5Kills.setLore(new ALoreBuilder("&7Selected: &e" + Objects.requireNonNull(getKillstreakFromInterval(player, 5)).name));
			builder5Kills.addEnchantGlint(true);
		}
		getInventory().setItem(10, builder5Kills.getItemStack());

		AItemStackBuilder builder10Kills = new AItemStackBuilder(Material.ITEM_FRAME);
		builder10Kills.setName("&c10 Kills");
		if(getKillstreakFromInterval(player, 10) != null) {
			builder10Kills.setLore(new ALoreBuilder("&7Selected: &e" + Objects.requireNonNull(getKillstreakFromInterval(player, 10)).name));
			builder10Kills.addEnchantGlint(true);
		}
		getInventory().setItem(19, builder10Kills.getItemStack());

		AItemStackBuilder builder25Kills = new AItemStackBuilder(Material.ITEM_FRAME);
		builder25Kills.setName("&c25 Kills");
		if(getKillstreakFromInterval(player, 7) != null) {
			builder25Kills.setLore(new ALoreBuilder("&7Selected: &e" + Objects.requireNonNull(getKillstreakFromInterval(player, 25)).name));
			builder25Kills.addEnchantGlint(true);
		}
		getInventory().setItem(28, builder25Kills.getItemStack());

		AItemStackBuilder builder50Kills = new AItemStackBuilder(Material.ITEM_FRAME);
		builder50Kills.setName("&c50 Kills");
		if(getKillstreakFromInterval(player, 7) != null) {
			builder50Kills.setLore(new ALoreBuilder("&7Selected: &e" + Objects.requireNonNull(getKillstreakFromInterval(player, 7)).name));
			builder50Kills.addEnchantGlint(true);
		}
		getInventory().setItem(37, builder50Kills.getItemStack());

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "To Perks & Killstreaks");
		meta.setLore(lore);
		back.setItemMeta(meta);

		getInventory().setItem(49, back);

		for(Killstreak killstreak : PerkManager.killstreaks) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

			if(killstreak.refName.equals("NoKillstreak")) {
				AItemStackBuilder builder = new AItemStackBuilder(killstreak.getDisplayItem());
				builder.setLore(new ALoreBuilder(builder.getItemStack().getItemMeta().getLore()).addLore("", "&eClick to remove killstreak!"));
				getInventory().setItem(50, builder.getItemStack());
				killstreakSlots.put(killstreak, 50);
				continue;
			}

			int slot = getIntervalStartingSlot(killstreak.killInterval);

			while(getInventory().getItem(slot) != null) {
				slot++;
			}

			AItemStackBuilder builder = new AItemStackBuilder(killstreak.getDisplayItem());
			ALoreBuilder loreBuilder = new ALoreBuilder(builder.getItemStack().getItemMeta().getLore()).addLore("");
			if(hasKillstreakEquipped(player, killstreak)) {
				builder.setName("&a" + killstreak.name);
				loreBuilder.addLore("&aAlready selected!");
				builder.addEnchantGlint(true);
			} else if(pitPlayer.prestige < killstreak.prestige) {
				builder.setName("&c" + killstreak.name);
				loreBuilder.addLore("&cUnlocked at prestige &e" + AUtil.toRoman(killstreak.prestige));
			} else {
				builder.setName("&e" + killstreak.name);
				loreBuilder.addLore("&eClick to select!");
			}

			builder.setLore(loreBuilder);
			getInventory().setItem(slot, builder.getItemStack());
			killstreakSlots.put(killstreak, slot);
		}

	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		for(int i = 0; i < getInventory().getSize(); i++) {
			getInventory().setItem(i, new ItemStack(Material.AIR));
		}

	}

	public static Killstreak getKillstreakFromInterval(Player player, int interval) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		for(Killstreak killstreak : pitPlayer.killstreaks) {
			if(killstreak.killInterval == interval) return killstreak;
		}

		return null;
	}

	public static int getIntervalStartingSlot(int interval) {
		if(interval == 5) return 10;
		if(interval == 10) return 19;
		if(interval == 25) return 28;
		return 37;
	}

	public static boolean hasKillstreakEquipped(Player player, Killstreak killstreak) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		for(Killstreak equippedKillstreak : pitPlayer.killstreaks) {
			if(equippedKillstreak.refName.equals(killstreak.refName)) return true;
		}
		return false;
	}
}
