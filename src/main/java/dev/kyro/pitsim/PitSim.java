package dev.kyro.pitsim;

import ac.grim.grimac.GrimAbstractAPI;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import com.sk89q.worldedit.EditSession;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import de.myzelyam.api.vanish.VanishAPI;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.ArcticAPI;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.hooks.AHook;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.battlepass.PassManager;
import dev.kyro.pitsim.battlepass.quests.*;
import dev.kyro.pitsim.battlepass.quests.daily.DailyBotKillQuest;
import dev.kyro.pitsim.battlepass.quests.daily.DailyPlayerKillQuest;
import dev.kyro.pitsim.battlepass.quests.daily.DailySWGamePlayedQuest;
import dev.kyro.pitsim.battlepass.quests.dzkillmobs.*;
import dev.kyro.pitsim.boosters.ChaosBooster;
import dev.kyro.pitsim.boosters.GoldBooster;
import dev.kyro.pitsim.boosters.PvPBooster;
import dev.kyro.pitsim.boosters.XPBooster;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.ingredients.*;
import dev.kyro.pitsim.brewing.objects.BrewingIngredient;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.commands.*;
import dev.kyro.pitsim.commands.admin.*;
import dev.kyro.pitsim.commands.essentials.*;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.cosmetics.CosmeticManager;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.cosmetics.aura.*;
import dev.kyro.pitsim.cosmetics.bounty.*;
import dev.kyro.pitsim.cosmetics.capes.*;
import dev.kyro.pitsim.cosmetics.killeffectsbot.AlwaysExe;
import dev.kyro.pitsim.cosmetics.killeffectsbot.OnlyExe;
import dev.kyro.pitsim.cosmetics.killeffectsbot.Tetris;
import dev.kyro.pitsim.cosmetics.killeffectsplayer.*;
import dev.kyro.pitsim.cosmetics.misc.ElectricPresence;
import dev.kyro.pitsim.cosmetics.misc.Halo;
import dev.kyro.pitsim.cosmetics.misc.KyroCosmetic;
import dev.kyro.pitsim.cosmetics.misc.MysticPresence;
import dev.kyro.pitsim.cosmetics.trails.*;
import dev.kyro.pitsim.enchants.GoldBoost;
import dev.kyro.pitsim.enchants.*;
import dev.kyro.pitsim.enchants.tainted.*;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.ThrowBlockEvent;
import dev.kyro.pitsim.helmetabilities.*;
import dev.kyro.pitsim.killstreaks.*;
import dev.kyro.pitsim.kits.EssentialKit;
import dev.kyro.pitsim.kits.GoldKit;
import dev.kyro.pitsim.kits.PvPKit;
import dev.kyro.pitsim.kits.XPKit;
import dev.kyro.pitsim.leaderboards.*;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.megastreaks.*;
import dev.kyro.pitsim.misc.*;
import dev.kyro.pitsim.misc.packets.SignPrompt;
import dev.kyro.pitsim.npcs.*;
import dev.kyro.pitsim.perks.*;
import dev.kyro.pitsim.pitmaps.BiomesMap;
import dev.kyro.pitsim.pitmaps.SandMap;
import dev.kyro.pitsim.pitmaps.XmasMap;
import dev.kyro.pitsim.placeholders.*;
import dev.kyro.pitsim.storage.StorageManager;
import dev.kyro.pitsim.upgrades.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.luckperms.api.LuckPerms;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import septogeddon.pluginquery.PluginQuery;
import septogeddon.pluginquery.api.QueryMessenger;

import java.io.File;
import java.util.*;

public class PitSim extends JavaPlugin {
	public static final double VERSION = 3.0;
	public static final boolean PASS_ENABLED = true;

	public static LuckPerms LUCKPERMS;
	public static GrimAbstractAPI GRIM;
	public static PitSim INSTANCE;
	public static ProtocolManager PROTOCOL_MANAGER = null;
	public static BukkitAudiences adventure;

	public static String serverName;

	public static PteroClient client = PteroBuilder.createClient("***REMOVED***", PrivateInfo.PTERO_KEY);

	public static long currentTick = 0;

	public static ServerStatus status;

	public static AnticheatManager anticheat;

	@Override
	public void onEnable() {
		INSTANCE = this;

		PlayerDataManager.exemptedPlayers.addAll(Bukkit.getOnlinePlayers());

		loadConfig();
		ArcticAPI.configInit(this, "prefix", "error-prefix");
		serverName = AConfig.getString("server");
		FirestoreManager.init();
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			PlayerManager.addRealPlayer(onlinePlayer.getUniqueId());
			boolean success = PitPlayer.loadPitPlayer(onlinePlayer.getUniqueId());
			if(success) continue;
			onlinePlayer.kickPlayer(ChatColor.RED + "Playerdata failed to load. Please open a support ticket: discord.pitsim.net");
		}

		if(Bukkit.getPluginManager().getPlugin("GrimAC") != null) hookIntoAnticheat(new GrimManager());
		if(Bukkit.getPluginManager().getPlugin("PolarLoader") != null) hookIntoAnticheat(new PolarManager());

