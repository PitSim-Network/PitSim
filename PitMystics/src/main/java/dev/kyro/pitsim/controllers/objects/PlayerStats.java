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

	public PlayerStats init(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
		this.uuid = pitPlayer.player.getUniqueId();

		return this;
	}
}
