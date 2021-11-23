package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PlayerStats;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class StatPanel extends AGUIPanel {
	public StatGUI statGUI;

	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public PlayerStats stats;
	public StatPanel(AGUI gui) {
		super(gui);
		statGUI = (StatGUI) gui;
		this.stats = pitPlayer.stats;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);

		Map<Integer, ItemStack> statMap = getStatItems();
		for(Map.Entry<Integer, ItemStack> entry : statMap.entrySet()) {
			getInventory().setItem(entry.getKey(), entry.getValue());
		}

		updateInventory();
	}

	public Map<Integer, ItemStack> getStatItems() {
		Map<Integer, ItemStack> statMap = new HashMap<>();

		AItemStackBuilder combat = new AItemStackBuilder(Math.random() < 0.5 ? Material.DIAMOND_SWORD : Material.IRON_SWORD)
				.setName("&cCombat Statistics")
				.setLore(new ALoreBuilder(
						"&7Player Kills: &e" + large(stats.playerKills),
						"&7Bot Kills: &e" + large(stats.botKills),
						"&7Hoppers Kills: &e" + large(stats.hopperKills),
						"&7Sword Hits: &e" + large(stats.swordHits),
						"&7Damage Dealt: &e" + large(stats.damageDealt),
						"&7True Damage Dealt: &e" + large(stats.trueDamageDealt),
						"",
						"&7Arrows Shot: &e" + large(stats.arrowShots),
						"&7Arrows Hit: &e" + large(stats.arrowHits),
						"&7Arrow Accuracy: &e" + percent(stats.getArrowAccuracy()),
						"",
						"&7Deaths: &e" + large(stats.deaths),
						"&7Damage Taken: &e" + large(stats.damageTaken),
						"&7True Damage Taken: &e" + large(stats.trueDamageTaken)
				));
		statMap.put(10, combat.getItemStack());

		Material megastreakMaterial = Math.random() < 0.25 ? Material.BLAZE_POWDER : (Math.random() < 0.5 ? Material.DIAMOND_HELMET : Math.random() < 0.75 ? Material.GOLD_BOOTS : Material.ENDER_STONE);
				AItemStackBuilder megastreak = new AItemStackBuilder(megastreakMaterial)
				.setName("&9Megastreak Statistics")
				.setLore(new ALoreBuilder(
						"&7Overdrive Streaks: &e" + large(stats.timesOnOverdrive),
						"&7Beastmode Streaks: &e" + large(stats.timesOnBeastmode),
						"&7Highlander Streaks: &e" + large(stats.timesOnHighlander),
						"&7To The Moon Streaks: &e" + large(stats.timesOnMoon),
						"&7Uberstreaks Completed: &e" + large(stats.ubersCompleted)
				));
		statMap.put(11, megastreak.getItemStack());

		ItemStack mystic = new AItemStackBuilder(Material.LEATHER_LEGGINGS).getItemStack();
		LeatherArmorMeta meta = (LeatherArmorMeta) mystic.getItemMeta();
		meta.setColor(Color.fromRGB(PantColor.getNormalRandom().hexColor));
		mystic.setItemMeta(meta);
		AItemStackBuilder mystics = new AItemStackBuilder(mystic)
				.setName("&eMystic Statistics")
				.setLore(new ALoreBuilder(
						"&7Disposed Income: &e" + large(stats.billionaire) + "g",
						"&7Perun Strikes: &e" + large(stats.perun),
						"&7Executions: &e" + large(stats.executioner),
						"&c\u2764&7s Risked: &e" + large(stats.gamble),
						"&7immobilized: &e" + time(stats.stun),
						"&7Stolen &c\u2764&7s: &e" + large(stats.lifesteal),
						"",
						"&7Homing Arrows: &e" + large(stats.robinhood),
						"&7Vollied Arrows: &e" + large(stats.volley),
						"&7Teleportations: &e" + large(stats.telebow),
						"&7Pulls: &e" + large(stats.pullbow),
						"&7Explosions: &e" + large(stats.explosive),
						"&7Lucky Shots: &e" + large(stats.lucky),
						"&7Drains: &e" + large(stats.drain),
						"&7Wasps: &e" + large(stats.wasp),
						"&7Pins: &e" + large(stats.pin),
						"&7FTTS': &e" + large(stats.ftts),
						"&7PCTS': &e" + large(stats.pcts),
						"",
						"&7RGM Stacks (Against you): &e" + large(stats.rgm),
						"&7Second Hits: &e" + large(stats.regularity)
				));
		statMap.put(12, mystics.getItemStack());

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 8, 13);

		AItemStackBuilder events = new AItemStackBuilder(Material.WOOL, 1, (int) (Math.random() * 16))
				.setName("&6Event Statistics")
				.setLore(new ALoreBuilder(
						"&7Events Participated: &e" + large(stats.eventsParticipated),
						"&7Coming soon..."
				));
		statMap.put(14, events.getItemStack());

		AItemStackBuilder progression = new AItemStackBuilder(Math.random() < 0.5 ? Material.WHEAT : Material.GOLD_INGOT)
				.setName("&bProgression Statistics")
				.setLore(new ALoreBuilder(
						"&7Hours Played: &e" + new DecimalFormat("0.#").format(stats.getHoursPlayed()),
						"&7Total XP: &e" + large(stats.getTotalXP()),
						"&7Total Gold: &e" + large(stats.totalGold) + "g",
						"",
						"&7XP/Hour: &e" + ratio(stats.getXpPerHour()),
						"&7Gold/Hour: &e" + ratio(stats.getGoldPerHour()) + "g",
						"",
						"&7Player Kills/Deaths: &e" + ratio(stats.getPlayerKillsToDeaths()),
						"&7Bot Kills/Deaths: &e" + ratio(stats.getBotKillsToDeaths()),
						"&7Damage Dealt/Taken: &e" + ratio(stats.getDamageDealtToDamageTaken())
				));
		statMap.put(15, progression.getItemStack());

		AItemStackBuilder misc = new AItemStackBuilder(Math.random() < 0.5 ? Material.LAVA_BUCKET : Material.PAINTING)
				.setName("&dMiscellaneous Statistics")
				.setLore(new ALoreBuilder(
						"&7Highest Streak: &e" + large(stats.highestStreak),
						"&7Health Regained: &e" + large(stats.healthRegained),
						"&7Absorption Gained: &e" + large(stats.absorptionGained),
						"&7Bounties Claimed: &e" + large(stats.bountiesClaimed),
						"",
						"&7Jewels Completed: &e" + large(stats.jewelsCompleted),
						"&7Items Gemmed: &e" + large(stats.itemsGemmed),
						"&7Lives Lost: &e" + large(stats.livesLost),
						"&7Feathers Lost: &e" + large(stats.feathersLost),
						"",
						"&7Chat Messages: &e" + large(stats.chatMessages)
				));
		statMap.put(16, misc.getItemStack());

		return statMap;
	}

	public String time(double seconds) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		if(seconds < 60) return decimalFormat.format(seconds) + " seconds";
		if(seconds < 60 * 60) return decimalFormat.format(seconds / 60.0) + " minutes";
		if(seconds < 60 * 60 * 24) return decimalFormat.format(seconds / 60.0 / 60.0) + " hours";
		return decimalFormat.format(seconds / 60.0 / 60.0 / 24.0) + " days";
	}
	public String large(double large) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
		if(large < 1_000) return decimalFormat.format(large);
		if(large < 1_000_000) return decimalFormat.format(large / 1_000.0) + "K";
		if(large < 1_000_000_000) return decimalFormat.format(large / 1_000_000.0) + "M";
		return decimalFormat.format(large / 1_000_000_000) + "B";
	}
	public String ratio(double ratio) {
		if(ratio < 1_000) return new DecimalFormat("#,##0.###").format(ratio);
		if(ratio < 1_000_000) return new DecimalFormat("#,##0.#").format(ratio / 1_000) + "K";
		return new DecimalFormat("#,##0.#").format(ratio / 1_000_000) + "M";
	}
	public String percent(double percent) {
		return new DecimalFormat("0.0").format(percent * 100) + "%";
	}

	@Override
	public String getName() {
		return "Stats";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) { }

	@Override
	public void onOpen(InventoryOpenEvent event) { }

	@Override
	public void onClose(InventoryCloseEvent event) { }
}