		if(anticheat == null) {
			Bukkit.getLogger().severe("No anticheat found! Shutting down...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else getServer().getPluginManager().registerEvents(anticheat, this);

		if(AConfig.getBoolean("standalone-server")) status = ServerStatus.ALL;
		else status = serverName.contains("darkzone") ? ServerStatus.DARKZONE : ServerStatus.PITSIM;

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		adventure = BukkitAudiences.create(this);
		if(getStatus().isDarkzone()) TaintedWell.onStart();
		if(getStatus().isDarkzone()) BrewingManager.onStart();
		ScoreboardManager.init();

		if(getStatus().isDarkzone()) MobManager.clearMobs();

		RegisteredServiceProvider<LuckPerms> luckpermsProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if(luckpermsProvider != null) LUCKPERMS = luckpermsProvider.getProvider();

		RegisteredServiceProvider<GrimAbstractAPI> grimProvider = Bukkit.getServicesManager().getRegistration(GrimAbstractAPI.class);
		if (grimProvider != null) GRIM = grimProvider.getProvider();

		PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

		new BukkitRunnable() {
			@Override
			public void run() {
				List<NPC> toRemove = new ArrayList<>();
				for(NPC npc : CitizensAPI.getNPCRegistry()) {
					toRemove.add(npc);
				}
				while(!toRemove.isEmpty()) {
					toRemove.get(0).destroy();
					toRemove.remove(0);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		registerMaps();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(getStatus().isDarkzone()) BossManager.onStart();
			}
		}.runTaskLater(PitSim.INSTANCE, 20);

		MapManager.onStart();
		if(getStatus().isPitsim()) NonManager.init();
		SignPrompt.registerSignUpdateListener();
		TempBlockHelper.init();
		ReloadManager.init();

		if(!Bukkit.getServer().getPluginManager().getPlugin("NoteBlockAPI").getDescription().getVersion().toLowerCase().contains("kyro")) {
			AOutput.log("Wrong version of NoteBlockAPI found");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

//		Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
//		EntityDamageEvent.getHandlerList().unregister(essentials);

		Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
		BlockIgniteEvent.getHandlerList().unregister(worldGuard);

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
		} else {
			AOutput.log(String.format("Could not find PlaceholderAPI! This plugin is required."));
			Bukkit.getPluginManager().disablePlugin(this);
		}

		if(!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
			getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
			return;
		}

		QueryMessenger messenger = PluginQuery.getMessenger();
		messenger.getEventBus().registerListener(new PluginMessageManager());

		registerBoosters();
		registerUpgrades();
		registerPerks();
		registerKillstreaks();
		registerMegastreaks();
		registerPassQuests();
		if(getStatus().isPitsim()) registerLeaderboards();
		if(getStatus().isPitsim()) LeaderboardManager.init();

		ArcticAPI.setupPlaceholderAPI("pitsim");
		AHook.registerPlaceholder(new PrefixPlaceholder());
		AHook.registerPlaceholder(new SuffixPlaceholder());
		AHook.registerPlaceholder(new StrengthChainingPlaceholder());
		AHook.registerPlaceholder(new GladiatorPlaceholder());
		AHook.registerPlaceholder(new CombatTimerPlaceholder());
		AHook.registerPlaceholder(new StreakPlaceholder());
		AHook.registerPlaceholder(new ExperiencePlaceholder());
		AHook.registerPlaceholder(new LevelPlaceholder());
		AHook.registerPlaceholder(new PlayerKillsPlaceholder());
		AHook.registerPlaceholder(new GuildPlaceholder());
		AHook.registerPlaceholder(new GuildPlaceholder2());
		AHook.registerPlaceholder(new GuildPlaceholder3());
		AHook.registerPlaceholder(new GuildPlaceholder4());
		AHook.registerPlaceholder(new GuildPlaceholder5());
		AHook.registerPlaceholder(new GuildPlaceholder6());
		AHook.registerPlaceholder(new GuildPlaceholder7());
		AHook.registerPlaceholder(new GuildPlaceholder8());
		AHook.registerPlaceholder(new GuildPlaceholder9());
		AHook.registerPlaceholder(new GuildPlaceholder10());
		AHook.registerPlaceholder(new PrestigeLevelPlaceholder());
		AHook.registerPlaceholder(new PrestigePlaceholder());
		AHook.registerPlaceholder(new ZombieCavePlaceholder());
		AHook.registerPlaceholder(new SkeletonCavePlaceholder());
		AHook.registerPlaceholder(new SpiderCavePlaceholder());
		AHook.registerPlaceholder(new CreeperCavePlaceholder());
		AHook.registerPlaceholder(new DeepSpiderCavePlaceholder());
		AHook.registerPlaceholder(new MagmaCavePlaceholder());
		AHook.registerPlaceholder(new PigmanCavePlaceholder());
		AHook.registerPlaceholder(new WitherCavePlaceholder());
		AHook.registerPlaceholder(new GolemCavePlaceholder());
		AHook.registerPlaceholder(new EndermanCavePlaceholder());
		AHook.registerPlaceholder(new GoldReqPlaceholder());
		AHook.registerPlaceholder(new SoulPlaceholder());
		AHook.registerPlaceholder(new SoulReqPlaceholder());
		AHook.registerPlaceholder(new PlayerCountPlaceholder());
		AHook.registerPlaceholder(new GoldPlaceholder());
		AHook.registerPlaceholder(new NicknamePlaceholder());
		AHook.registerPlaceholder(new ServerIPPlaceholder());
		new LeaderboardPlaceholders().register();

		CooldownManager.init();

		registerEnchants();
		registerCommands();
		registerListeners();
		registerHelmetAbilities();
		registerKits();
		if(getStatus().isDarkzone()) registerMobs();
		registerBrewingIngredients();
		registerCosmetics();

		PassManager.registerPasses();
		if(getStatus().isDarkzone()) AuctionManager.onStart();
		if(getStatus().isDarkzone()) AuctionDisplays.onStart();

		new BukkitRunnable() {
			@Override
			public void run() {
				ProxyMessaging.sendStartup();
			}
		}.runTaskLater(this, 20 * 10);

		new BukkitRunnable() {
			@Override
			public void run() {
				registerNPCs();
			}
		}.runTaskLater(PitSim.INSTANCE, 20);

	}

	@Override
	public void onDisable() {
//		System.out.println("Disconnecting database");
//		try {
//			for(FirebaseApp app : new ArrayList<>(FirebaseApp.getApps())) app.delete();
//		} catch(Exception exception) {
//			exception.printStackTrace();
//			System.out.println("Database failed to disconnect");
//		}
//		System.out.println("Database disconnected");

		FirestoreManager.AUCTION.save();

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);

//			disable cosmetics
			if(!VanishAPI.isInvisible(onlinePlayer)) {
				List<PitCosmetic> activeCosmetics = CosmeticManager.getEquippedCosmetics(pitPlayer);
				for(PitCosmetic activeCosmetic : activeCosmetics) activeCosmetic.disable(pitPlayer);
			}
		}

		for(World world : Bukkit.getWorlds()) {
			for(Entity entity : new ArrayList<>(world.getEntities())) {
				if(!(entity instanceof Item)) continue;
				ItemStack itemStack = ((Item) entity).getItemStack();
				NBTItem nbtItem = new NBTItem(itemStack);
				if(nbtItem.hasKey(NBTTag.CANNOT_PICKUP.getRef())) entity.remove();
			}
		}

		if(MapManager.getDarkzone() != null) {
			for(Entity entity : MapManager.getDarkzone().getEntities()) {
				if(entity instanceof Item) {
					entity.remove();
				}
			}
		}

		if(status.isDarkzone()) MobManager.clearMobs();

//		TODO: Fix
		for(Player player : Bukkit.getOnlinePlayers()) {
			List<PotionEffect> toExpire = new ArrayList<>();
			for(PotionEffect potionEffect : PotionManager.potionEffectList) {
				if(potionEffect.player == player) toExpire.add(potionEffect);
			}

			for(PotionEffect potionEffect : toExpire) {

				potionEffect.onExpire(true);

				String time = String.valueOf(System.currentTimeMillis());

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				pitPlayer.potionStrings.add(potionEffect.potionType.name + ":" + potionEffect.potency.tier + ":" + potionEffect.getTimeLeft() + ":" + time);
			}
		}

		if(getStatus().isDarkzone()) {
			for(NPC value : BossManager.clickables.values()) {
				value.destroy();
				NPCRegistry registry = CitizensAPI.getNPCRegistry();
				registry.deregister(value);
			}

			for(NPC clickable : AuctionDisplays.clickables) {
				clickable.destroy();
				NPCRegistry registry = CitizensAPI.getNPCRegistry();
				registry.deregister(clickable);
			}
		}

		for(EditSession session : FreezeSpell.sessions.keySet()) {
			session.undo(session);
		}

		TempBlockHelper.restoreSessions();

		for(Map.Entry<Location, Material> entry : FreezeSpell.blocks.entrySet()) {
			entry.getKey().getBlock().setType(entry.getValue());
		}

		if(status.isDarkzone()) {
			for(PitMob mob : MobManager.mobs) {
				MobManager.nameTags.get(mob.entity.getUniqueId()).remove();
				mob.entity.remove();
			}
		}

		if(this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}

		if(status.isDarkzone()) {
			for(Hologram hologram : BossManager.holograms) {
				hologram.delete();
			}
		}

		NPCManager.onDisable();
		List<Non> copyList = new ArrayList<>(NonManager.nons);
		for(Non non : copyList) {
			non.remove();
		}
		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) pitEnchant.onDisable();

