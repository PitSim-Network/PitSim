package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.enchants.Hearts;
import dev.kyro.pitsim.enums.AChatColor;
import dev.kyro.pitsim.enums.DeathCry;
import dev.kyro.pitsim.enums.KillEffect;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.IncrementKillsEvent;
import dev.kyro.pitsim.inventories.ChatColorPanel;
import dev.kyro.pitsim.killstreaks.Monster;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.megastreaks.*;
import dev.kyro.pitsim.perks.NoPerk;
import dev.kyro.pitsim.perks.Thick;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PitPlayer {

	public static List<PitPlayer> pitPlayers = new ArrayList<>();

	public Player player;
	public String prefix;

	private int kills = 0;
	public double assistAmount = 0;
	public int bounty = 0;
	public int latestKillAnnouncement = 0;

	public Map<PitEnchant, Integer> enchantHits = new HashMap<>();
	public Map<PitEnchant, Integer> enchantCharge = new HashMap<>();

	public Map<UUID, Double> recentDamageMap = new HashMap<>();
	public List<BukkitTask> assistRemove = new ArrayList<>();

	public UUID lastHitUUID = null;
	public ItemStack confirmedDrop = null;

	//	Savable
	public int prestige;
	public int level;
	public int remainingXP = PrestigeValues.getXPForLevel(1);
	public int playerKills;
	public int renown;
	public PitPerk[] pitPerks = new PitPerk[4];
	public List<Killstreak> killstreaks = Arrays.asList(NoKillstreak.INSTANCE, NoKillstreak.INSTANCE, NoKillstreak.INSTANCE);
	public Megastreak megastreak;

	public boolean playerChatDisabled;
	public boolean killFeedDisabled;
	public boolean bountiesDisabled;
	public boolean streaksDisabled;
	public boolean lightingDisabled;
	public boolean promptPack;

	public double goldStack;
	public int moonBonus;
	public int dailyUbersLeft;
	public long uberReset;
	public int goldGrinded;
	public Map<Booster, Integer> boosters = new HashMap<>();
	public Map<Booster, Integer> boosterTime = new HashMap<>();

	public double lastVersion;
	public KillEffect killEffect;
	public DeathCry deathCry;
	public AChatColor chatColor;

	public PlayerStats stats;

	public void save() {
		APlayer aPlayer = APlayerData.getPlayerData(player);
		FileConfiguration playerData = aPlayer.playerData;

		playerData.set("prestige", prestige);
		playerData.set("level", level);
		playerData.set("xp", remainingXP);
		playerData.set("playerkills", playerKills);
		playerData.set("renown", renown);

		playerData.set("goldstack", goldStack);
		playerData.set("moonbonus", moonBonus);
		playerData.set("ubersleft", dailyUbersLeft);
		playerData.set("ubercooldown", uberReset);
		playerData.set("goldgrinded", goldGrinded);
		for(Map.Entry<Booster, Integer> entry : boosters.entrySet())
			playerData.set("boosters." + entry.getKey().refName, entry.getValue());

		playerData.set("lastversion", PitSim.version);

		aPlayer.save();
	}

	public void fullSave() {
		APlayer aPlayer = APlayerData.getPlayerData(player);
		FileConfiguration playerData = aPlayer.playerData;

		playerData.set("prestige", prestige);
		playerData.set("level", level);
		playerData.set("xp", remainingXP);
		playerData.set("playerkills", playerKills);
		playerData.set("renown", renown);
		for(int i = 0; i < pitPerks.length; i++) playerData.set("perk-" + i, pitPerks[i].refName);
		for(int i = 0; i < killstreaks.size(); i++) playerData.set("killstreak-" + i, killstreaks.get(i).refName);
		playerData.set("megastreak", megastreak.getRawName());

		playerData.set("disabledplayerchat", playerChatDisabled);
		playerData.set("disabledkillfeed", killFeedDisabled);
		playerData.set("disabledbounties", bountiesDisabled);
		playerData.set("disabledstreaks", streaksDisabled);
		playerData.set("settings.lightning", lightingDisabled);
		playerData.set("promptPack", promptPack);

		playerData.set("goldstack", goldStack);
		playerData.set("moonbonus", moonBonus);
		playerData.set("ubersleft", dailyUbersLeft);
		playerData.set("ubercooldown", uberReset);
		playerData.set("goldgrinded", goldGrinded);
		for(Map.Entry<Booster, Integer> entry : boosters.entrySet())
			playerData.set("boosters." + entry.getKey().refName, entry.getValue());
		for(Map.Entry<Booster, Integer> entry : boosterTime.entrySet())
			playerData.set("booster-time." + entry.getKey().refName, entry.getValue());

		playerData.set("lastversion", PitSim.version);
		if(killEffect != null) playerData.set("killeffect", killEffect.toString());
		if(deathCry != null) playerData.set("deathcry", deathCry.toString());
		if(chatColor != null) playerData.set("chatcolor", chatColor.toString());

		aPlayer.save();
		stats.save();
	}

	public PitPlayer(Player player) {
		this.player = player;
		this.megastreak = new NoMegastreak(this);

		Non non = NonManager.getNon(player);

		if(non == null) {
			prefix = "";
			APlayer aPlayer = APlayerData.getPlayerData(player);
			FileConfiguration playerData = aPlayer.playerData;

			prestige = playerData.getInt("prestige");
			level = playerData.contains("level") ? playerData.getInt("level") : 1;
			remainingXP = playerData.getInt("xp");
			playerKills = playerData.getInt("playerkills");
			renown = playerData.getInt("renown");
			for(int i = 0; i < pitPerks.length; i++) {
				String perkString = playerData.getString("perk-" + i);
				PitPerk savedPerk = perkString != null ? PitPerk.getPitPerk(perkString) : NoPerk.INSTANCE;
				pitPerks[i] = savedPerk != null ? savedPerk : NoPerk.INSTANCE;
			}
			for(int i = 0; i < killstreaks.size(); i++) {
				String killstreakString = playerData.getString("killstreak-" + i);
				Killstreak savedKillstreak = killstreakString != null ? Killstreak.getKillstreak(killstreakString) : NoKillstreak.INSTANCE;
				if(savedKillstreak == null) killstreaks.set(i, NoKillstreak.INSTANCE);
				else killstreaks.set(i, savedKillstreak);
			}
			String streak = playerData.getString("megastreak");
			if(Objects.equals(streak, "Beastmode")) this.megastreak = new Beastmode(this);
			if(Objects.equals(streak, "No Megastreak")) this.megastreak = new NoMegastreak(this);
			if(Objects.equals(streak, "Highlander")) this.megastreak = new Highlander(this);
			if(Objects.equals(streak, "Overdrive")) this.megastreak = new Overdrive(this);
			if(Objects.equals(streak, "Uberstreak")) this.megastreak = new Uberstreak(this);
			if(Objects.equals(streak, "To the Moon")) this.megastreak = new ToTheMoon(this);
			if(Objects.equals(streak, "RNGesus")) this.megastreak = new RNGesus(this);

			playerChatDisabled = playerData.getBoolean("disabledplayerchat");
			killFeedDisabled = playerData.getBoolean("disabledkillfeed");
			bountiesDisabled = playerData.getBoolean("disabledbounties");
			streaksDisabled = playerData.getBoolean("disabledstreaks");
			lightingDisabled = playerData.getBoolean("settings.lightning");
			promptPack = playerData.getBoolean("promptPack");

			lastVersion = playerData.getDouble("lastversion");
			String killEffectString = playerData.getString("killeffect");
			if(killEffectString != null) killEffect = KillEffect.valueOf(killEffectString);
			String deathCryString = playerData.getString("deathcry");
			if(deathCryString != null) deathCry = DeathCry.valueOf(deathCryString);
			String chatColorString = playerData.getString("chatcolor");
			if(chatColorString != null) {
				chatColor = AChatColor.valueOf(chatColorString);
				ChatColorPanel.playerChatColors.put(player, chatColor);
			}

			goldStack = playerData.getDouble("goldstack");
			moonBonus = playerData.getInt("moonbonus");
			dailyUbersLeft = playerData.contains("ubersleft") ? playerData.getInt("ubersleft") : 5;
			uberReset = playerData.getLong("ubercooldown");
			goldGrinded = playerData.getInt("goldgrinded");
			for(Booster booster : BoosterManager.boosterList) {
				boosters.put(booster, playerData.getInt("boosters." + booster.refName));
				boosterTime.put(booster, playerData.getInt("booster-time." + booster.refName));
			}

			stats = new PlayerStats(this, playerData);
			LevelManager.setXPBar(player, this);
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
		for(Killstreak killstreak : killstreaks) {
			killstreak.reset(player);
		}
		kills = 0;
		latestKillAnnouncement = 0;
	}

	public void incrementKills() {
		kills++;
		Bukkit.getPluginManager().callEvent(new IncrementKillsEvent(this.player, kills));

		for(Killstreak killstreak : killstreaks) {
			if(kills == 0 || kills % killstreak.killInterval != 0) continue;
			killstreak.proc(player);
		}

		if(Math.floor(kills) % 25 == 0) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
				if(pitPlayer.streaksDisabled) continue;
				String message = ChatColor.translateAlternateColorCodes(
						'&', "&c&lSTREAK!&7 of &c" + (int) Math.floor(kills) + " &7by %luckperms_prefix%" + player.getDisplayName());
				onlinePlayer.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
			}
		}
	}

	public void incrementAssist(double assistPercent) {
		assistAmount = assistAmount + assistPercent;
		if(assistAmount >= 1) {
			assistAmount = 0;
			kills++;
		}

		Bukkit.getPluginManager().callEvent(new IncrementKillsEvent(this.player, kills));
		if(kills >= megastreak.getRequiredKills() && megastreak.getClass() != NoMegastreak.class &
				!megastreak.isOnMega()) megastreak.proc();
		for(Killstreak killstreak : killstreaks) {
			if(kills == 0 || kills % killstreak.killInterval != 0) continue;
			killstreak.proc(player);
		}
		if(Math.floor(kills) % 25 == 0 && latestKillAnnouncement != Math.floor(kills)) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
				if(pitPlayer.streaksDisabled) continue;
				String message = ChatColor.translateAlternateColorCodes(
						'&', "&c&lSTREAK!&7 of &c" + (int) Math.floor(kills) + " &7by %luckperms_prefix%" + player.getDisplayName());
				onlinePlayer.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
			}
			latestKillAnnouncement = (int) Math.floor(kills);
		}
	}

	public int getKills() {
		return kills;
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
					recentDamageMap.put(player.getUniqueId(), recentDamageMap.get(player.getUniqueId()) - damage);
				else recentDamageMap.remove(player.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 200L);
		assistRemove.add(bukkitTask);
	}

	public HealEvent heal(double amount) {

		return heal(amount, HealEvent.HealType.HEALTH, -1);
	}

	public HealEvent heal(double amount, HealEvent.HealType healType, int max) {
		if(max == -1) max = Integer.MAX_VALUE;

		HealEvent healEvent = new HealEvent(player, amount, healType, max);
		Bukkit.getServer().getPluginManager().callEvent(healEvent);

		if(healType == HealEvent.HealType.HEALTH) {
			player.setHealth(Math.min(player.getHealth() + healEvent.getFinalHeal(), player.getMaxHealth()));
		} else {
			EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
			if(nmsPlayer.getAbsorptionHearts() < healEvent.max)
				nmsPlayer.setAbsorptionHearts(Math.min((float) (nmsPlayer.getAbsorptionHearts() + healEvent.getFinalHeal()), max));
		}
		return healEvent;
	}

	public boolean hasPerk(PitPerk pitPerk) {

		for(PitPerk perk : pitPerks) if(perk == pitPerk) return true;
		return false;
	}

	public void updateMaxHealth() {

		int maxHealth = 24;
		if(hasPerk(Thick.INSTANCE)) maxHealth += 4;
		if(Hearts.INSTANCE != null) maxHealth += Hearts.INSTANCE.getExtraHealth(this);

		if(megastreak.getClass() == Uberstreak.class) {
			Uberstreak uberstreak = (Uberstreak) megastreak;
			if(uberstreak.uberEffects.contains(Uberstreak.UberEffect.LOSE_MAX_HEALTH)) maxHealth -= 4;
		}

		if(Killstreak.hasKillstreak(player, "Monster") && Monster.healthMap.containsKey(player)) {
			maxHealth += Monster.healthMap.get(player);
		}

		if(player.getMaxHealth() == maxHealth) return;
		player.setMaxHealth(maxHealth);
	}
}
