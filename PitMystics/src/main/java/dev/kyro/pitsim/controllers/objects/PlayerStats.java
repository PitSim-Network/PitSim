package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.controllers.PrestigeValues;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class PlayerStats {
	public PitPlayer pitPlayer;
	public UUID uuid;
	public FileConfiguration playerData;

//	Offense
	public int playerKills;
	public int botKills;
	public int hopperKills;
	public int swordHits;
	public int arrowShots;
	public int arrowHits;
	public double damageDealt;
	public double trueDamageDealt;
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
	public int getTotalXP() {
		return PrestigeValues.getTotalXP(pitPlayer.prestige, pitPlayer.level, pitPlayer.remainingXP);
	}
	public double totalGold;

	public double getXpPerHour() {
		if(getHoursPlayed() == 0) return 0;
		return (double) getTotalXP() / getHoursPlayed();
	}
	public double getGoldPerHour() {
		if(getHoursPlayed() == 0) return 0;
		return totalGold / getHoursPlayed();
	}
	public double getHoursPlayed() {
		return (double) minutesPlayed / 60;
	}

	//	Ratios
	public double getPlayerKillsToDeaths() {
		if(deaths == 0) return 0;
		return (double) playerKills / deaths;
	}
	public double getBotKillsToDeaths() {
		if(deaths == 0) return 0;
		return (double) botKills / deaths;
	}
	public double getDamageDealtToDamageTaken() {
		if(damageTaken == 0) return 0;
		return damageDealt / damageTaken;
	}

//	Events
	public int eventsParticipated;

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

	public PlayerStats(PitPlayer pitPlayer, FileConfiguration playerData) {
		this.pitPlayer = pitPlayer;
		this.uuid = pitPlayer.player.getUniqueId();
		this.playerData = playerData;

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

		eventsParticipated = playerData.getInt("stats.events.events-participated");

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

		chatMessages = playerData.getInt("stats.misc.chat-messages");
	}

	public void save() {
		playerData.set("stats.combat.player-kills", playerKills);
		playerData.set("stats.combat.bot-kills", botKills);
		playerData.set("stats.combat.hopper-kills", hopperKills);
		playerData.set("stats.combat.sword-hits", swordHits);
		playerData.set("stats.combat.arrow-shots", arrowShots);
		playerData.set("stats.combat.arrow-hits", arrowHits);
		playerData.set("stats.combat.damage-dealt", damageDealt);
		playerData.set("stats.combat.true-damage-dealt", trueDamageDealt);

		playerData.set("stats.combat.deaths", deaths);
		playerData.set("stats.combat.damage-taken", damageTaken);
		playerData.set("stats.combat.true-damage-taken", trueDamageTaken);

		playerData.set("stats.megastreak.overdrive", timesOnOverdrive);
		playerData.set("stats.megastreak.beastmode", timesOnBeastmode);
		playerData.set("stats.megastreak.highlander", timesOnHighlander);
		playerData.set("stats.megastreak.moon", timesOnMoon);
		playerData.set("stats.megastreak.ubers-completed", ubersCompleted);

		playerData.set("stats.enchant.billionaire", billionaire);
		playerData.set("stats.enchant.perun", perun);
		playerData.set("stats.enchant.executioner", executioner);
		playerData.set("stats.enchant.gamble", gamble);
		playerData.set("stats.enchant.stun", stun);
		playerData.set("stats.enchant.lifesteal", lifesteal);

		playerData.set("stats.enchant.robinhood", robinhood);
		playerData.set("stats.enchant.volley", volley);
		playerData.set("stats.enchant.telebow", telebow);
		playerData.set("stats.enchant.pullbow", pullbow);
		playerData.set("stats.enchant.explosive", explosive);
		playerData.set("stats.enchant.lucky", lucky);
		playerData.set("stats.enchant.drain", drain);
		playerData.set("stats.enchant.wasp", wasp);
		playerData.set("stats.enchant.pin", pin);
		playerData.set("stats.enchant.ftts", ftts);
		playerData.set("stats.enchant.pcts", pcts);

		playerData.set("stats.enchant.rgm", rgm);
		playerData.set("stats.enchant.regularity", regularity);

		playerData.set("stats.events.events-participated", eventsParticipated);

		playerData.set("stats.progression.minutes-played", minutesPlayed);
		playerData.set("stats.progression.total-gold", totalGold);

		playerData.set("stats.misc.highest-streak", highestStreak);
		playerData.set("stats.misc.health-regained", healthRegained);
		playerData.set("stats.misc.absorption-gained", absorptionGained);
		playerData.set("stats.misc.bounties-claimed", bountiesClaimed);

		playerData.set("stats.misc.jewels-completed", jewelsCompleted);
		playerData.set("stats.misc.items-gemmed", itemsGemmed);
		playerData.set("stats.misc.lives-lost", livesLost);
		playerData.set("stats.misc.items-broken", itemsBroken);
		playerData.set("stats.misc.feathers-lost", feathersLost);

		playerData.set("stats.misc.chat-messages", chatMessages);

		APlayerData.savePlayerData(uuid);
	}
}
