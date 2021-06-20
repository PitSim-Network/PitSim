package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.killstreaks.Uberstreak;
import dev.kyro.pitsim.perks.NoPerk;
import dev.kyro.pitsim.perks.Thick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PitPlayer {

	public static List<PitPlayer> pitPlayers = new ArrayList<>();

	public Player player;
	public String prefix;

	public PitPerk[] pitPerks = new PitPerk[4];

	private int kills = 0;
	public int bounty = 0;
	public List<Killstreak> killstreaks = new ArrayList<>();
	public Megastreak megastreak;

	public Map<PitEnchant, Integer> enchantHits = new HashMap<>();
	public Map<PitEnchant, Integer> enchantCharge = new HashMap<>();

	public Map<UUID, Double> recentDamageMap = new HashMap<>();
	public List<BukkitTask> assistRemove = new ArrayList<>();

	public PitPlayer(Player player) {
		this.player = player;
		this.megastreak = new Uberstreak(this);

		Non non = NonManager.getNon(player);
		if(non == null) {
			prefix = "&d[&b&l120&d]&r ";
		}

		for(int i = 0; i < pitPerks.length; i++) {

			FileConfiguration playerData = APlayerData.getPlayerData(player);
			String perkString = playerData.getString("perk-" + i);
			PitPerk savedPerk = perkString != null ? PitPerk.getPitPerk(perkString) : NoPerk.INSTANCE;

			pitPerks[i] = savedPerk != null ? savedPerk : NoPerk.INSTANCE;
		}
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
		megastreak.reset();
		killstreaks.forEach(Killstreak::reset);
		kills = 0;
	}

	public void incrementKills() {

		kills++;
		if(kills == megastreak.getRequiredKills()) megastreak.proc();
		for(Killstreak killstreak : killstreaks) {
			if(kills == 0 || kills % killstreak.killInterval != 0) continue;
			killstreak.proc();
		}
		megastreak.kill();

		if(kills % 10 == 0) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
				'&', "&c&lSTREAK!&7 of &c" + kills + " &7by " + player.getDisplayName()));
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {

		kills = Math.max(kills, 0);
		endKillstreak();

		for(int i = 0; i < kills; i++) incrementKills();
	}

	public Map<UUID, Double> getRecentDamageMap() {
		return recentDamageMap;
	}

	public void addDamage(Player player, double damage) {
		if(player == null) return;

		recentDamageMap.putIfAbsent(player.getUniqueId(), 0D);
		recentDamageMap.put(player.getUniqueId(), recentDamageMap.get(player.getUniqueId()) + damage);

		assistRemove.add(new BukkitRunnable() {
			@Override
			public void run() {
				recentDamageMap.putIfAbsent(player.getUniqueId(), 0D);
				if( recentDamageMap.get(player.getUniqueId()) - damage != 0)
					recentDamageMap.put(player.getUniqueId(), recentDamageMap.get(player.getUniqueId()) - damage); else recentDamageMap.remove(player.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 200L));
	}

	public void heal(double amount) {

		HealEvent healEvent = new HealEvent(player, amount);
		Bukkit.getServer().getPluginManager().callEvent(healEvent);
		player.setHealth(Math.min(player.getHealth() + healEvent.getFinalHeal(), player.getMaxHealth()));
	}

	public boolean hasPerk(PitPerk pitPerk) {

		for(PitPerk perk : pitPerks) if(perk == pitPerk) return true;
		return false;
	}

	public void updateMaxHealth() {

		int maxHealth = 24;
		if(hasPerk(Thick.INSTANCE)) maxHealth += 4;

		player.setMaxHealth(maxHealth);
	}
}
