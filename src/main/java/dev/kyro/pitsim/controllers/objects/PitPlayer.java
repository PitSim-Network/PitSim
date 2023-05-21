package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.FastTravelDestination;
import dev.kyro.pitsim.adarkzone.FastTravelManager;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.adarkzone.notdarkzone.Shield;
import dev.kyro.pitsim.adarkzone.progression.DarkzoneData;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.ManaBranch;
import dev.kyro.pitsim.battlepass.PassData;
import dev.kyro.pitsim.battlepass.PassManager;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.battlepass.quests.ReachKillstreakQuest;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingSession;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.enchants.overworld.GottaGoFast;
import dev.kyro.pitsim.enchants.overworld.Hearts;
import dev.kyro.pitsim.enchants.tainted.chestplate.Sonic;
import dev.kyro.pitsim.enchants.tainted.uncommon.Tanky;
import dev.kyro.pitsim.enums.AChatColor;
import dev.kyro.pitsim.enums.DeathCry;
import dev.kyro.pitsim.enums.KillEffect;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.IncrementKillsEvent;
import dev.kyro.pitsim.inventories.ChatColorPanel;
import dev.kyro.pitsim.killstreaks.Limiter;
import dev.kyro.pitsim.killstreaks.Monster;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.megastreaks.*;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.perks.*;
import dev.kyro.pitsim.settings.scoreboard.ScoreboardData;
import dev.kyro.pitsim.storage.StorageManager;
import dev.kyro.pitsim.storage.StorageProfile;
import dev.kyro.pitsim.tutorial.DarkzoneTutorial;
import dev.kyro.pitsim.tutorial.OverworldTutorial;
import dev.kyro.pitsim.tutorial.TutorialData;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PitPlayer {
	@Exclude
	public static List<PitPlayer> pitPlayers = new ArrayList<>();

	@Exclude
	public static final long SAVE_COOLDOWN = 1_100;

	@Exclude
	public boolean isNPC;

	@Exclude
	public boolean isInitialized;

	@Exclude
	public Player player;

	@Exclude
	private int kills = 0;
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
	private double mana = 0;

	@Exclude
	public long lastCommand = 0;

	@Exclude
	public Shield shield = new Shield();

	@Exclude
	public UUID uuid;

	//	Savable
	public String nickname;
	public Date lastLogin = new Date(0);
	public int prestige = 0;
	public int level = 1;
	public long remainingXP = PrestigeValues.getXPForLevel(1);
	public String savedLeaderboardRef = "xp";

	public int renown = 0;
	@Exclude
	public List<PitPerk> pitPerks = Arrays.asList(Vampire.INSTANCE, StrengthChaining.INSTANCE, Dirty.INSTANCE, Dispersion.INSTANCE);
	public List<String> pitPerksRef = Arrays.asList("vampire", "strength", "dirty", "dispersion");
	@Exclude
	public List<Killstreak> killstreaks = Arrays.asList(Limiter.INSTANCE, NoKillstreak.INSTANCE, NoKillstreak.INSTANCE);
	public List<String> killstreaksRef = Arrays.asList("Limiter", "NoKillstreak", "NoKillstreak");
	@Exclude
	private Megastreak megastreak = Overdrive.INSTANCE;
	public String megastreakRef = "overdrive";

	public Map<String, Integer> renownUpgrades = new HashMap<>();
	public boolean playerChatDisabled = false;
	public boolean killFeedDisabled = false;
	public boolean bountiesDisabled = false;
	public boolean streaksDisabled = false;
	public boolean lightingDisabled = false;
	public boolean musicDisabled = false;
	public boolean promptPack = false;
	public List<String> uuidIgnoreList = new ArrayList<>();

	public double gold = 50_000;

	private Map<String, MegastreakLimit> megastreakCooldownMap = new HashMap<>();
	public double goldStack = 0;
	public int moonBonus = 0;
	public int apostleBonus = 0;
	public int goldGrinded = 0;
	public Map<String, Integer> boosters = new HashMap<>();

	public double lastVersion = PitSim.VERSION;
	public KillEffect killEffect;
	public DeathCry deathCry;
	public AChatColor chatColor = AChatColor.GRAY;

	public List<String> brewingSessions = Arrays.asList(null, null, null);

	public int taintedSouls = 0;

	public List<String> potionStrings = new ArrayList<>();

	public List<String> auctionReturn = new ArrayList<>();
	public int soulReturn = 0;

	public boolean darkzoneCutscene = false;

	public PlayerStats stats = new PlayerStats();

	public TutorialData overworldTutorialData = new TutorialData();
	public TutorialData darkzoneTutorialData = new TutorialData();

	@Exclude
	public OverworldTutorial overworldTutorial;
	@Exclude
	public DarkzoneTutorial darkzoneTutorial;

	public ScoreboardData scoreboardData = new ScoreboardData();
	private PassData passData = new PassData();
	public DarkzoneData darkzoneData = new DarkzoneData();

	public Map<String, UnlockedCosmeticData> unlockedCosmeticsMap = new HashMap<>();

	public static class UnlockedCosmeticData {
		public List<ParticleColor> unlockedColors;
	}

	public Map<String, EquippedCosmeticData> equippedCosmeticMap = new HashMap<>();

	public static class EquippedCosmeticData {
		public String refName;
		public ParticleColor particleColor;

		public EquippedCosmeticData() {
		}

		public EquippedCosmeticData(String refName, ParticleColor particleColor) {
			this.refName = refName;
			this.particleColor = particleColor;
		}
	}

	public PlayerSettings playerSettings = new PlayerSettings();

	public static class PlayerSettings {
		//		Particle Settings
		public boolean auraParticles = true;
		public boolean trailParticles = true;
	}

	@Deprecated
	public PassData getPassData() {
		return passData;
	}

	public PassData getPassData(Date passDate) {
		if(!passDate.equals(passData.currentPassDate)) {
//			TODO: give unclaimed rewards
			passData = new PassData(passDate);
			save(true, false);
		}

		long daysPassed = TimeUnit.DAYS.convert(new Date().getTime() - passDate.getTime(), TimeUnit.MILLISECONDS);
		if(daysPassed != passData.daysPassed) {
			passData.daysPassed = daysPassed;
			for(PassQuest dailyQuest : PassManager.getDailyQuests())
				passData.questCompletion.remove(dailyQuest.refName);
		}
		return passData;
	}

	public void setPassData(PassData passData) {
		this.passData = passData;
	}

	@Exclude
	public long lastSave;

	@Exclude
	public void save(boolean itemData, boolean finalSave) {
		try {
			save(finalSave, null, itemData);
		} catch(ExecutionException | InterruptedException ignored) {}
	}

	@Exclude
	public void save(boolean finalSave, BukkitRunnable callback, boolean itemData) throws ExecutionException, InterruptedException {
		if(StorageManager.frozenPlayers.contains(uuid)) {
			AOutput.log("Player " + player.getName() + " is frozen, not saving");
			if(finalSave) StorageManager.frozenPlayers.remove(uuid);
			return;
		}
		if(PitSim.getStatus() == PitSim.ServerStatus.STANDALONE) return;
		if(finalSave && lastSave + SAVE_COOLDOWN > System.currentTimeMillis()) {
			long timeUntilSave = lastSave + SAVE_COOLDOWN - System.currentTimeMillis();
			new Thread(() -> {
				try {
					Thread.sleep(timeUntilSave);
					save(true, callback, itemData);
				} catch(Exception exception) {
					AOutput.log("----------------------------------------");
					AOutput.log("CRITICAL ERROR: data for " + uuid + " failed to final save");
					AOutput.log("");
					exception.printStackTrace();
					AOutput.log("----------------------------------------");
					Misc.alertDiscord("CRITICAL ERROR: data for " + player.getName() + " failed to final save");
				}
			}).start();
			return;
		}

		if(lastSave + SAVE_COOLDOWN > System.currentTimeMillis()) return;
		lastSave = System.currentTimeMillis();

		if(isNPC) {
			AOutput.log("Complete development failure. " + uuid + " is attempting to save data and is not a real player");
			return;
		}

		if(itemData) {
			StorageProfile profile = StorageManager.getProfile(uuid);
			profile.saveData(finalSave);
		}

		if(isInitialized) {
			PotionManager.savePotions(this, finalSave);

			if(finalSave) {
				darkzoneTutorial.endTutorial();
				overworldTutorial.endTutorial();
			}

			for(int i = 0; i < pitPerks.size(); i++) {
				PitPerk pitPerk = pitPerks.get(i);
				pitPerksRef.set(i, pitPerk.refName);
			}

			for(int i = 0; i < killstreaks.size(); i++) {
				Killstreak killstreak = killstreaks.get(i);
				killstreaksRef.set(i, killstreak.refName);
			}

			megastreakRef = megastreak.getRefName();
		}

		if(finalSave && callback != null) {
			FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION).document(uuid.toString())
					.set(this).addListener(callback, command -> callback.runTask(PitSim.INSTANCE));
			AOutput.log("Saving Player (Blocking Thread): " + uuid.toString());
		} else {
			FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION).document(uuid.toString()).set(this);
			AOutput.log("Saving Player: " + Bukkit.getOfflinePlayer(uuid).getName());
		}
	}

	//	NPC Init
	public PitPlayer(Player player) {
		this.isNPC = true;

		this.uuid = player.getUniqueId();
		this.player = player;
	}

	public PitPlayer() {
		for(Booster booster : BoosterManager.boosterList) boosters.put(booster.refName, 0);
	}

	@Deprecated
	public PitPlayer(UUID uuid) {
		this.uuid = uuid;

		APlayer aPlayer = APlayerData.getPlayerData(uuid);
		FileConfiguration playerData = aPlayer.playerData;

		prestige = playerData.getInt("prestige");
		level = playerData.contains("level") ? playerData.getInt("level") : 1;
		remainingXP = playerData.getInt("xp");
		renown = playerData.getInt("renown");

		for(int i = 0; i < pitPerks.size(); i++) {
			String perkString = playerData.getString("perk-" + i);
			pitPerks.set(i, PerkManager.getPitPerk(perkString));
		}
		for(int i = 0; i < killstreaks.size(); i++) {
			String killstreakString = playerData.getString("killstreak-" + i);
			killstreaks.set(i, Killstreak.getKillstreak(killstreakString));
		}
		String megastreakString = playerData.getString("megastreak");
		megastreak = PerkManager.getMegastreak(megastreakString);

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
		goldGrinded = playerData.getInt("goldgrinded");
		for(Booster booster : BoosterManager.boosterList)
			boosters.put(booster.refName, playerData.getInt("boosters." + booster.refName));

		stats = new PlayerStats(this, playerData);
		overworldTutorial = new OverworldTutorial(playerData);

		for(int i = 0; i < brewingSessions.size(); i++) {
			brewingSessions.set(i, playerData.getString("brewingsession" + (i + 1)));
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
			if(!upgrade.isTiered() && playerData.contains(upgrade.refName)) tier = 1;
			else if(upgrade.isTiered() && playerData.contains(upgrade.refName)) tier = playerData.getInt(upgrade.refName);
			renownUpgrades.put(upgrade.refName, tier);
		}

		if(playerData.contains("auctionreturn"))
			auctionReturn = Arrays.asList(playerData.getString("auctionreturn").split(","));
		if(playerData.contains("soulreturn")) soulReturn = playerData.getInt("soulreturn");
		if(playerData.contains("darkzonepreview")) darkzoneCutscene = playerData.getBoolean("darkzonepreview");
	}

	public void init(Player player) {
		this.player = player;

		ChatColorPanel.playerChatColors.put(player, chatColor);

		overworldTutorial = new OverworldTutorial(this);
		darkzoneTutorial = new DarkzoneTutorial(this);

		for(int i = 0; i < pitPerks.size(); i++) {
			String perkString = pitPerksRef.get(i);
			PitPerk savedPerk = PerkManager.getPitPerk(perkString);
			pitPerks.set(i, savedPerk);
		}
		for(int i = 0; i < killstreaks.size(); i++) {
			String killstreakString = killstreaksRef.get(i);
			Killstreak savedKillstreak = Killstreak.getKillstreak(killstreakString);
			killstreaks.set(i, savedKillstreak);
		}
		megastreak = PerkManager.getMegastreak(megastreakRef);

		if(PitSim.getStatus().isDarkzone()) {
			for(int i = 0; i < brewingSessions.size(); i++) {
				if(brewingSessions.get(i) != null)
					BrewingManager.brewingSessions.add(new BrewingSession(player, i, brewingSessions.get(i), null, null, null, null));
			}
		}

		if(renownUpgrades == null) {
			renownUpgrades = new HashMap<>();

			for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
				renownUpgrades.put(upgrade.refName, 0);
			}
		}

		this.stats.init(this);
		this.scoreboardData.init(this);
		this.shield.init(this);
		this.overworldTutorial.attemptStart();
		this.darkzoneTutorial.attemptStart();
		updateXPBar();

		this.isInitialized = true;
	}

	public static boolean loadPitPlayer(UUID playerUUID) {
		for(PitPlayer testPitPlayer : pitPlayers) {
			if(testPitPlayer.player == null) continue;
			if(!testPitPlayer.player.getUniqueId().equals(playerUUID)) continue;
			AOutput.log("Found duplicate pitplayer for " + testPitPlayer.player.getName());
			return false;
		}

		PitPlayer pitPlayer;
		try {
			DocumentSnapshot documentSnapshot = FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION)
					.document(playerUUID.toString()).get().get();

			if(documentSnapshot.exists()) {
				pitPlayer = documentSnapshot.toObject(PitPlayer.class);
			} else {
				pitPlayer = new PitPlayer();
			}

			AOutput.log("Loaded Player: " + Bukkit.getOfflinePlayer(playerUUID).getName());
			assert pitPlayer != null;

			pitPlayer.uuid = playerUUID;

		} catch(Exception exception) {
			AOutput.log("----------------------------------------");
			AOutput.log("Playerdata for " + Bukkit.getOfflinePlayer(playerUUID).getName() + " failed to load");
			AOutput.log("Disconnecting player");
			AOutput.log("");
			exception.printStackTrace();
			AOutput.log("----------------------------------------");
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
				AOutput.log("PitPlayer is null and shouldn't be");
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
	public List<MegastreakLimit> getAllCooldowns() {
		return new ArrayList<>(megastreakCooldownMap.values());
	}

	@Exclude
	public MegastreakLimit getMegastreakCooldown(Megastreak megastreak) {
		if(!megastreak.hasDailyLimit) throw new RuntimeException();
		megastreakCooldownMap.putIfAbsent(megastreak.refName, new MegastreakLimit(megastreak));
		return megastreakCooldownMap.get(megastreak.refName);
	}

	public Map<String, MegastreakLimit> getMegastreakCooldownMap() {
		return megastreakCooldownMap;
	}

	public void setMegastreakCooldownMap(Map<String, MegastreakLimit> megastreakCooldownMap) {
		this.megastreakCooldownMap = megastreakCooldownMap;
	}

	@Exclude
	public boolean isOnMega() {
		return kills >= megastreak.requiredKills;
	}

	@Exclude
	public void endKillstreak() {
		ReachKillstreakQuest.INSTANCE.endStreak(this, kills);
		megastreak.reset(player);
		for(Killstreak killstreak : killstreaks) killstreak.reset(player);
		kills = 0;
		latestKillAnnouncement = 0;
	}

	@Exclude
	public Megastreak getMegastreak() {
		return megastreak;
	}

	@Exclude
	public void setMegastreak(Megastreak megastreak) {
		this.megastreak = megastreak;
		ChatTriggerManager.sendPerksInfo(this);
	}

	@Exclude
	public void incrementKills() {
		kills++;
		Bukkit.getPluginManager().callEvent(new IncrementKillsEvent(this.player, kills));

		for(Killstreak killstreak : killstreaks) {
			if(kills == 0 || kills % killstreak.killInterval != 0) continue;
			killstreak.proc(player);
		}

		int notifyEvery = megastreak instanceof RNGesus && kills > RNGesus.INSTABILITY_THRESHOLD ? 250 : 100;
		if(kills % notifyEvery == 0 && kills != megastreak.requiredKills) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
				if(pitPlayer.streaksDisabled) continue;
				String message = ChatColor.translateAlternateColorCodes(
						'&', "&c&lSTREAK!&7 of &c" + kills + " &7by %luckperms_prefix%" + player.getDisplayName());
				onlinePlayer.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
			}
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
		return heal(amount, healType, max, null);
	}

	@Exclude
	public HealEvent heal(double amount, HealEvent.HealType healType, int max, PitEnchant pitEnchant) {
		return Misc.heal(player, amount, healType, max, pitEnchant);
	}

	@Exclude
	public boolean hasPerk(PitPerk pitPerk) {
		for(PitPerk perk : pitPerks) if(perk == pitPerk) return true;
		return false;
	}

	@Exclude
	public boolean hasManaUnlocked() {
		return ProgressionManager.isUnlocked(this, ManaBranch.INSTANCE, SkillBranch.MajorUnlockPosition.FIRST);
	}

	public double getMana() {
		if(!hasManaUnlocked()) return 0;
		return mana;
	}

	public void giveMana(double amount) {
		if(!hasManaUnlocked()) return;
		mana = Math.min(mana + amount, getMaxMana());
	}

	@Exclude
	public boolean useManaForSpell(int amount) {
		if(!hasManaUnlocked()) return false;
		if(ProgressionManager.isUnlocked(this, ManaBranch.INSTANCE, SkillBranch.MajorUnlockPosition.LAST))
			amount *= Misc.getReductionMultiplier(ManaBranch.getSpellManaReduction());
		return useMana(amount);
	}

	@Exclude
	public boolean useMana(int amount) {
		if(!hasManaUnlocked() || amount > mana) return false;
		mana = Math.max(mana - amount, 0);
		return true;
	}

	@Exclude
	public int getMaxMana() {
		int maxMana = 100;
		maxMana += ProgressionManager.getUnlockedEffectAsValue(this, ManaBranch.INSTANCE,
				SkillBranch.PathPosition.FIRST_PATH, "max-mana");
		return maxMana;
	}

	@Exclude
	public void updateMaxHealth() {
		int maxHealth = 24;
		if(hasPerk(Thick.INSTANCE) && !MapManager.inDarkzone(player)) maxHealth += 4;

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
		maxHealth += Hearts.getExtraHealth(enchantMap);
		maxHealth += Tanky.getExtraHealth(enchantMap);

		if(megastreak instanceof Uberstreak) {
			List<Uberstreak.UberEffect> uberEffects = Uberstreak.getUberEffects(player);
			if(uberEffects.contains(Uberstreak.UberEffect.LOSE_MAX_HEALTH)) maxHealth -= 4;
		} else if(megastreak instanceof Apostle) {
			maxHealth -= Apostle.getRemovedHealth(this);
		}

		if(Killstreak.hasKillstreak(player, "Monster") && Monster.healthMap.containsKey(player)) maxHealth += Monster.healthMap.get(player);

		if(maxHealth <= 0) {
			DamageManager.killPlayer(player);
			updateMaxHealth();
			player.setHealth(player.getMaxHealth());
			return;
		}

		if(player.getMaxHealth() == maxHealth) return;
		player.setMaxHealth(maxHealth);
	}

	@Exclude
	public void updateXPBar() {
		if(MapManager.inDarkzone(player)) {
			if(!shield.isUnlocked()) {
				player.setLevel(0);
				player.setExp(0);
			} else if(shield.isActive()) {
				player.setLevel((int) Math.ceil(shield.getDisplayAmount()));
				player.setExp((float) (shield.getPreciseAmount() / shield.getMaxShield()));
			} else {
				player.setLevel(0);
				float progress = 1 - ((float) shield.getTicksUntilReactivation() / shield.getInitialTicksUntilReactivation());
				player.setExp(Math.min(progress, 1));
			}
			return;
		}

		if(megastreak instanceof RNGesus && getKills() < RNGesus.INSTABILITY_THRESHOLD && getKills() >= 100) {
			RNGesus.RNGesusInfo rngesusInfo = RNGesus.getRNGesusInfo(player);
			RNGesus.RealityInfo realityInfo = rngesusInfo.realityMap.get(rngesusInfo.reality);

			int level = realityInfo.getLevel();
			float currentAmount = (float) realityInfo.progression;
			float currentTier = (float) realityInfo.getProgression(level);
			float nextTier = (float) realityInfo.getProgression(level + 1);

			player.setLevel(level);
			float ratio = (currentAmount - currentTier) / (nextTier - currentTier);
			player.setExp(ratio);
			return;
		}

		player.setLevel(level);
		float remaining = remainingXP;
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(prestige);
		float total = (float) (PrestigeValues.getXPForLevel(level) * prestigeInfo.getXpMultiplier());

		player.setLevel(level);
		float xp = (total - remaining) / total;

		player.setExp(xp);
	}

	@Exclude
	public void updateManaBar() {
		List<String> messageSegments = new ArrayList<>();

		int mana = (int) Math.round(this.mana);
		int maxMana = getMaxMana();

		String manaBar1 = AUtil.createProgressBar(
				"|", ChatColor.AQUA, ChatColor.DARK_GRAY, 20, (mana * 2.0) / maxMana);
		String manaBar2 = AUtil.createProgressBar(
				"|", ChatColor.AQUA, ChatColor.DARK_GRAY, 20, Math.max((mana * 2.0 - maxMana) / maxMana, 0));
		messageSegments.add("&3&l[ " + manaBar1 + " &3&l" + mana + " " + manaBar2 + " &3&l]");

		String actionBarMessage = String.join(" ", messageSegments);
		ActionBarManager.sendActionBar(player, null, actionBarMessage);
	}

	@Exclude
	public void updateWalkingSpeed() {
		float previousWalkSpeed = player.getWalkSpeed();

		float newWalkSpeed = 0.2F;
		newWalkSpeed *= 1 + (Sonic.getWalkSpeedIncrease(this) / 100.0);
		newWalkSpeed *= 1 + (GottaGoFast.getWalkSpeedIncrease(this) / 100.0);

		if(previousWalkSpeed != newWalkSpeed) player.setWalkSpeed(newWalkSpeed);
	}

	@Exclude
	public boolean hasFastTravelUnlocked(SubLevel subLevel) {
		if(subLevel == null) return false;
		return darkzoneData.fastTravelData.unlockedLocations.contains(subLevel.getIndex());
	}

	@Exclude
	public void unlockFastTravelDestination(SubLevel subLevel) {
		if(subLevel == null || darkzoneData.fastTravelData.unlockedLocations.contains(subLevel.getIndex())) return;
		darkzoneData.fastTravelData.unlockedLocations.add(subLevel.getIndex());
		FastTravelDestination destination = FastTravelManager.getDestination(subLevel);
		if(destination == null) return;
		AOutput.send(player, "&f&lFAST TRAVEL!&7 Unlocked access to " + destination.displayName + "&7!");
		Sounds.RENOWN_SHOP_PURCHASE.play(player);
	}

	@Exclude
	public void giveSouls(int amount) {
		giveSouls(amount, true);
	}

	@Exclude
	public void giveSouls(int amount, boolean stats) {
		taintedSouls += amount;
		if(stats) this.stats.lifetimeSouls += amount;
	}

	@Exclude
	public String getPrefix() {
		String rankColor = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%");
		String megaPrefix = megastreak.getPrefix(player);
		return (isOnMega() && megaPrefix != null ? megaPrefix + " " : PrestigeValues.getPlayerPrefixNameTag(player)) + rankColor;
	}

	public static class MegastreakLimit {
		private final long COOLDOWN_LENGTH = 1000 * 60 * 60 * 20;

		private String megastreakRef;
		private int streaksCompleted = 0;
		private long lastReset = System.currentTimeMillis();

		public MegastreakLimit() {
		}

		public MegastreakLimit(Megastreak megastreak) {
			this.megastreakRef = megastreak.refName;
		}

		@Exclude
		public void attemptReset(PitPlayer pitPlayer) {
			if(lastReset + COOLDOWN_LENGTH > System.currentTimeMillis()) return;
			forceReset(pitPlayer);
		}

		@Exclude
		public void forceReset(PitPlayer pitPlayer) {
			streaksCompleted = 0;
			lastReset = System.currentTimeMillis();
			ChatTriggerManager.sendPerksInfo(pitPlayer);
		}

		@Exclude
		public void completeStreak(PitPlayer pitPlayer) {
			streaksCompleted++;
			if(isAtLimit(pitPlayer)) pitPlayer.setMegastreak(NoMegastreak.INSTANCE);
			ChatTriggerManager.sendPerksInfo(pitPlayer);
		}

		@Exclude
		public String getTimeLeft() {
			long timeRemaining = lastReset + COOLDOWN_LENGTH - System.currentTimeMillis();
			return Formatter.formatDurationFull(timeRemaining, true);
		}

		@Exclude
		public boolean shouldDisplayResetTime() {
			return streaksCompleted != 0 && lastReset + COOLDOWN_LENGTH > System.currentTimeMillis();
		}

		@Exclude
		public boolean isAtLimit(PitPlayer pitPlayer) {
			return streaksCompleted >= getMegastreak().getMaxDailyStreaks(pitPlayer);
		}

		@Exclude
		public int getStreaksLeft(PitPlayer pitPlayer) {
			return getMegastreak().getMaxDailyStreaks(pitPlayer) - streaksCompleted;
		}

		public int getStreaksCompleted() {
			return streaksCompleted;
		}

		public String getMegastreakRef() {
			return megastreakRef;
		}

		@Exclude
		public Megastreak getMegastreak() {
			return PerkManager.getMegastreak(megastreakRef);
		}

		public long getLastReset() {
			return lastReset;
		}

		public void setMegastreakRef(String megastreakRef) {
			this.megastreakRef = megastreakRef;
		}

		public void setStreaksCompleted(int streaksCompleted) {
			this.streaksCompleted = streaksCompleted;
		}

		public void setLastReset(long lastReset) {
			this.lastReset = lastReset;
		}
	}

	@Deprecated
	public int soulsGathered = 0;
}
