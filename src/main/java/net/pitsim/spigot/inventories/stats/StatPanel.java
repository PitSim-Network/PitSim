package net.pitsim.spigot.inventories.stats;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.controllers.objects.PlayerStats;
import net.pitsim.spigot.enums.PantColor;
import net.pitsim.spigot.misc.Formatter;
import net.pitsim.spigot.misc.Misc;
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
						"&7Player Kills: &e" + Formatter.formatLarge(stats.playerKills),
						"&7Bot Kills: &e" + Formatter.formatLarge(stats.botKills),
						"&7Hoppers Kills: &e" + Formatter.formatLarge(stats.hopperKills),
						"&7Sword Hits: &e" + Formatter.formatLarge(stats.swordHits),
						"&7Damage Dealt: &e" + Formatter.formatLarge(stats.damageDealt),
						"&7True Damage Dealt: &e" + Formatter.formatLarge(stats.trueDamageDealt),
						"",
						"&7Arrows Shot: &e" + Formatter.formatLarge(stats.arrowShots),
						"&7Arrows Hit: &e" + Formatter.formatLarge(stats.arrowHits),
						"&7Arrow Accuracy: &e" + Formatter.formatPercent(stats.getArrowAccuracy()),
						"",
						"&7Deaths: &e" + Formatter.formatLarge(stats.deaths),
						"&7Damage Taken: &e" + Formatter.formatLarge(stats.damageTaken),
						"&7True Damage Taken: &e" + Formatter.formatLarge(stats.trueDamageTaken)
				));
		statMap.put(10, combat.getItemStack());

		Material megastreakMaterial = Math.random() < 0.25 ? Material.BLAZE_POWDER : (Math.random() < 0.5 ? Material.DIAMOND_HELMET : Math.random() < 0.75 ? Material.GOLD_BOOTS : Material.ENDER_STONE);
		AItemStackBuilder megastreak = new AItemStackBuilder(megastreakMaterial)
				.setName("&9Megastreak Statistics")
				.setLore(new ALoreBuilder(
						"&7Overdrive Streaks: &e" + Formatter.formatLarge(stats.timesOnOverdrive),
						"&7Beastmode Streaks: &e" + Formatter.formatLarge(stats.timesOnBeastmode),
						"&7Highlander Streaks: &e" + Formatter.formatLarge(stats.timesOnHighlander),
						"&7To The Moon Streaks: &e" + Formatter.formatLarge(stats.timesOnMoon),
						"&7RNGesus Completed: &e" + Formatter.formatLarge(stats.rngesusCompleted),
						"&7Uberstreaks Completed: &e" + Formatter.formatLarge(stats.ubersCompleted)
				));
		statMap.put(11, megastreak.getItemStack());

		ItemStack mystic = new AItemStackBuilder(Material.LEATHER_LEGGINGS).getItemStack();
		LeatherArmorMeta meta = (LeatherArmorMeta) mystic.getItemMeta();
		meta.setColor(Color.fromRGB(PantColor.getNormalRandom().hexColor));
		mystic.setItemMeta(meta);
		AItemStackBuilder mystics = new AItemStackBuilder(mystic)
				.setName("&eMystic Statistics")
				.setLore(new ALoreBuilder(
						"&7Disposed Income: &e" + Formatter.formatLarge(stats.billionaire) + "g",
						"&7Perun Strikes: &e" + Formatter.formatLarge(stats.perun),
						"&7Executions: &e" + Formatter.formatLarge(stats.executioner),
						"&c\u2764&7s Risked: &e" + Formatter.formatLarge(stats.gamble),
						"&7immobilized: &e" + Formatter.formatDurationMostSignificant(stats.stun),
						"&7Stolen &c\u2764&7s: &e" + Formatter.formatLarge(stats.lifesteal),
						"",
						"&7Homing Arrows: &e" + Formatter.formatLarge(stats.robinhood),
						"&7Vollied Arrows: &e" + Formatter.formatLarge(stats.volley),
						"&7Teleportations: &e" + Formatter.formatLarge(stats.telebow),
						"&7Pulls: &e" + Formatter.formatLarge(stats.pullbow),
						"&7Explosions: &e" + Formatter.formatLarge(stats.explosive),
						"&7Lucky Shots: &e" + Formatter.formatLarge(stats.lucky),
						"&7Drains: &e" + Formatter.formatLarge(stats.drain),
						"&7Wasps: &e" + Formatter.formatLarge(stats.wasp),
						"&7Pins: &e" + Formatter.formatLarge(stats.pin),
						"&7FTTS': &e" + Formatter.formatLarge(stats.ftts),
						"&7PCTS': &e" + Formatter.formatLarge(stats.pcts),
						"",
						"&7RGM Stacks (Against you): &e" + Formatter.formatLarge(stats.rgm),
						"&7Second Hits: &e" + Formatter.formatLarge(stats.regularity)
				));
		statMap.put(12, mystics.getItemStack());

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 8, 13);

		AItemStackBuilder darkzone = new AItemStackBuilder(Material.GOLD_HOE)
				.setName("&5Darkzone Statistics")
				.setLore(new ALoreBuilder(
						"&7Bosses Killed: &e" + Formatter.formatLarge(stats.bossesKilled),
						"&7Mobs Killed: &e" + Formatter.formatLarge(stats.mobsKilled),
						"",
						"&7Lifetime Souls: &e" + Formatter.formatLarge(stats.lifetimeSouls),
						"&7Potions Brewed: &e" + Formatter.formatLarge(stats.potionsBrewed),
						"",
						"&7Auctions Won: &e" + Formatter.formatLarge(stats.auctionsWon),
						"&7Highest Bid: &e" + Formatter.formatLarge(stats.highestBid)
				));
		Misc.addEnchantGlint(darkzone.getItemStack());
		statMap.put(14, darkzone.getItemStack());

		AItemStackBuilder progression = new AItemStackBuilder(Math.random() < 0.5 ? Material.WHEAT : Material.GOLD_INGOT)
				.setName("&bProgression Statistics")
				.setLore(new ALoreBuilder(
						"&7Hours Played: &e" + new DecimalFormat("0.#").format(stats.getHoursPlayed()),
						"&7Total XP: &e" + Formatter.formatLarge(stats.getTotalXP()),
						"&7Total Gold: &e" + Formatter.formatLarge(stats.totalGold) + "g",
						"",
						"&7XP/Hour: &e" + Formatter.formatRatio(stats.getXpPerHour()),
						"&7Gold/Hour: &e" + Formatter.formatRatio(stats.getGoldPerHour()) + "g",
						"",
						"&7Player Kills/Deaths: &e" + Formatter.formatRatio(stats.getPlayerKillsToDeaths()),
						"&7Bot Kills/Deaths: &e" + Formatter.formatRatio(stats.getBotKillsToDeaths()),
						"&7Damage Dealt/Taken: &e" + Formatter.formatRatio(stats.getDamageDealtToDamageTaken())
				));
		statMap.put(15, progression.getItemStack());

		AItemStackBuilder misc = new AItemStackBuilder(Math.random() < 0.5 ? Material.LAVA_BUCKET : Material.PAINTING)
				.setName("&dMiscellaneous Statistics")
				.setLore(new ALoreBuilder(
						"&7Highest Streak: &e" + Formatter.formatLarge(stats.highestStreak),
						"&7Health Regained: &e" + Formatter.formatLarge(stats.healthRegained),
						"&7Absorption Gained: &e" + Formatter.formatLarge(stats.absorptionGained),
						"&7Bounties Claimed: &e" + Formatter.formatLarge(stats.bountiesClaimed),
						"",
						"&7Jewels Completed: &e" + Formatter.formatLarge(stats.jewelsCompleted),
						"&7Items Gemmed: &e" + Formatter.formatLarge(stats.itemsGemmed),
						"&7Lives Lost: &e" + Formatter.formatLarge(stats.livesLost),
						"&7Feathers Lost: &e" + Formatter.formatLarge(stats.feathersLost),
						"",
						"&7Chat Messages: &e" + Formatter.formatLarge(stats.chatMessages)
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
