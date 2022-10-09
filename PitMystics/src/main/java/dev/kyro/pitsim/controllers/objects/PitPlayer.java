package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.PassData;
import dev.kyro.pitsim.battlepass.quests.ReachKillstreakQuest;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.brewing.objects.BrewingSession;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.enchants.Hearts;
import dev.kyro.pitsim.enchants.tainted.MaxHealth;
import dev.kyro.pitsim.enchants.tainted.MaxMana;
import dev.kyro.pitsim.enums.AChatColor;
import dev.kyro.pitsim.enums.DeathCry;
import dev.kyro.pitsim.enums.KillEffect;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.IncrementKillsEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.inventories.ChatColorPanel;
import dev.kyro.pitsim.killstreaks.Monster;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.megastreaks.*;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.perks.NoPerk;
import dev.kyro.pitsim.perks.Thick;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PitPlayer {
	@Exclude
	public static List<PitPlayer> pitPlayers = new ArrayList<>();

	@Exclude
	public boolean isNPC;

	@Exclude
	public Player player;
	@Exclude
	public String prefix = "";

	@Exclude
	private int kills = 0;
	@Exclude
	public double assistAmount = 0;
	@Exclude
	public int bounty = 0;
	@Exclude
	public int latestKillAnnouncement = 0;

	@Exclude
	public Map<PitEnchant, Integer> enchantHits = new HashMap<>();
	@Exclude
	public Map<PitEnchant, Integer> enchantCharge = new HashMap<>();

	@Exclude
	public Map<UUID, Double> recentDamageMap = new HashMap<>();
	@Exclude
	public List<BukkitTask> assistRemove = new ArrayList<>();

	@Exclude
	public UUID lastHitUUID = null;
	@Exclude
	public ItemStack confirmedDrop = null;

	@Exclude
	public double mana = 0;
	@Exclude
	public int graceTiers = 0;

	@Exclude
	public UUID uuid;
	//	Savable
	public Date lastLogin = new Date(0);
	public int prestige = 0;
	public int level = 1;
	public long remainingXP = PrestigeValues.getXPForLevel(1);
	public int soulsGathered = 0;

	public int renown = 0;
	@Exclude
	public List<PitPerk> pitPerks = Arrays.asList(NoPerk.INSTANCE, NoPerk.INSTANCE, NoPerk.INSTANCE, NoPerk.INSTANCE);
	public List<String> pitPerksRef = Arrays.asList("none", "none", "none", "none");
	@Exclude
	public List<Killstreak> killstreaks = Arrays.asList(NoKillstreak.INSTANCE, NoKillstreak.INSTANCE, NoKillstreak.INSTANCE);
	public List<String> killstreaksRef = Arrays.asList("NoKillstreak", "NoKillstreak", "NoKillstreak");
	@Exclude
//	TODO: Save megastreak ref, not megastreak
	public Megastreak megastreak;
	public String megastreakRef = "nomegastreak";

	public Map<String, Integer> renownUpgrades = new HashMap<>();
	public boolean playerChatDisabled = false;
	public boolean killFeedDisabled = false;
	public boolean bountiesDisabled = false;
	public boolean streaksDisabled = false;
	public boolean lightingDisabled = false;
	public boolean musicDisabled = false;
	public boolean promptPack = false;

	public double goldStack = 0;
	public int moonBonus = 0;
	public int dailyUbersLeft = 5;
	public long uberReset = 0;
	public int goldGrinded = 0;
	public Map<String, Integer> boosters = new HashMap<>();
	public Map<String, Integer> boosterTime = new HashMap<>();

	public double lastVersion = PitSim.VERSION;
	public KillEffect killEffect;
	public DeathCry deathCry;
	public AChatColor chatColor = AChatColor.GRAY;

	public List<String> brewingSessions = Arrays.asList(null, null, null);
	public int taintedSouls = 200;

	public List<String> potionStrings = new ArrayList<>();

	public List<String> auctionReturn = new ArrayList<>();
	public int soulReturn = 0;

	public boolean tutorial = false;
	public boolean darkzoneCutscene = false;

	public PlayerStats stats = new PlayerStats();
	private PassData passData = new PassData();

	@Deprecated
	public PassData getPassData() {
		return passData;
	}

	public PassData getPassData(Date passDate) {
		if(!passDate.equals(passData.currentPassDate)) {
//			TODO: give unclaimed rewards
			passData = new PassData(passDate);
			save();
		}
		return passData;
	}

	public void setPassData(PassData passData) {
		this.passData = passData;
	}

	@Exclude
	public long lastSave;
	@Exclude
	public void save() {
		if(lastSave + 1_500L > System.currentTimeMillis()) return;
		lastSave = System.currentTimeMillis();

		if(isNPC) {
			System.out.println("complete development failure. " + player.getName() + " is attempting to save data and is not a real player");
			return;
		}

		megastreakRef = megastreak.getRefNames().get(0);

		for(int i = 0; i < pitPerks.size(); i++) {
			PitPerk pitPerk = pitPerks.get(i);
			pitPerksRef.set(i, pitPerk.refName);
		}

		for(int i = 0; i < killstreaks.size(); i++) {
			Killstreak killstreak = killstreaks.get(i);
			killstreaksRef.set(i, killstreak.refName);
		}

		FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION).document(uuid.toString()).set(this);
		System.out.println("Saving Data: " + uuid.toString());
	}

