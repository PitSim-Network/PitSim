package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.enchants.Hearts;
import dev.kyro.pitsim.enums.AChatColor;
import dev.kyro.pitsim.enums.DeathCry;
import dev.kyro.pitsim.enums.KillEffect;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.inventories.ChatColorPanel;
import dev.kyro.pitsim.killstreaks.*;
import dev.kyro.pitsim.perks.NoPerk;
import dev.kyro.pitsim.perks.Thick;
import dev.kyro.pitsim.pitevents.Juggernaut;
import me.clip.placeholderapi.PlaceholderAPI;
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

	public int playerLevel = 1;
	public int remainingXP = 21;
	public int playerKills = 0;
	public PitPerk[] pitPerks = new PitPerk[4];
	public int renown;

	private double kills = 0;
	public int bounty = 0;
	public List<Killstreak> killstreaks = new ArrayList<>();
	public int latestKillAnnouncement = 0;

	public Megastreak megastreak;
	public long uberReset = 0;
	public int dailyUbersLeft = 5;

	public Map<PitEnchant, Integer> enchantHits = new HashMap<>();
	public Map<PitEnchant, Integer> enchantCharge = new HashMap<>();

	public Map<UUID, Double> recentDamageMap = new HashMap<>();
	public List<BukkitTask> assistRemove = new ArrayList<>();

	public KillEffect killEffect = null;
	public DeathCry deathCry = null;
	public AChatColor chatColor = null;

	public Boolean disabledPlayerChat = false;
	public Boolean disabledKillFeed = false;
	public Boolean disabledBounties = false;
	public Boolean disabledStreaks = false;

	public UUID lastHitUUID = null;

	public PitPlayer(Player player) {
		this.player = player;
		this.megastreak = new NoMegastreak(this);

		Non non = NonManager.getNon(player);

		if(non == null) {
			String message = "%luckperms_prefix%";
			prefix = "&7[&e" + playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(player, message);

			FileConfiguration playerData = APlayerData.getPlayerData(player);

			if(playerData.getInt("level") > 0) {
				playerLevel = playerData.getInt("level");
				remainingXP = playerData.getInt("xp");
				playerKills = playerData.getInt("playerkills");
				LevelManager.setXPBar(player, this);
			}

			for(int i = 0; i < pitPerks.length; i++) {

				String perkString = playerData.getString("perk-" + i);
				PitPerk savedPerk = perkString != null ? PitPerk.getPitPerk(perkString) : NoPerk.INSTANCE;

				pitPerks[i] = savedPerk != null ? savedPerk : NoPerk.INSTANCE;
			}

			String deathCryString = playerData.getString("deathcry");
			if(deathCryString != null) deathCry = DeathCry.valueOf(deathCryString);

			String killEffectString = playerData.getString("killeffect");
			if(killEffectString != null) killEffect = KillEffect.valueOf(killEffectString);

			String chatColorString = playerData.getString("chatcolor");
			if(chatColorString != null) {
				chatColor = AChatColor.valueOf(chatColorString);
				ChatColorPanel.playerChatColors.put(player, chatColor);
			}

			if(playerData.contains("renown")) renown = playerData.getInt("renown");

			disabledBounties = playerData.getBoolean("disabledbounties");
			disabledStreaks = playerData.getBoolean("disabledstreaks");
			disabledKillFeed = playerData.getBoolean("disabledkillfeed");
			disabledPlayerChat = playerData.getBoolean("disabledplayerchat");
			uberReset = playerData.getLong("ubercooldown");
			dailyUbersLeft = playerData.getInt("ubersleft");

				String streak = playerData.getString("megastreak");

			if(streak == "Beastmode") this.megastreak = new Beastmode(this);
			if(streak == "No Megastreak") this.megastreak = new NoMegastreak(this);
			if(streak == "Highlander") this.megastreak = new Highlander(this);
			if(streak == "Overdrive") this.megastreak = new Overdrive(this);
			if(streak == "Uberstreak") this.megastreak = new Uberstreak(this);




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
		if(!PitEventManager.majorEvent) megastreak.reset();
		killstreaks.forEach(Killstreak::reset);
		kills = 0;
		latestKillAnnouncement = 0;
	}

	public void incrementKills() {

		kills++;
		if(kills >= megastreak.getRequiredKills() && kills < megastreak.getRequiredKills() + 1 && megastreak.getClass() != NoMegastreak.class) megastreak.proc();
		for(Killstreak killstreak : killstreaks) {
			if(kills == 0 || kills % killstreak.killInterval != 0) continue;
			killstreak.proc();
		}
		megastreak.kill();

		if(Math.floor(kills) % 25 == 0) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
				if(pitPlayer.disabledStreaks) continue;
				String message = ChatColor.translateAlternateColorCodes(
						'&', "&c&lSTREAK!&7 of &c" + (int) Math.floor(kills) + " &7by %luckperms_prefix%" + player.getDisplayName());
				onlinePlayer.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
			}
		}
	}

	public void incrementAssist(double assistPercent) {

		kills =  kills + (Math.round(assistPercent * 100) / 100D);

		if(kills >= megastreak.getRequiredKills() && kills < megastreak.getRequiredKills() + 1 && megastreak.getClass() != NoMegastreak.class &
				!megastreak.isOnMega()) megastreak.proc();
		for(Killstreak killstreak : killstreaks) {
			if(kills == 0 || kills % killstreak.killInterval != 0) continue;
			killstreak.proc();
		}
		if(Math.floor(kills) % 25 == 0 && latestKillAnnouncement != Math.floor(kills)) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
				if(pitPlayer.disabledStreaks) continue;
				String message = ChatColor.translateAlternateColorCodes(
						'&', "&c&lSTREAK!&7 of &c" + (int) Math.floor(kills) + " &7by %luckperms_prefix%" + player.getDisplayName());
				onlinePlayer.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
			}
			latestKillAnnouncement = (int) Math.floor(kills);
		}
	}

	public double getKills() {
		return kills;
	}

	public void setKillsreak(double newKills) {
		kills = newKills;
	}

	public void setKills(double kills) {

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

		BukkitTask bukkitTask = new BukkitRunnable() {
			@Override
			public void run() {
				for(BukkitTask pendingTask : Bukkit.getScheduler().getPendingTasks()) {
					if(pendingTask.getTaskId() != getTaskId()) continue;
					assistRemove.remove(pendingTask);
					break;
				}
				recentDamageMap.putIfAbsent(player.getUniqueId(), 0D);
				if(recentDamageMap.get(player.getUniqueId()) - damage != 0)
					recentDamageMap.put(player.getUniqueId(), recentDamageMap.get(player.getUniqueId()) - damage); else recentDamageMap.remove(player.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 200L);
		assistRemove.add(bukkitTask);
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
		if(Hearts.INSTANCE != null) maxHealth += Hearts.INSTANCE.getExtraHealth(this);

		if(megastreak.isOnMega() && megastreak.getClass() == Uberstreak.class && kills >= 100) {
			maxHealth -= 2;
		}
		if(megastreak.isOnMega() && megastreak.getClass() == Uberstreak.class && kills >= 200) {
			maxHealth -= 2;
		}
		if(megastreak.isOnMega() && megastreak.getClass() == Uberstreak.class && kills >= 300) {
			maxHealth -= 2;
		}

		if(Juggernaut.juggernaut == this.player) maxHealth = 100;
		if(player.getMaxHealth() == maxHealth) return;
		player.setMaxHealth(maxHealth);
	}
}
