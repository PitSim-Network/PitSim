package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.killstreaks.Killstreak;
import dev.kyro.pitsim.controllers.killstreaks.Megastreak;
import dev.kyro.pitsim.killstreaks.Overdrive;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PitPlayer {

	public static List<PitPlayer> pitPlayers = new ArrayList<>();

	public Player player;

	public int kills = 0;
	public List<Killstreak> killstreaks = new ArrayList<>();
	public Megastreak megastreak = new Overdrive();

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

	public void endKillstreak() {
		kills = 0;
		megastreak.reset();
	}

	public void incrementKillstreak() {

		kills++;
		if(kills == megastreak.requiredKills) megastreak.proc();
		for(Killstreak killstreak : killstreaks) {
			if(kills == 0 || kills % killstreak.killInterval != 0) continue;
			killstreak.proc();
		}
	}

	public void setKills(int kills) {

		kills = Math.max(kills, 0);
		endKillstreak();

		for(int i = 0; i < kills; i++) incrementKillstreak();
	}
}
