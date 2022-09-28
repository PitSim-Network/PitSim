package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.pitsim.controllers.PrestigeValues;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class PlayerStats {
	@Exclude
	public PitPlayer pitPlayer;
	@Exclude
	public UUID uuid;

	//	Offense
	public int playerKills;
	public int botKills;
	public int hopperKills;
	public int swordHits;
	public int arrowShots;
	public int arrowHits;
	public double damageDealt;
	public double trueDamageDealt;

	@Exclude
	public double getArrowAccuracy() {
		if(arrowShots == 0) return 0;
		return (double) arrowHits / arrowShots;
	}

	//	Defence
	public int deaths;
	public double damageTaken;
	public double trueDamageTaken;

	//	Megastreaks
	public int timesOnOverdrive;
	public int timesOnBeastmode;
	public int timesOnHighlander;
	public int timesOnMoon;
	public int rngesusCompleted;
	public int ubersCompleted;

	//	Mystics
	public int billionaire;
	public int perun;
	public int executioner;
	public double gamble;
	public double stun;
	public double lifesteal;

	public int robinhood;
	public int volley;
	public int telebow;
	public int pullbow;
	public int explosive;
	public int lucky;
	public int drain;
	public int wasp;
	public int pin;
	public int ftts;
	public int pcts;

	public int rgm;
	public int regularity;

	//	Progression
	public int minutesPlayed;

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
	public int bossesKilled;
	public int mobsKilled;
	public int lifetimeSouls;
	public int itemsEnchanted;
	public int potionsBrewed;
	public int auctionsWon;
	public int highestBid;

	//	Misc
	public int highestStreak;
	public double healthRegained;
	public double absorptionGained;
	public int bountiesClaimed;

	public int jewelsCompleted;
	public int itemsGemmed;
	public int livesLost;
	public int itemsBroken;
	public int feathersLost;

	public int chatMessages;

	public PlayerStats() {
	}

	public PlayerStats(PitPlayer pitPlayer, FileConfiguration playerData) {
		this.pitPlayer = pitPlayer;
		this.uuid = pitPlayer.player.getUniqueId();

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
		itemsEnchanted = playerData.getInt("stats.darkzone.items-enchanted");
		potionsBrewed = playerData.getInt("stats.darkzone.potions-brewed");
		auctionsWon = playerData.getInt("stats.darkzone.auctions-won");
		highestBid = playerData.getInt("stats.darkzone.highest-bid");

		chatMessages = playerData.getInt("stats.misc.chat-messages");
	}

	public PlayerStats init(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
		this.uuid = pitPlayer.player.getUniqueId();

		return this;
	}
}
