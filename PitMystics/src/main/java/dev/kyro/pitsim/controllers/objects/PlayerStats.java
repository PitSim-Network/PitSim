package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.pitsim.controllers.PrestigeValues;

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
	public int timesOnBeastmode = 0;
	public int timesOnHighlander = 0;
	public int timesOnMoon = 0;
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

	public PlayerStats init(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
		this.uuid = pitPlayer.player.getUniqueId();

		return this;
	}
}