//	NPC Init
	public PitPlayer(Player player) {
		this.isNPC = true;

		this.uuid = player.getUniqueId();
		this.player = player;
		this.megastreak = new NoMegastreak(this);
	}

	public PitPlayer() {
		for(Booster booster : BoosterManager.boosterList) {
			boosters.put(booster.refName, 0);
			boosterTime.put(booster.refName, 0);
		}
	}

	@Deprecated
	public PitPlayer(UUID uuid) {
		this.uuid = uuid;
		this.megastreak = new NoMegastreak(this);

		prefix = "";
		APlayer aPlayer = APlayerData.getPlayerData(uuid);
		FileConfiguration playerData = aPlayer.playerData;

		prestige = playerData.getInt("prestige");
		level = playerData.contains("level") ? playerData.getInt("level") : 1;
		remainingXP = playerData.getInt("xp");
		soulsGathered = playerData.getInt("soulsgathered");
		renown = playerData.getInt("renown");
		for(int i = 0; i < pitPerks.size(); i++) {
			String perkString = playerData.getString("perk-" + i);
			PitPerk savedPerk = perkString != null ? PitPerk.getPitPerk(perkString) : NoPerk.INSTANCE;
			pitPerks.set(i, savedPerk != null ? savedPerk : NoPerk.INSTANCE);
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
		musicDisabled = playerData.getBoolean("settings.music");
		promptPack = playerData.getBoolean("promptPack");

		lastVersion = playerData.getDouble("lastversion");
		String killEffectString = playerData.getString("killeffect");
		if(killEffectString != null) killEffect = KillEffect.valueOf(killEffectString);
		String deathCryString = playerData.getString("deathcry");
		if(deathCryString != null) deathCry = DeathCry.valueOf(deathCryString);
		String chatColorString = playerData.getString("chatcolor");
		if(chatColorString != null) {
			chatColor = AChatColor.valueOf(chatColorString);
//			ChatColorPanel.playerChatColors.put(player, chatColor);
		}

		goldStack = playerData.getDouble("goldstack");
		moonBonus = playerData.getInt("moonbonus");
		dailyUbersLeft = playerData.contains("uberslef	t") ? playerData.getInt("ubersleft") : 5;
		uberReset = playerData.getLong("ubercooldown");
		goldGrinded = playerData.getInt("goldgrinded");
		for(Booster booster : BoosterManager.boosterList) {
			boosters.put(booster.refName, playerData.getInt("boosters." + booster.refName));
			boosterTime.put(booster.refName, playerData.getInt("booster-time." + booster.refName));
		}

		stats = new PlayerStats(this, playerData);
//		updateXPBar();

		for(int i = 0; i < brewingSessions.size(); i++) {
			brewingSessions.set(i, playerData.getString("brewingsession" + (i + 1)));
		}
		for(int i = 0; i < brewingSessions.size(); i++) {
			if(brewingSessions.get(i) != null) {
//				BrewingManager.brewingSessions.add(new BrewingSession(player, i, brewingSessions.get(i), null, null, null, null));
			}
		}

		if(playerData.contains("taintedsouls")) {
			taintedSouls = playerData.getInt("taintedsouls");
		} else taintedSouls = 200;


		if(chatColorString != null) {
			chatColor = AChatColor.valueOf(chatColorString);
//			ChatColorPanel.playerChatColors.put(player, chatColor);
		}


		for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
			int tier = 0;
			if(!upgrade.isTiered && playerData.contains(upgrade.refName)) tier = 1;
			else if(upgrade.isTiered && playerData.contains(upgrade.refName)) tier = playerData.getInt(upgrade.refName);
			renownUpgrades.put(upgrade.refName, tier);
		}


		if(playerData.contains("auctionreturn")) {
			auctionReturn = Arrays.asList(playerData.getString("auctionreturn").split(","));
		}

		if(playerData.contains("soulreturn")) {
			soulReturn = playerData.getInt("soulreturn");
		}

		if(playerData.contains("tutorial")) {
			tutorial = playerData.getBoolean("tutorial");
		}

		if(playerData.contains("darkzonepreview")) {
			darkzoneCutscene = playerData.getBoolean("darkzonepreview");
		}

	}

	public void init(Player player) {
		this.player = player;

		ChatColorPanel.playerChatColors.put(player, chatColor);

		if(megastreakRef.equals("nomegastreak")) this.megastreak = new NoMegastreak(this);
		else if(megastreakRef.equals("beastmode")) this.megastreak = new Beastmode(this);
		else if(megastreakRef.equals("highlander")) this.megastreak = new Highlander(this);
		else if(megastreakRef.equals("overdrive")) this.megastreak = new Overdrive(this);
		else if(megastreakRef.equals("uberstreak")) this.megastreak = new Uberstreak(this);
		else if(megastreakRef.equals("moon")) this.megastreak = new ToTheMoon(this);
		else if(megastreakRef.equals("rngesus")) this.megastreak = new RNGesus(this);

		for(int i = 0; i < pitPerks.size(); i++) {
			String perkString = pitPerksRef.get(i);
			PitPerk savedPerk = perkString != null ? PitPerk.getPitPerk(perkString) : NoPerk.INSTANCE;
			pitPerks.set(i, savedPerk);
		}

		for(int i = 0; i < killstreaks.size(); i++) {
			String killstreakString = killstreaksRef.get(i);
			Killstreak savedKillstreak = killstreakString != null ? Killstreak.getKillstreak(killstreakString) : NoKillstreak.INSTANCE;
			killstreaks.set(i, savedKillstreak);
		}

		for(int i = 0; i < brewingSessions.size(); i++) {
			if(brewingSessions.get(i) != null)
				BrewingManager.brewingSessions.add(new BrewingSession(player, i, brewingSessions.get(i), null, null, null, null));
		}

		if(renownUpgrades == null) {
			renownUpgrades = new HashMap<>();

			for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
				renownUpgrades.put(upgrade.refName, 0);
			}
		}

		stats.init(this);
		updateXPBar();
	}

	public static boolean loadPitPlayer(UUID playerUUID) {
		for(PitPlayer testPitPlayer : pitPlayers) {
			if(testPitPlayer.player == null) continue;
			if(!testPitPlayer.player.getUniqueId().equals(playerUUID)) continue;
			System.out.println("found duplicate pitplayer for " + testPitPlayer.player.getName());
			return false;
		}

		PitPlayer pitPlayer;
		try {
			pitPlayer = FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION)
					.document(playerUUID.toString()).get().get().toObject(PitPlayer.class);
			System.out.println("Loaded Data: " + Bukkit.getOfflinePlayer(playerUUID).getName());
			assert pitPlayer != null;
			pitPlayer.uuid = playerUUID;

		} catch(Exception exception) {
			System.out.println("--------------------------------------------------");
			System.out.println("Playerdata for " + Bukkit.getOfflinePlayer(playerUUID).getName() + " failed to load");
			System.out.println("Disconnecting player");
			System.out.println();
			exception.printStackTrace();
			System.out.println("--------------------------------------------------");
			return false;
		}

		pitPlayers.add(pitPlayer);
		return true;
	}

	@Exclude
	public static PitPlayer getPitPlayer(Player player) {
		if(player == null) return null;

		PitPlayer pitPlayer = null;
		for(PitPlayer testPitPlayer : pitPlayers) {
			if(!testPitPlayer.uuid.equals(player.getUniqueId())) continue;
			pitPlayer = testPitPlayer;
			if(pitPlayer.player == null) pitPlayer.init(player);
			break;
		}
		if(pitPlayer == null) {

			boolean isNPC = !PlayerManager.isRealPlayer(player);
			if(isNPC) {
				pitPlayer = new PitPlayer(player);
			} else {
				System.out.println("pitplayer is null and shouldn't be");
				return null;
			}

			pitPlayers.add(pitPlayer);
		}

		return pitPlayer;
	}

	@Exclude
	public static PitPlayer getPitPlayer(LivingEntity checkPlayer) {
		if(!(checkPlayer instanceof Player)) return null;
		Player player = (Player) checkPlayer;
		return getPitPlayer(player);
	}

	@Exclude
	public void endKillstreak() {
		ReachKillstreakQuest.INSTANCE.endStreak(this, kills);
		megastreak.reset();
		for(Killstreak killstreak : killstreaks) {
			killstreak.reset(player);
		}
		kills = 0;
		latestKillAnnouncement = 0;
	}

	@Exclude
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

	@Exclude
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

	@Exclude
	public int getKills() {
		return kills;
	}

	@Exclude
	public void setKills(double kills) {

		kills = Math.max(kills, 0);
		endKillstreak();

		for(int i = 0; i < kills; i++) incrementKills();
	}

	@Exclude
	public Map<UUID, Double> getRecentDamageMap() {
		return recentDamageMap;
	}

	@Exclude
	public void addDamage(LivingEntity entity, double damage) {
		if(entity == null) return;

		recentDamageMap.putIfAbsent(entity.getUniqueId(), 0D);
		recentDamageMap.put(entity.getUniqueId(), recentDamageMap.get(entity.getUniqueId()) + damage);

		BukkitTask bukkitTask = new BukkitRunnable() {
			@Override
			public void run() {
				for(BukkitTask pendingTask : Bukkit.getScheduler().getPendingTasks()) {
					if(pendingTask.getTaskId() != getTaskId()) continue;
					assistRemove.remove(pendingTask);
					break;
				}
				recentDamageMap.putIfAbsent(entity.getUniqueId(), 0D);
				if(recentDamageMap.get(entity.getUniqueId()) - damage != 0)
					recentDamageMap.put(entity.getUniqueId(), recentDamageMap.get(entity.getUniqueId()) - damage);
				else recentDamageMap.remove(entity.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 200L);
		assistRemove.add(bukkitTask);
	}

	@Exclude
	public HealEvent heal(double amount) {

		return heal(amount, HealEvent.HealType.HEALTH, -1);
	}

	@Exclude
	public HealEvent heal(double amount, HealEvent.HealType healType, int max) {
		return Misc.heal(player, amount, healType, max);
	}

	@Exclude
	public boolean hasPerk(PitPerk pitPerk) {

		for(PitPerk perk : pitPerks) if(perk == pitPerk) return true;
		return false;
	}

	@Exclude
	public void updateMaxHealth() {

		int maxHealth = 24;
		if(hasPerk(Thick.INSTANCE)) maxHealth += 4;

		if(MapManager.inDarkzone(player)) maxHealth += 20;

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
		if(Hearts.INSTANCE != null) maxHealth += Hearts.INSTANCE.getExtraHealth(enchantMap);
		if(MaxHealth.INSTANCE != null) maxHealth += MaxHealth.INSTANCE.
				getExtraHealth(player, enchantMap);

		if(megastreak.getClass() == Uberstreak.class) {
			Uberstreak uberstreak = (Uberstreak) megastreak;
			if(uberstreak.uberEffects.contains(Uberstreak.UberEffect.LOSE_MAX_HEALTH)) maxHealth -= 4;
		}

		if(Killstreak.hasKillstreak(player, "Monster") && Monster.healthMap.containsKey(player)) {
			maxHealth += Monster.healthMap.get(player);
		}

		maxHealth -= (8 * graceTiers);

		if(maxHealth <= 0) {
			if(!CombatManager.taggedPlayers.containsKey(player.getUniqueId())) {
				DamageManager.death(player);
				return;
			}

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			UUID attackerUUID = pitPlayer.lastHitUUID;
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(onlinePlayer.getUniqueId().equals(attackerUUID)) {

					Map<PitEnchant, Integer> attackerEnchant = new HashMap<>();
					Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
					EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(onlinePlayer, player, EntityDamageEvent.DamageCause.CUSTOM, 0);
					AttackEvent attackEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);

					DamageManager.kill(attackEvent, onlinePlayer, player, false, KillType.DEATH);
					return;
				}
			}
			DamageManager.death(player);
			OofEvent oofEvent = new OofEvent(player);
			Bukkit.getPluginManager().callEvent(oofEvent);
			return;
		}
		if(player.getMaxHealth() == maxHealth) return;
		player.setMaxHealth(maxHealth);
	}

	@Exclude
	public boolean useMana(int amount) {
		if(amount > mana) return false;
		mana -= amount;
		return true;
	}

	@Exclude
	public int getMaxMana() {
		int maxMana = 100;

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
		if(MaxMana.INSTANCE != null) maxMana += MaxMana.INSTANCE.getExtraMana(enchantMap);

		return maxMana;
	}

	@Exclude
	public void damage(double damage, LivingEntity damager) {
		if(player.getHealth() - damage <= 0) {
			if(damager == null) {
				DamageManager.death(player);
				AOutput.send(player, "&c&lDEATH!");
			} else {
				EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(damager, player, EntityDamageEvent.DamageCause.CUSTOM, damage);
				AttackEvent attackEvent = new AttackEvent(ev, EnchantManager.getEnchantsOnPlayer(damager), EnchantManager.getEnchantsOnPlayer(player), false);

				DamageManager.kill(attackEvent, damager, player, false, KillType.DEFAULT);
			}
		} else player.damage(damage);
	}

	@Exclude
	public void updateXPBar() {
		if(MapManager.inDarkzone(player)) {
			player.setLevel((int) Math.ceil(mana));
			if(mana >= getMaxMana() - 1) player.setLevel(getMaxMana());
			player.setExp((float) (mana / getMaxMana()));
		} else {
			player.setLevel(level);
			float remaining = remainingXP;
			PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(prestige);
			float total = (float) (PrestigeValues.getXPForLevel(level) * prestigeInfo.xpMultiplier);

			player.setLevel(level);
			float xp = (total - remaining) / total;

			player.setExp(xp);
		}
	}
}
