package dev.kyro.pitsim.controllers.objects;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.notdarkzone.Shield;
import dev.kyro.pitsim.adarkzone.progression.DarkzoneData;
import dev.kyro.pitsim.battlepass.PassData;
import dev.kyro.pitsim.battlepass.PassManager;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.battlepass.quests.ReachKillstreakQuest;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.objects.BrewingSession;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.enchants.overworld.Hearts;
import dev.kyro.pitsim.enchants.tainted.uncommon.Tanky;
import dev.kyro.pitsim.enums.AChatColor;
import dev.kyro.pitsim.enums.DeathCry;
import dev.kyro.pitsim.enums.KillEffect;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.IncrementKillsEvent;
import dev.kyro.pitsim.inventories.ChatColorPanel;
import dev.kyro.pitsim.killstreaks.Limiter;
import dev.kyro.pitsim.killstreaks.Monster;
import dev.kyro.pitsim.killstreaks.NoKillstreak;
import dev.kyro.pitsim.megastreaks.*;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.perks.*;
import dev.kyro.pitsim.settings.scoreboard.ScoreboardData;
import dev.kyro.pitsim.storage.StorageManager;
import dev.kyro.pitsim.storage.StorageProfile;
import dev.kyro.pitsim.tutorial.Tutorial;
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
	public Player player;
	@Exclude
	public String prefix = "";

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
	public double mana = 0;

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
	public Megastreak megastreak;
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

	public double goldStack = 0;
	public int moonBonus = 0;
	public int dailyUbersLeft = 5;
	public long uberReset = 0;
	public int goldGrinded = 0;
	public Map<String, Integer> boosters = new HashMap<>();

	public double lastVersion = PitSim.VERSION;
	public KillEffect killEffect;
	public DeathCry deathCry;
	public AChatColor chatColor = AChatColor.GRAY;

	public List<String> brewingSessions = Arrays.asList(null, null, null);
	public int taintedSouls = 200;

	public List<String> potionStrings = new ArrayList<>();

	public List<String> auctionReturn = new ArrayList<>();
	public int soulReturn = 0;

	public boolean darkzoneCutscene = false;

	public PlayerStats stats = new PlayerStats();
	public Tutorial tutorial = new Tutorial();
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
		if(PitSim.getStatus() == PitSim.ServerStatus.STANDALONE || PlayerDataManager.exemptedPlayers.contains(uuid)) return;
		if(finalSave && lastSave + SAVE_COOLDOWN > System.currentTimeMillis()) {
			long timeUntilSave = lastSave + SAVE_COOLDOWN - System.currentTimeMillis();
			new Thread(() -> {
				try {
					Thread.sleep(timeUntilSave);
					save(true, callback, itemData);
				} catch(Exception exception) {
					System.out.println("--------------------------------------------------");
					System.out.println("CRITICAL ERROR: data for " + uuid + " failed to final save");
					System.out.println();
					exception.printStackTrace();
					System.out.println("--------------------------------------------------");
					Misc.alertDiscord("CRITICAL ERROR: data for " + player.getName() + " failed to final save");
				}
			}).start();
			return;
		}

		if(lastSave + SAVE_COOLDOWN > System.currentTimeMillis()) return;
		lastSave = System.currentTimeMillis();

		if(isNPC) {
			System.out.println("complete development failure. " + uuid + " is attempting to save data and is not a real player");
			return;
		}

		if(itemData) {
			StorageProfile profile = StorageManager.getProfile(uuid);
			profile.saveData(finalSave);
		}

		PotionManager.savePotions(this, finalSave);

		megastreakRef = megastreak.getRefNames().get(0);

		for(int i = 0; i < pitPerks.size(); i++) {
			PitPerk pitPerk = pitPerks.get(i);
			pitPerksRef.set(i, pitPerk.refName);
		}

		for(int i = 0; i < killstreaks.size(); i++) {
			Killstreak killstreak = killstreaks.get(i);
			killstreaksRef.set(i, killstreak.refName);
		}

		if(finalSave && callback != null) {
			FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION).document(uuid.toString())
					.set(this).addListener(callback, command -> {
						callback.runTask(PitSim.INSTANCE);
					});
			System.out.println("Saving Data (Blocking Thread): " + uuid.toString());
		} else {
			FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION).document(uuid.toString()).set(this);
			System.out.println("Saving Data: " + Bukkit.getOfflinePlayer(uuid).getName());
		}
	}

	//	NPC Init
	public PitPlayer(Player player) {
		this.isNPC = true;

		this.uuid = player.getUniqueId();
		this.player = player;
		this.megastreak = new NoMegastreak(this);
	}

	public PitPlayer() {
		for(Booster booster : BoosterManager.boosterList) boosters.put(booster.refName, 0);
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
		renown = playerData.getInt("renown");
		for(int i = 0; i < pitPerks.size(); i++) {
			PitPerk defaultPerk = NoPerk.INSTANCE;
			if(i == 0) defaultPerk = Vampire.INSTANCE;
			else if(i == 1) defaultPerk = StrengthChaining.INSTANCE;
			else if(i == 2) defaultPerk = Dirty.INSTANCE;
			else if(i == 3) defaultPerk = Dispersion.INSTANCE;

			String perkString = playerData.getString("perk-" + i);
			PitPerk savedPerk = perkString != null ? PitPerk.getPitPerk(perkString) : defaultPerk;
			pitPerks.set(i, savedPerk != null ? savedPerk : defaultPerk);
		}
		for(int i = 0; i < killstreaks.size(); i++) {
			Killstreak defaultKillstreak = NoKillstreak.INSTANCE;
			if(i == 0) defaultKillstreak = Limiter.INSTANCE;

			String killstreakString = playerData.getString("killstreak-" + i);
			Killstreak savedKillstreak = killstreakString != null ? Killstreak.getKillstreak(killstreakString) : NoKillstreak.INSTANCE;
			if(savedKillstreak == null) killstreaks.set(i, defaultKillstreak);
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
		dailyUbersLeft = playerData.contains("ubersleft") ? playerData.getInt("ubersleft") : 5;
		uberReset = playerData.getLong("ubercooldown");
		goldGrinded = playerData.getInt("goldgrinded");
		for(Booster booster : BoosterManager.boosterList)
			boosters.put(booster.refName, playerData.getInt("boosters." + booster.refName));

		stats = new PlayerStats(this, playerData);
		tutorial = new Tutorial(this, playerData);
//			updateXPBar();

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
			if(!upgrade.isTiered && playerData.contains(upgrade.refName)) tier = 1;
			else if(upgrade.isTiered && playerData.contains(upgrade.refName)) tier = playerData.getInt(upgrade.refName);
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

		stats.init(this);
		tutorial.init(this);
		scoreboardData.init(this);
		shield.init(this);
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
			DocumentSnapshot documentSnapshot = FirestoreManager.FIRESTORE.collection(FirestoreManager.PLAYERDATA_COLLECTION)
					.document(playerUUID.toString()).get().get();

			if(documentSnapshot.exists()) {
				pitPlayer = documentSnapshot.toObject(PitPlayer.class);
			} else {
				pitPlayer = new PitPlayer();
			}

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

		int everyX = megastreak instanceof RNGesus && kills > RNGesus.INSTABILITY_THRESHOLD ? 250 : 100;
		if(kills % everyX == 0 && kills != megastreak.getRequiredKills()) {
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
	public void updateMaxHealth() {

		int maxHealth = MapManager.inDarkzone(player) ? 20 : 24;
		if(hasPerk(Thick.INSTANCE) && !MapManager.inDarkzone(player)) maxHealth += 4;

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
		maxHealth += Hearts.INSTANCE.getExtraHealth(enchantMap);
		maxHealth += Tanky.INSTANCE.getExtraHealth(enchantMap);

		if(megastreak instanceof Uberstreak) {
			Uberstreak uberstreak = (Uberstreak) megastreak;
			if(uberstreak.uberEffects.contains(Uberstreak.UberEffect.LOSE_MAX_HEALTH)) maxHealth -= 4;
		}

		if(Killstreak.hasKillstreak(player, "Monster") && Monster.healthMap.containsKey(player)) {
			maxHealth += Monster.healthMap.get(player);
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
		return 100;
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

				DamageManager.kill(attackEvent, damager, player, KillType.DEFAULT);
			}
		} else player.damage(damage);
	}

	@Exclude
	public void updateXPBar() {
		if(MapManager.inDarkzone(player)) {
//			TODO: Check if shield is unlocked
			player.setLevel((int) Math.ceil(shield.getDisplayAmount()));
			if(shield.isActive()) {
				player.setExp((float) (shield.getPreciseAmount() / shield.getMax()));
			} else {
				player.setExp(1 - ((float) shield.getTicksUntilReactivation() / shield.getInitialTicksUntilReactivation()));
			}
			return;
		}

		if(megastreak instanceof RNGesus && getKills() < RNGesus.INSTABILITY_THRESHOLD && getKills() >= 100) return;

		player.setLevel(level);
		float remaining = remainingXP;
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(prestige);
		float total = (float) (PrestigeValues.getXPForLevel(level) * prestigeInfo.xpMultiplier);

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
}
