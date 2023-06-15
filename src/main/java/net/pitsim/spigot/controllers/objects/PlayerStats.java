package net.pitsim.spigot.controllers.objects;

import com.google.cloud.firestore.annotation.Exclude;
import net.pitsim.spigot.controllers.PrestigeValues;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class PlayerStats {
	@Exclude
	public PitPlayer pitPlayer;
	@Exclude
	public UUID uuid;

//	Offense
	public int playerKills = 0;
	public int botKills = 0;
	public int hopperKills = 0;
	public int swordHits = 0;
	public int arrowShots = 0;
	public int arrowHits = 0;
	public double damageDealt = 0;
	public double trueDamageDealt = 0;

	@Exclude
	public double getArrowAccuracy() {
		if(arrowShots == 0) return 0;
		return (double) arrowHits / arrowShots;
	}

//	Defence
	public int deaths = 0;
	public double damageTaken = 0;
	public double trueDamageTaken = 0;

//	Megastreaks
	public int timesOnOverdrive = 0;
	public int timesOnStashStreaker = 0;
	public int timesOnRampage = 0;
	public int timesOnHighStakes = 0;
	public int timesOnBeastmode = 0;
	public int timesOnHighlander = 0;
	public int timesOnMoon = 0;
	public int timesOnProsperity = 0;
	public int timesOnApostle = 0;
	public int rngesusCompleted = 0;
	public int ubersCompleted = 0;

//	Mystics
	public int billionaire = 0;
	public int perun = 0;
	public int executioner = 0;
	public double gamble = 0;
	public double stun = 0;
	public double lifesteal = 0;

	public int robinhood = 0;
	public int volley = 0;
	public int telebow = 0;
	public int pullbow = 0;
	public int explosive = 0;
	public int lucky = 0;
	public int drain = 0;
	public int wasp = 0;
	public int pin = 0;
	public int ftts = 0;
	public int pcts = 0;

	public int rgm = 0;
	public int regularity = 0;

//	Darkzone Mystics
	public int devour = 0;

//	Progression
	public int minutesPlayed = 0;

	@Exclude
	public long getTotalXP() {
		return PrestigeValues.getTotalXP(pitPlayer.prestige, pitPlayer.level, pitPlayer.remainingXP);
	}

	public double totalGold;

	@Exclude
	public double getXpPerHour() {
		if(getHoursPlayed() == 0) return 0;
		return (double) getTotalXP() / getHoursPlayed();
	}

	@Exclude
	public double getGoldPerHour() {
		if(getHoursPlayed() == 0) return 0;
		return totalGold / getHoursPlayed();
	}

	@Exclude
	public double getHoursPlayed() {
		return (double) minutesPlayed / 60;
	}

	//	Ratios
	@Exclude
	public double getPlayerKillsToDeaths() {
		if(deaths == 0) return 0;
		return (double) playerKills / deaths;
	}

	@Exclude
	public double getBotKillsToDeaths() {
		if(deaths == 0) return 0;
		return (double) botKills / deaths;
	}

	@Exclude
	public double getDamageDealtToDamageTaken() {
		if(damageTaken == 0) return 0;
		return damageDealt / damageTaken;
	}

	//	Darkzone
	public int bossesKilled = 0;
	public int mobsKilled = 0;
	public int lifetimeSouls = 0;
	@Deprecated
	public int itemsEnchanted = 0;
	public int potionsBrewed = 0;
	public int auctionsWon = 0;
	public int highestBid = 0;
	public int itemsTier3 = 0;
	public int itemsTier4 = 0;
	public int soulsSacrificed = 0;
	public int listingsSold = 0;
	public int listingsClaimed = 0;
	public int itemsShredded = 0;
	public int timesFastTraveled = 0;

	//	Misc
	public int highestStreak = 0;
	public double healthRegained = 0;
	public double absorptionGained = 0;
	public int bountiesClaimed = 0;

	public int jewelsCompleted = 0;
	public int itemsGemmed = 0;
	public int livesLost = 0;
	public int itemsBroken = 0;
	public int feathersLost = 0;

	public int chatMessages = 0;

	public PlayerStats() {
	}

	@Deprecated
	public PlayerStats(PitPlayer pitPlayer, FileConfiguration playerData) {
		this.pitPlayer = pitPlayer;

		playerKills = playerData.getInt("stats.combat.player-kills");
		botKills = playerData.getInt("stats.combat.bot-kills");
		hopperKills = playerData.getInt("stats.combat.hopper-kills");
		swordHits = playerData.getInt("stats.combat.sword-hits");
		arrowShots = playerData.getInt("stats.combat.arrow-shots");
		arrowHits = playerData.getInt("stats.combat.arrow-hits");
		damageDealt = playerData.getDouble("stats.combat.damage-dealt");
		trueDamageDealt = playerData.getDouble("stats.combat.true-damage-dealt");

		deaths = playerData.getInt("stats.combat.deaths");
		damageTaken = playerData.getDouble("stats.combat.damage-taken");
		trueDamageTaken = playerData.getDouble("stats.combat.true-damage-taken");

		timesOnOverdrive = playerData.getInt("stats.megastreak.overdrive");
		timesOnBeastmode = playerData.getInt("stats.megastreak.beastmode");
		timesOnHighlander = playerData.getInt("stats.megastreak.highlander");
		timesOnMoon = playerData.getInt("stats.megastreak.moon");
		rngesusCompleted = playerData.getInt("stats.megastreak.rngesus");
		ubersCompleted = playerData.getInt("stats.megastreak.ubers-completed");

		billionaire = playerData.getInt("stats.enchant.billionaire");
		perun = playerData.getInt("stats.enchant.perun");
		executioner = playerData.getInt("stats.enchant.executioner");
		gamble = playerData.getDouble("stats.enchant.gamble");
		stun = playerData.getDouble("stats.enchant.stun");
		lifesteal = playerData.getDouble("stats.enchant.lifesteal");

		robinhood = playerData.getInt("stats.enchant.robinhood");
		volley = playerData.getInt("stats.enchant.volley");
		telebow = playerData.getInt("stats.enchant.telebow");
		pullbow = playerData.getInt("stats.enchant.pullbow");
		explosive = playerData.getInt("stats.enchant.explosive");
		lucky = playerData.getInt("stats.enchant.lucky");
		drain = playerData.getInt("stats.enchant.drain");
		wasp = playerData.getInt("stats.enchant.wasp");
		pin = playerData.getInt("stats.enchant.pin");
		ftts = playerData.getInt("stats.enchant.ftts");
		pcts = playerData.getInt("stats.enchant.pcts");

		rgm = playerData.getInt("stats.enchant.rgm");
		regularity = playerData.getInt("stats.enchant.regularity");

		minutesPlayed = playerData.getInt("stats.progression.minutes-played");
		totalGold = playerData.getDouble("stats.progression.total-gold");

		highestStreak = playerData.getInt("stats.misc.highest-streak");
		healthRegained = playerData.getDouble("stats.misc.health-regained");
		absorptionGained = playerData.getDouble("stats.misc.absorption-gained");
		bountiesClaimed = playerData.getInt("stats.misc.bounties-claimed");

		jewelsCompleted = playerData.getInt("stats.misc.jewels-completed");
		itemsGemmed = playerData.getInt("stats.misc.items-gemmed");
		livesLost = playerData.getInt("stats.misc.lives-lost");
		itemsBroken = playerData.getInt("stats.misc.items-broken");
		feathersLost = playerData.getInt("stats.misc.feathers-lost");

		bossesKilled = playerData.getInt("stats.darkzone.bosses-killed");
		mobsKilled = playerData.getInt("stats.darkzone.mobs-killed");
		lifetimeSouls = playerData.getInt("stats.darkzone.lifetime-souls");
		potionsBrewed = playerData.getInt("stats.darkzone.potions-brewed");
		auctionsWon = playerData.getInt("stats.darkzone.auctions-won");
		highestBid = playerData.getInt("stats.darkzone.highest-bid");

		chatMessages = playerData.getInt("stats.misc.chat-messages");
	}

	public void init(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
		this.uuid = pitPlayer.player.getUniqueId();
	}
}
