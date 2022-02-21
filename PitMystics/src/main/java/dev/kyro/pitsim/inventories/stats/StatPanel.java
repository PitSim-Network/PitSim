package dev.kyro.pitsim.inventories.stats;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PlayerStats;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
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
		this.statGUI = (StatGUI) gui;
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
						"&7Player Kills: &e" + Misc.formatLarge(stats.playerKills),
						"&7Bot Kills: &e" + Misc.formatLarge(stats.botKills),
						"&7Hoppers Kills: &e" + Misc.formatLarge(stats.hopperKills),
						"&7Sword Hits: &e" + Misc.formatLarge(stats.swordHits),
						"&7Damage Dealt: &e" + Misc.formatLarge(stats.damageDealt),
						"&7True Damage Dealt: &e" + Misc.formatLarge(stats.trueDamageDealt),
						"",
						"&7Arrows Shot: &e" + Misc.formatLarge(stats.arrowShots),
						"&7Arrows Hit: &e" + Misc.formatLarge(stats.arrowHits),
						"&7Arrow Accuracy: &e" + Misc.formatPercent(stats.getArrowAccuracy()),
						"",
						"&7Deaths: &e" + Misc.formatLarge(stats.deaths),
						"&7Damage Taken: &e" + Misc.formatLarge(stats.damageTaken),
						"&7True Damage Taken: &e" + Misc.formatLarge(stats.trueDamageTaken)
				));
		statMap.put(10, combat.getItemStack());

		Material megastreakMaterial = Math.random() < 0.25 ? Material.BLAZE_POWDER : (Math.random() < 0.5 ? Material.DIAMOND_HELMET : Math.random() < 0.75 ? Material.GOLD_BOOTS : Material.ENDER_STONE);
		AItemStackBuilder megastreak = new AItemStackBuilder(megastreakMaterial)
				.setName("&9Megastreak Statistics")
				.setLore(new ALoreBuilder(
						"&7Overdrive Streaks: &e" + Misc.formatLarge(stats.timesOnOverdrive),
						"&7Beastmode Streaks: &e" + Misc.formatLarge(stats.timesOnBeastmode),
						"&7Highlander Streaks: &e" + Misc.formatLarge(stats.timesOnHighlander),
						"&7To The Moon Streaks: &e" + Misc.formatLarge(stats.timesOnMoon),
						"&7RNGesus Streaks: &e" + Misc.formatLarge(stats.timesOnRNGesus),
						"&7Uberstreaks Completed: &e" + Misc.formatLarge(stats.ubersCompleted)
				));
		statMap.put(11, megastreak.getItemStack());

		ItemStack mystic = new AItemStackBuilder(Material.LEATHER_LEGGINGS).getItemStack();
		LeatherArmorMeta meta = (LeatherArmorMeta) mystic.getItemMeta();
		meta.setColor(Color.fromRGB(PantColor.getNormalRandom().hexColor));
		mystic.setItemMeta(meta);
		AItemStackBuilder mystics = new AItemStackBuilder(mystic)
				.setName("&eMystic Statistics")
				.setLore(new ALoreBuilder(
						"&7Disposed Income: &e" + Misc.formatLarge(stats.billionaire) + "g",
						"&7Perun Strikes: &e" + Misc.formatLarge(stats.perun),
						"&7Executions: &e" + Misc.formatLarge(stats.executioner),
						"&c\u2764&7s Risked: &e" + Misc.formatLarge(stats.gamble),
						"&7immobilized: &e" + Misc.formatDuration(stats.stun),
						"&7Stolen &c\u2764&7s: &e" + Misc.formatLarge(stats.lifesteal),
						"",
						"&7Homing Arrows: &e" + Misc.formatLarge(stats.robinhood),
						"&7Vollied Arrows: &e" + Misc.formatLarge(stats.volley),
						"&7Teleportations: &e" + Misc.formatLarge(stats.telebow),
						"&7Pulls: &e" + Misc.formatLarge(stats.pullbow),
						"&7Explosions: &e" + Misc.formatLarge(stats.explosive),
						"&7Lucky Shots: &e" + Misc.formatLarge(stats.lucky),
						"&7Drains: &e" + Misc.formatLarge(stats.drain),
						"&7Wasps: &e" + Misc.formatLarge(stats.wasp),
						"&7Pins: &e" + Misc.formatLarge(stats.pin),
						"&7FTTS': &e" + Misc.formatLarge(stats.ftts),
						"&7PCTS': &e" + Misc.formatLarge(stats.pcts),
						"",
						"&7RGM Stacks (Against you): &e" + Misc.formatLarge(stats.rgm),
						"&7Second Hits: &e" + Misc.formatLarge(stats.regularity)
				));
		statMap.put(12, mystics.getItemStack());

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 8, 13);

		AItemStackBuilder events = new AItemStackBuilder(Material.WOOL, 1, (int) (Math.random() * 16))
				.setName("&6Event Statistics")
				.setLore(new ALoreBuilder(
						"&7Events Participated: &e" + Misc.formatLarge(stats.eventsParticipated),
						"&7Coming soon..."
				));
		statMap.put(14, events.getItemStack());

		AItemStackBuilder progression = new AItemStackBuilder(Math.random() < 0.5 ? Material.WHEAT : Material.GOLD_INGOT)
				.setName("&bProgression Statistics")
				.setLore(new ALoreBuilder(
						"&7Hours Played: &e" + new DecimalFormat("0.#").format(stats.getHoursPlayed()),
						"&7Total XP: &e" + Misc.formatLarge(stats.getTotalXP()),
						"&7Total Gold: &e" + Misc.formatLarge(stats.totalGold) + "g",
						"",
						"&7XP/Hour: &e" + Misc.formatRatio(stats.getXpPerHour()),
						"&7Gold/Hour: &e" + Misc.formatRatio(stats.getGoldPerHour()) + "g",
						"",
						"&7Player Kills/Deaths: &e" + Misc.formatRatio(stats.getPlayerKillsToDeaths()),
						"&7Bot Kills/Deaths: &e" + Misc.formatRatio(stats.getBotKillsToDeaths()),
						"&7Damage Dealt/Taken: &e" + Misc.formatRatio(stats.getDamageDealtToDamageTaken())
				));
		statMap.put(15, progression.getItemStack());

		AItemStackBuilder misc = new AItemStackBuilder(Math.random() < 0.5 ? Material.LAVA_BUCKET : Material.PAINTING)
				.setName("&dMiscellaneous Statistics")
				.setLore(new ALoreBuilder(
						"&7Highest Streak: &e" + Misc.formatLarge(stats.highestStreak),
						"&7Health Regained: &e" + Misc.formatLarge(stats.healthRegained),
						"&7Absorption Gained: &e" + Misc.formatLarge(stats.absorptionGained),
						"&7Bounties Claimed: &e" + Misc.formatLarge(stats.bountiesClaimed),
						"",
						"&7Jewels Completed: &e" + Misc.formatLarge(stats.jewelsCompleted),
						"&7Items Gemmed: &e" + Misc.formatLarge(stats.itemsGemmed),
						"&7Lives Lost: &e" + Misc.formatLarge(stats.livesLost),
						"&7Feathers Lost: &e" + Misc.formatLarge(stats.feathersLost),
						"",
						"&7Chat Messages: &e" + Misc.formatLarge(stats.chatMessages)
				));
		statMap.put(16, misc.getItemStack());

		return statMap;
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
	public void onClick(InventoryClickEvent event) {
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
