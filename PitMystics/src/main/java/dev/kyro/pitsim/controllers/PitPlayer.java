package dev.kyro.pitsim.controllers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PitPlayer {

	public static List<PitPlayer> pitPlayers = new ArrayList<>();

	public Player player;

	public int killstreak = 0;
	public Megastreak megastreak;

	public HashMap<PitEnchant, Integer> enchantHits = new HashMap<>();

	private PitPlayer(Player player) {
		this.player = player;
	}

	public static PitPlayer getPitPlayer(Player player) {

		PitPlayer pitPlayer = null;
		for(PitPlayer testPitPlayer : pitPlayers) {

			if(testPitPlayer.player != player) continue;
			pitPlayer = testPitPlayer;
			break;
		}
		if(pitPlayer == null) {

			pitPlayer = new PitPlayer(player);
			pitPlayers.add(pitPlayer);
		}

		return pitPlayer;
	}

	public void incrementKillstreak() {

		killstreak++;
		if(killstreak == megastreak.requiredKills) megastreak.onMega();
	}

	public void setKillstreak(int killstreak) {

		this.killstreak = killstreak;
		if(killstreak >= megastreak.requiredKills) megastreak.onMega();
	}
}