		Iterator<Map.Entry<Player, EntitySongPlayer>> it = StereoManager.playerMusic.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Player, EntitySongPlayer> pair = it.next();
			EntitySongPlayer esp = pair.getValue();
			esp.destroy();
			it.remove();
		}

		File file = new File("plugins/Citizens/saves.yml");
		if(file.exists()) file.deleteOnExit();
	}

	private void registerMaps() {
		PitMap pitMap = null;
		long time;

		PitMap biomes = MapManager.registerMap(new BiomesMap("biomes", 7));
		PitMap sand = MapManager.registerMap(new SandMap("sand", 7));
//		PitMap dimensions = MapManager.registerMap(new DimensionsMap("dimensions", 7));
		PitMap xmas = MapManager.registerMap(new XmasMap("xmas", -1));

		String configString = AConfig.getString("current-map");
		if(configString == null || configString.isEmpty()) {
			pitMap = biomes;
			time = System.currentTimeMillis();
		} else {
			String[] split = configString.split(":");
			String mapName = split[0];
			time = Long.parseLong(split[1]);
			PitMap currentMap = MapManager.getMap(mapName);
			pitMap = currentMap;

			assert currentMap != null;
			if(Math.ceil((System.currentTimeMillis() - time) / 1000.0 / 60.0 / 60.0 / 24.0) >= currentMap.rotationDays) {
				pitMap = MapManager.getNextMap(currentMap);
				time = System.currentTimeMillis();
			}
		}

		if(TimeManager.isChristmasSeason() && status != ServerStatus.DARKZONE) {
			pitMap = xmas;
			time = System.currentTimeMillis();
			MapManager.currentMap.world.setStorm(true);
			MapManager.currentMap.world.setWeatherDuration(Integer.MAX_VALUE);
		}

		assert pitMap != null;

		AConfig.set("current-map", pitMap.world.getName() + ":" + time);
		AConfig.saveConfig();
		MapManager.setMap(pitMap);
	}

	private void registerPerks() {

		PerkManager.registerUpgrade(new NoPerk());
		PerkManager.registerUpgrade(new Vampire());
		PerkManager.registerUpgrade(new Dirty());
		PerkManager.registerUpgrade(new StrengthChaining());
		PerkManager.registerUpgrade(new Gladiator());
		PerkManager.registerUpgrade(new Thick());
//		PerkManager.registerUpgrade(new AssistantToTheStreaker());
		PerkManager.registerUpgrade(new FirstStrike());
		PerkManager.registerUpgrade(new Streaker());
		PerkManager.registerUpgrade(new CounterJanitor());
		PerkManager.registerUpgrade(new Telekinesis());
//		PerkManager.registerUpgrade(new Regenerative());
		PerkManager.registerUpgrade(new JewelHunter());
		PerkManager.registerUpgrade(new Dispersion());
	}

	private void registerKillstreaks() {
		PerkManager.registerKillstreak(new NoKillstreak());

		PerkManager.registerKillstreak(new Limiter());
		PerkManager.registerKillstreak(new Explicious());
		PerkManager.registerKillstreak(new AssuredStrike());
		PerkManager.registerKillstreak(new Leech());

//		PerkManager.registerKillstreak(new TacticalRetreat());
		PerkManager.registerKillstreak(new RAndR());
		PerkManager.registerKillstreak(new FightOrFlight());
		PerkManager.registerKillstreak(new HerosHaste());
		PerkManager.registerKillstreak(new CounterStrike());

		PerkManager.registerKillstreak(new Survivor());
		PerkManager.registerKillstreak(new AuraOfProtection());
		PerkManager.registerKillstreak(new GoldNanoFactory());
		PerkManager.registerKillstreak(new Baker());

		PerkManager.registerKillstreak(new Monster());
		PerkManager.registerKillstreak(new Spongesteve());
		PerkManager.registerKillstreak(new GoldStack());
		PerkManager.registerKillstreak(new Shockwave());
	}

	private void registerBosses() {

	}

	private void registerMegastreaks() {
		PerkManager.registerMegastreak(new Overdrive(null));
		PerkManager.registerMegastreak(new Highlander(null));
		PerkManager.registerMegastreak(new Uberstreak(null));
		PerkManager.registerMegastreak(new NoMegastreak(null));
		PerkManager.registerMegastreak(new Beastmode(null));
		PerkManager.registerMegastreak(new ToTheMoon(null));
		PerkManager.registerMegastreak(new RNGesus(null));
	}

	private void registerLeaderboards() {
		LeaderboardManager.registerLeaderboard(new XPLeaderboard());
		LeaderboardManager.registerLeaderboard(new GoldLeaderboard());
		LeaderboardManager.registerLeaderboard(new GoldGrindedLeaderboard());
//		LeaderboardManager.registerLeaderboard(new PlayerKillsLeaderboard());
		LeaderboardManager.registerLeaderboard(new BotKillsLeaderboard());
		LeaderboardManager.registerLeaderboard(new PlaytimeLeaderboard());
		LeaderboardManager.registerLeaderboard(new UbersCompletedLeaderboard());
		LeaderboardManager.registerLeaderboard(new JewelsCompletedLeaderboard());
		LeaderboardManager.registerLeaderboard(new FeathersLostLeaderboard());
		LeaderboardManager.registerLeaderboard(new BossesKilledLeaderboard());
		LeaderboardManager.registerLeaderboard(new LifetimeSoulsLeaderboard());
		LeaderboardManager.registerLeaderboard(new AuctionsWonLeaderboard());
		LeaderboardManager.registerLeaderboard(new HighestBidLeaderboard());
	}

	private void registerNPCs() {
		if(status.isDarkzone()) {
			NPCManager.registerNPC(new TaintedShopNPC(Collections.singletonList(MapManager.getDarkzone())));
			NPCManager.registerNPC(new LeggingsShopNPC(Collections.singletonList(MapManager.getDarkzone())));
			NPCManager.registerNPC(new PotionMasterNPC(Collections.singletonList(MapManager.getDarkzone())));
			NPCManager.registerNPC(new AuctioneerNPC(Collections.singletonList(MapManager.getDarkzone())));
		}

		if(status.isPitsim()) {
			NPCManager.registerNPC(new PerkNPC(Collections.singletonList(MapManager.currentMap.world)));
			NPCManager.registerNPC(new PassNPC(Collections.singletonList(MapManager.currentMap.world)));
			NPCManager.registerNPC(new PrestigeNPC(Collections.singletonList(MapManager.currentMap.world)));
			NPCManager.registerNPC(new KeeperNPC(Collections.singletonList(MapManager.currentMap.world)));
			NPCManager.registerNPC(new KitNPC(Collections.singletonList(MapManager.currentMap.world)));
			NPCManager.registerNPC(new StatsNPC(Collections.singletonList(MapManager.currentMap.world)));

			NPCManager.registerNPC(new KyroNPC(Collections.singletonList(MapManager.currentMap.world)));
			NPCManager.registerNPC(new WijiNPC(Collections.singletonList(MapManager.currentMap.world)));
			NPCManager.registerNPC(new SplkNPC(Collections.singletonList(MapManager.currentMap.world)));
		}
	}

	private void registerMobs() {
	}

	private void registerCommands() {
		AMultiCommand adminCommand = new BaseAdminCommand("pitsim");
		getCommand("ps").setExecutor(adminCommand);
		AMultiCommand giveCommand = new BaseSetCommand(adminCommand, "give");
		AMultiCommand setCommand = new BaseSetCommand(adminCommand, "set");
//		adminCommand.registerCommand(new AnticheatCommand("check"));
		new HopperCommand(adminCommand, "hopper");
		new UUIDCommand(adminCommand, "uuid");
		new RandomizeCommand(adminCommand, "randomize");
		new ReloadCommand(adminCommand, "reload");
		new BypassCommand(adminCommand, "bypass");
		new ExtendCommand(adminCommand, "extend");
		new LockdownCommand(adminCommand, "lockdown");
		new UnlockCosmeticCommand(adminCommand, "unlockcosmetic");
		new GodCommand(adminCommand, "god");
		new BountyCommand(setCommand, "bounty");

		new JewelCommand(giveCommand, "jewel");

		getCommand("atest").setExecutor(new ATestCommand());
		getCommand("fps").setExecutor(new FPSCommand());
		getCommand("oof").setExecutor(new OofCommand());
		getCommand("perks").setExecutor(new PerkCommand());
		getCommand("non").setExecutor(new NonCommand());
		getCommand("enchant").setExecutor(new EnchantCommand());
		getCommand("fresh").setExecutor(new FreshCommand());
		getCommand("show").setExecutor(new ShowCommand());
		getCommand("enchants").setExecutor(new EnchantListCommand());
		getCommand("donator").setExecutor(new DonatorCommand());
		getCommand("renown").setExecutor(new RenownCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("crategive").setExecutor(new OldCrateGiveCommand());
		getCommand("cg").setExecutor(new CrateGiveCommand());
		getCommand("store").setExecutor(new StoreCommand());
		getCommand("shop").setExecutor(new StoreCommand());
		getCommand("discord").setExecutor(new DiscordCommand());
		getCommand("disc").setExecutor(new DiscordCommand());
		getCommand("booster").setExecutor(new BoosterCommand());
		getCommand("boostergive").setExecutor(new BoosterGiveCommand());
		getCommand("resource").setExecutor(new ResourceCommand());
		getCommand("lightning").setExecutor(new LightningCommand());
		getCommand("stat").setExecutor(new StatCommand());
//		getCommand("captcha").setExecutor(new CaptchaCommand());
		getCommand("pay").setExecutor(new PayCommand());
		getCommand("cutscene").setExecutor(new CutsceneCommand());
		getCommand("kit").setExecutor(new KitCommand());
		getCommand("view").setExecutor(new ViewCommand());
		getCommand("music").setExecutor(new MusicCommand());
		getCommand("migrate").setExecutor(new MigrateCommand());
		if(PASS_ENABLED) getCommand("pass").setExecutor(new PassCommand());
		if(PASS_ENABLED) getCommand("quests").setExecutor(new QuestsCommand());
		SettingsCommand settingsCommand = new SettingsCommand();
		getCommand("settings").setExecutor(settingsCommand);
		getCommand("setting").setExecutor(settingsCommand);
		getCommand("set").setExecutor(settingsCommand);
		getCommand("potions").setExecutor(new PotionsCommand());
		getCommand("balance").setExecutor(new BalanceCommand());
		getCommand("eco").setExecutor(new EcoCommand());
		getCommand("ignore").setExecutor(new IgnoreCommand());
		getCommand("ignore").setTabCompleter(new IgnoreCommand());
		//TODO: Remove this
//		getCommand("massmigrate").setExecutor(new MassMigrateCommand());

		getCommand("gamemode").setExecutor(new GamemodeCommand());
		getCommand("nickname").setExecutor(new NicknameCommand());
		getCommand("fly").setExecutor(new FlyCommand());
		getCommand("fly").setTabCompleter(new FlyCommand());
		getCommand("teleport").setExecutor(new TeleportCommand());
		getCommand("teleporthere").setExecutor(new TeleportHereCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("trash").setExecutor(new TrashCommand());
		getCommand("rename").setExecutor(new RenameCommand());
	}

	private void registerListeners() {

		getServer().getPluginManager().registerEvents(new DamageManager(), this);
		getServer().getPluginManager().registerEvents(new ThrowBlockEvent(), this);
//		getServer().getPluginManager().registerEvents(new NonManager(), this);
		getServer().getPluginManager().registerEvents(new PlayerManager(), this);
		getServer().getPluginManager().registerEvents(new PlayerDataManager(), this);
		getServer().getPluginManager().registerEvents(new ChatManager(), this);
		getServer().getPluginManager().registerEvents(new DamageIndicator(), this);
		getServer().getPluginManager().registerEvents(new ItemManager(), this);
		getServer().getPluginManager().registerEvents(new CombatManager(), this);
		getServer().getPluginManager().registerEvents(new SpawnManager(), this);
		getServer().getPluginManager().registerEvents(new ItemRename(), this);
		getServer().getPluginManager().registerEvents(new EnderchestManager(), this);
		getServer().getPluginManager().registerEvents(new AFKManager(), this);
		getServer().getPluginManager().registerEvents(new EnchantManager(), this);
		getServer().getPluginManager().registerEvents(new TotallyLegitGem(), this);
		getServer().getPluginManager().registerEvents(new ChunkOfVile(), this);
		getServer().getPluginManager().registerEvents(new ReachAutoBan(), this);
//		getServer().getPluginManager().registerEvents(new NonAnticheat(), this);
//		getServer().getPluginManager().registerEvents(new HelmetListeners(), this);
		getServer().getPluginManager().registerEvents(new PitBlob(), this);
		getServer().getPluginManager().registerEvents(new BackwardsCompatibility(), this);
		getServer().getPluginManager().registerEvents(new YummyBread(), this);
		getServer().getPluginManager().registerEvents(new BoosterManager(), this);
		getServer().getPluginManager().registerEvents(new HopperManager(), this);
		getServer().getPluginManager().registerEvents(new ResourcePackManager(), this);
		getServer().getPluginManager().registerEvents(new StatManager(), this);
//		getServer().getPluginManager().registerEvents(new LockdownManager(), this);
		getServer().getPluginManager().registerEvents(new GoldenHelmet(), this);
		getServer().getPluginManager().registerEvents(new MapManager(), this);
		getServer().getPluginManager().registerEvents(new GuildIntegrationManager(), this);
		getServer().getPluginManager().registerEvents(new UpgradeManager(), this);
		getServer().getPluginManager().registerEvents(new KitManager(), this);
		if(getStatus().isDarkzone()) getServer().getPluginManager().registerEvents(new MobManager(), this);
		getServer().getPluginManager().registerEvents(new PortalManager(), this);
		if(getStatus().isDarkzone()) getServer().getPluginManager().registerEvents(new BossManager(), this);
		if(getStatus().isDarkzone()) getServer().getPluginManager().registerEvents(new TaintedWell(), this);
		if(getStatus().isDarkzone()) getServer().getPluginManager().registerEvents(new BrewingManager(), this);
		getServer().getPluginManager().registerEvents(new PotionManager(), this);
		getServer().getPluginManager().registerEvents(new TaintedManager(), this);
		getServer().getPluginManager().registerEvents(new StereoManager(), this);
		if(getStatus().isDarkzone()) getServer().getPluginManager().registerEvents(new MusicManager(), this);
		if(getStatus().isDarkzone()) getServer().getPluginManager().registerEvents(new CutsceneManager(), this);
		if(getStatus().isDarkzone()) getServer().getPluginManager().registerEvents(new AuctionDisplays(), this);
		if(getStatus().isDarkzone()) getServer().getPluginManager().registerEvents(new AuctionManager(), this);
		getServer().getPluginManager().registerEvents(new ScoreboardManager(), this);
		getServer().getPluginManager().registerEvents(new ProxyMessaging(), this);
		getServer().getPluginManager().registerEvents(new LobbySwitchManager(), this);
		getServer().getPluginManager().registerEvents(new AuctionManager(), this);
		if(PASS_ENABLED) getServer().getPluginManager().registerEvents(new PassManager(), this);
		getServer().getPluginManager().registerEvents(new SkinManager(), this);
		getServer().getPluginManager().registerEvents(new TimeManager(), this);
		getServer().getPluginManager().registerEvents(new NPCManager(), this);
		getServer().getPluginManager().registerEvents(new CosmeticManager(), this);
		getServer().getPluginManager().registerEvents(new LogManager(), this);
		getServer().getPluginManager().registerEvents(new StorageManager(), this);
		getServer().getPluginManager().registerEvents(new CrossServerMessageManager(), this);
		getServer().getPluginManager().registerEvents(new PacketManager(), this);
		getServer().getPluginManager().registerEvents(new GrimManager(), this);
		getServer().getPluginManager().registerEvents(new MiscManager(), this);
		getServer().getPluginManager().registerEvents(new FirstJoinManager(), this);
		getServer().getPluginManager().registerEvents(new AIManager(), this);
	}

	public void registerBoosters() {
		BoosterManager.registerBooster(new XPBooster());
		BoosterManager.registerBooster(new GoldBooster());
		BoosterManager.registerBooster(new PvPBooster());
		BoosterManager.registerBooster(new ChaosBooster());
	}

	public void registerUpgrades() {
		UpgradeManager.registerUpgrade(new dev.kyro.pitsim.upgrades.GoldBoost());
		UpgradeManager.registerUpgrade(new XPBoost());
		UpgradeManager.registerUpgrade(new Tenacity());
		UpgradeManager.registerUpgrade(new UnlockStreaker());
		UpgradeManager.registerUpgrade(new UberIncrease());
		UpgradeManager.registerUpgrade(new DivineIntervention());
		UpgradeManager.registerUpgrade(new Withercraft());
		UpgradeManager.registerUpgrade(new UnlockFirstStrike());
		UpgradeManager.registerUpgrade(new Impatient());
		UpgradeManager.registerUpgrade(new Helmetry());
		UpgradeManager.registerUpgrade(new Chemist());
//		UpgradeManager.registerUpgrade(new SelfConfidence());
		UpgradeManager.registerUpgrade(new UnlockCounterJanitor());
		UpgradeManager.registerUpgrade(new LuckyKill());
		UpgradeManager.registerUpgrade(new LifeInsurance());
		UpgradeManager.registerUpgrade(new TaxEvasion());
		UpgradeManager.registerUpgrade(new DoubleDeath());
		UpgradeManager.registerUpgrade(new XPComplex());
		UpgradeManager.registerUpgrade(new KillSteal());
		UpgradeManager.registerUpgrade(new ShardHunter());
		UpgradeManager.registerUpgrade(new TheWay());
		UpgradeManager.registerUpgrade(new FastPass());
		UpgradeManager.registerUpgrade(new Celebrity());
	}

	private void registerHelmetAbilities() {
		HelmetAbility.registerHelmetAbility(new LeapAbility(null));
		HelmetAbility.registerHelmetAbility(new BlobAbility(null));
		HelmetAbility.registerHelmetAbility(new GoldRushAbility(null));
		HelmetAbility.registerHelmetAbility(new HermitAbility(null));
		HelmetAbility.registerHelmetAbility(new JudgementAbility(null));
		HelmetAbility.registerHelmetAbility(new PhoenixAbility(null));
		HelmetAbility.registerHelmetAbility(new ManaAbility(null));
	}

	private void registerKits() {
		KitManager.registerKit(new EssentialKit());
		KitManager.registerKit(new XPKit());
		KitManager.registerKit(new GoldKit());
		KitManager.registerKit(new PvPKit());
	}

	private void registerBrewingIngredients() {
		BrewingIngredient.registerIngredient(new RottenFlesh());
		BrewingIngredient.registerIngredient(new Bone());
		BrewingIngredient.registerIngredient(new SpiderEye());
		BrewingIngredient.registerIngredient(new Gunpowder());
		BrewingIngredient.registerIngredient(new FermentedSpiderEye());
		BrewingIngredient.registerIngredient(new MagmaCream());
		BrewingIngredient.registerIngredient(new RawPork());
		BrewingIngredient.registerIngredient(new WitherSkull());
		BrewingIngredient.registerIngredient(new IronIngot());
		BrewingIngredient.registerIngredient(new EnderPearl());
	}

	private void registerPassQuests() {
//		Daily quests
		PassManager.registerQuest(new DailyBotKillQuest());
		PassManager.registerQuest(new DailyPlayerKillQuest());
		PassManager.registerQuest(new DailySWGamePlayedQuest());

//		Weekly quests
		PassManager.registerQuest(new KillPlayersQuest());
		PassManager.registerQuest(new CompleteUbersQuest());
		PassManager.registerQuest(new DoTrueDamageVSBotsQuest());
		PassManager.registerQuest(new DoTrueDamageVSPlayersQuest());
		PassManager.registerQuest(new ReachKillstreakQuest());
		PassManager.registerQuest(new GrindXPQuest());
		PassManager.registerQuest(new GrindGoldQuest());
		PassManager.registerQuest(new HoursPlayedQuest());
		PassManager.registerQuest(new AttackBotsWithHealerQuest());
		PassManager.registerQuest(new LandMLBShotsQuest());
		PassManager.registerQuest(new UseHelmetGoldQuest());
		PassManager.registerQuest(new WinAuctionsQuest());
		PassManager.registerQuest(new EarnGuildReputationQuest());
		PassManager.registerQuest(new EarnRenownQuest());
		PassManager.registerQuest(new PunchUniquePlayers());
		PassManager.registerQuest(new GainAbsorptionQuest());
		PassManager.registerQuest(new SneakingBotKillQuest());
		PassManager.registerQuest(new WalkDistanceQuest());
		PassManager.registerQuest(new CongratulatePrestigeQuest());
		PassManager.registerQuest(new HaveSpeedQuest());
		PassManager.registerQuest(new JudgementHopperQuest());
		PassManager.registerQuest(new BrewPotionsQuest());

		PassManager.registerQuest(new KillZombiesQuest());
		PassManager.registerQuest(new KillSkeletonsQuest());
		PassManager.registerQuest(new KillSpidersQuest());
		PassManager.registerQuest(new KillCreepersQuest());
		PassManager.registerQuest(new KillCaveSpidersQuest());
		PassManager.registerQuest(new KillMagmaCubesQuest());
		PassManager.registerQuest(new KillZombiePigmenQuest());
		PassManager.registerQuest(new KillWitherSkeletonsQuest());
		PassManager.registerQuest(new KillIronGolemsQuest());
		PassManager.registerQuest(new KillEndermenQuest());
	}

	private void registerCosmetics() {
		CosmeticManager.registerCosmetic(new AlwaysExe());
		CosmeticManager.registerCosmetic(new OnlyExe());
		CosmeticManager.registerCosmetic(new Tetris());

		CosmeticManager.registerCosmetic(new DeathHowl());
		CosmeticManager.registerCosmetic(new DeathScream());
		CosmeticManager.registerCosmetic(new SuperMario());
		CosmeticManager.registerCosmetic(new DeathExplosion());
		CosmeticManager.registerCosmetic(new DeathFirework());

		CosmeticManager.registerCosmetic(new BountyBlueShell());
		CosmeticManager.registerCosmetic(new BountyBully());
		CosmeticManager.registerCosmetic(new BountyCope());
		CosmeticManager.registerCosmetic(new BountyQuickDropped());
		CosmeticManager.registerCosmetic(new BountyEmbarrassed());
		CosmeticManager.registerCosmetic(new BountyForgotToPay());
		CosmeticManager.registerCosmetic(new BountyHunted());
		CosmeticManager.registerCosmetic(new BountyPacking());
		CosmeticManager.registerCosmetic(new BountyRailed());
		CosmeticManager.registerCosmetic(new BountyRatted());
		CosmeticManager.registerCosmetic(new BountyReaper());
		CosmeticManager.registerCosmetic(new BountyRobbery());
		CosmeticManager.registerCosmetic(new BountySuffocated());
		CosmeticManager.registerCosmetic(new BountySystemMalfunction());
		CosmeticManager.registerCosmetic(new BountyTakeTheL());

		CosmeticManager.registerCosmetic(new SolidCape());
		CosmeticManager.registerCosmetic(new FireCape());
		CosmeticManager.registerCosmetic(new MagicCape());
		CosmeticManager.registerCosmetic(new CritCape());
		CosmeticManager.registerCosmetic(new CritMagicCape());

		CosmeticManager.registerCosmetic(new SmokeTrail());
		CosmeticManager.registerCosmetic(new FootstepTrail());
		CosmeticManager.registerCosmetic(new IceTrail());
		CosmeticManager.registerCosmetic(new RainbowTrail());
		CosmeticManager.registerCosmetic(new CoalTrail());
		CosmeticManager.registerCosmetic(new IronTrail());
		CosmeticManager.registerCosmetic(new RedstoneTrail());
		CosmeticManager.registerCosmetic(new LapisTrail());
		CosmeticManager.registerCosmetic(new DiamondTrail());
		CosmeticManager.registerCosmetic(new EmeraldTrail());
		CosmeticManager.registerCosmetic(new SlimeTrail());
		CosmeticManager.registerCosmetic(new LavaTrail());

		CosmeticManager.registerCosmetic(new KyroAura());
		CosmeticManager.registerCosmetic(new LivelyAura());
		CosmeticManager.registerCosmetic(new PotionAura());
		CosmeticManager.registerCosmetic(new WaterAura());
		CosmeticManager.registerCosmetic(new FireAura());
		CosmeticManager.registerCosmetic(new MysticAura());
		CosmeticManager.registerCosmetic(new FireworkAura());

		CosmeticManager.registerCosmetic(new KyroCosmetic());
		CosmeticManager.registerCosmetic(new MysticPresence());
		CosmeticManager.registerCosmetic(new ElectricPresence());
		CosmeticManager.registerCosmetic(new Halo());

		CosmeticManager.loadForOnlinePlayers();
	}

	private void loadConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onLoad() {
		File file = new File("plugins/Citizens/save.yml");
		if(file.exists()) file.delete();
	}

	private void registerEnchants() {
		EnchantManager.registerEnchant(new ComboVenom());
//		EnchantManager.registerEnchant(new aCPLEnchant());
		EnchantManager.registerEnchant(new SelfCheckout());

		EnchantManager.registerEnchant(new Billionaire());
		EnchantManager.registerEnchant(new ComboPerun());
		EnchantManager.registerEnchant(new Executioner());
		EnchantManager.registerEnchant(new Gamble());
		EnchantManager.registerEnchant(new ComboStun());
		EnchantManager.registerEnchant(new SpeedyHit());
		EnchantManager.registerEnchant(new Healer());
		EnchantManager.registerEnchant(new Lifesteal());
		EnchantManager.registerEnchant(new ComboHeal());

		EnchantManager.registerEnchant(new Shark());
		EnchantManager.registerEnchant(new PainFocus());
		EnchantManager.registerEnchant(new DiamondStomp());
		EnchantManager.registerEnchant(new ComboDamage());
		EnchantManager.registerEnchant(new Berserker());
		EnchantManager.registerEnchant(new KingBuster());
		EnchantManager.registerEnchant(new Sharp());
		EnchantManager.registerEnchant(new Punisher());
		EnchantManager.registerEnchant(new BeatTheSpammers());
		EnchantManager.registerEnchant(new GoldAndBoosted());

		EnchantManager.registerEnchant(new ComboSwift());
		EnchantManager.registerEnchant(new BulletTime());
		EnchantManager.registerEnchant(new Guts());
		EnchantManager.registerEnchant(new Crush());

		EnchantManager.registerEnchant(new MegaLongBow());
		EnchantManager.registerEnchant(new Robinhood());
		EnchantManager.registerEnchant(new Volley());
		EnchantManager.registerEnchant(new Telebow());
		EnchantManager.registerEnchant(new Pullbow());
		EnchantManager.registerEnchant(new Explosive());
		EnchantManager.registerEnchant(new TrueShot());
		EnchantManager.registerEnchant(new LuckyShot());

		EnchantManager.registerEnchant(new SprintDrain());
		EnchantManager.registerEnchant(new Wasp());
		EnchantManager.registerEnchant(new PinDown());
		EnchantManager.registerEnchant(new FasterThanTheirShadow());
		EnchantManager.registerEnchant(new PushComesToShove());
		EnchantManager.registerEnchant(new Parasite());
		EnchantManager.registerEnchant(new Chipping());
		EnchantManager.registerEnchant(new Fletching());
		EnchantManager.registerEnchant(new Sniper());
		EnchantManager.registerEnchant(new SpammerAndProud());
		EnchantManager.registerEnchant(new Jumpspammer());

		EnchantManager.registerEnchant(new RetroGravityMicrocosm());
		EnchantManager.registerEnchant(new Regularity());
		EnchantManager.registerEnchant(new Solitude());

		EnchantManager.registerEnchant(new Mirror());
		EnchantManager.registerEnchant(new Sufferance());
		EnchantManager.registerEnchant(new CriticallyFunky());
		EnchantManager.registerEnchant(new FractionalReserve());
		EnchantManager.registerEnchant(new NotGladiator());
		EnchantManager.registerEnchant(new Protection());
		EnchantManager.registerEnchant(new RingArmor());

		EnchantManager.registerEnchant(new Peroxide());
		EnchantManager.registerEnchant(new Booboo());
		EnchantManager.registerEnchant(new ReallyToxic());
		EnchantManager.registerEnchant(new NewDeal());
		EnchantManager.registerEnchant(new HeighHo());

		EnchantManager.registerEnchant(new GoldenHeart());
		EnchantManager.registerEnchant(new Hearts());
		EnchantManager.registerEnchant(new Prick());
		EnchantManager.registerEnchant(new Electrolytes());
		EnchantManager.registerEnchant(new GottaGoFast());
		EnchantManager.registerEnchant(new CounterOffensive());
		EnchantManager.registerEnchant(new LastStand());
		EnchantManager.registerEnchant(new Stereo());
//		EnchantManager.registerEnchant(new DiamondAllergy());
//		EnchantManager.registerEnchant(new PitBlob());

//		Resource Enchants
		EnchantManager.registerEnchant(new Moctezuma());
		EnchantManager.registerEnchant(new GoldBump());
		EnchantManager.registerEnchant(new GoldBoost());

		EnchantManager.registerEnchant(new Sweaty());
//		EnchantManager.registerEnchant(new XpBump());

		EnchantManager.registerEnchant(new FireballSpell());
		EnchantManager.registerEnchant(new RepelSpell());
		EnchantManager.registerEnchant(new FreezeSpell());
		EnchantManager.registerEnchant(new SweepingEdgeSpell());
		EnchantManager.registerEnchant(new MeteorSpell());
		EnchantManager.registerEnchant(new SavingGraceSpell());
		EnchantManager.registerEnchant(new CleaveSpell());
		EnchantManager.registerEnchant(new WarpSpell());
		EnchantManager.registerEnchant(new ExtractSpell());

		EnchantManager.registerEnchant(new TaintedSoul());
		EnchantManager.registerEnchant(new MaxHealth());
		EnchantManager.registerEnchant(new MaxMana());
		EnchantManager.registerEnchant(new ManaRegeneration());
		EnchantManager.registerEnchant(new EmotionalDamage());
		EnchantManager.registerEnchant(new Sonic());
		EnchantManager.registerEnchant(new Inferno());
		EnchantManager.registerEnchant(new Laser());
		EnchantManager.registerEnchant(new Forcefield());
	}

	public void hookIntoAnticheat(AnticheatManager anticheat) {
		if(PitSim.anticheat != null) {
			Bukkit.getLogger().severe("Multiple anticheats found! Shutting down...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		PitSim.anticheat = anticheat;
	}

	public enum ServerStatus {
		DARKZONE, PITSIM, ALL;

		public boolean isDarkzone() {
			return this == DARKZONE || this == ALL;
		}

		public boolean isPitsim() {
			return this == PITSIM || this == ALL;
		}

		public boolean isAll() {
			return true;
		}
	}

	public static ServerStatus getStatus() {
		return status;
	}
}
