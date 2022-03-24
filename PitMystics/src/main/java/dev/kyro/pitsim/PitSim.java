package dev.kyro.pitsim;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import dev.kyro.arcticapi.ArcticAPI;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.data.AData;
import dev.kyro.arcticapi.hooks.AHook;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.boosters.ChaosBooster;
import dev.kyro.pitsim.boosters.GoldBooster;
import dev.kyro.pitsim.boosters.PvPBooster;
import dev.kyro.pitsim.boosters.XPBooster;
import dev.kyro.pitsim.commands.*;
import dev.kyro.pitsim.commands.ViewCommand;
import dev.kyro.pitsim.commands.admin.*;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.log.DupeManager;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.enchants.GoldBoost;
import dev.kyro.pitsim.enchants.*;
import dev.kyro.pitsim.enchants.tainted.TaintedSoul;
import dev.kyro.pitsim.helmetabilities.*;
import dev.kyro.pitsim.killstreaks.*;
import dev.kyro.pitsim.kits.EssentialKit;
import dev.kyro.pitsim.kits.GoldKit;
import dev.kyro.pitsim.kits.PvPKit;
import dev.kyro.pitsim.kits.XPKit;
import dev.kyro.pitsim.leaderboards.*;
import dev.kyro.pitsim.megastreaks.*;
import dev.kyro.pitsim.misc.*;
import dev.kyro.pitsim.perks.*;
import dev.kyro.pitsim.pitmaps.BiomesMap;
import dev.kyro.pitsim.placeholders.*;
import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.TaskListener;
import dev.kyro.pitsim.tutorial.TutorialManager;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.upgrades.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PitSim extends JavaPlugin {
	public static double version = 2.0;

	public static LuckPerms LUCKPERMS;
	public static PitSim INSTANCE;
	public static Economy VAULT = null;
	public static ProtocolManager PROTOCOL_MANAGER = null;

	public static AData playerList;

	public static PteroClient client = PteroBuilder.createClient("***REMOVED***",
			"im4F1vVHTJKIjhRQcvJ8CAdOX3aCt99JmpukhFGbzQXI5BOQ");

	@Override
	public void onEnable() {
		INSTANCE = this;

		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if(provider != null) {
			LUCKPERMS = provider.getProvider();
		}

		PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

		List<NPC> toRemove = new ArrayList<>();
		CitizensAPI.getNPCRegistry().forEach(toRemove::add);
		while(!toRemove.isEmpty()) {
			toRemove.get(0).destroy();
			toRemove.remove(0);
		}

		registerMaps();
		MapManager.onStart();
		NonManager.init();
		SpawnNPCs.createNPCs();

		if(!setupEconomy()) {
			AOutput.log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
		EntityDamageEvent.getHandlerList().unregister(essentials);

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
		} else {
			AOutput.log(String.format("Could not find PlaceholderAPI! This plugin is required."));
			Bukkit.getPluginManager().disablePlugin(this);
		}

		boolean NoteBlockAPI = true;
		if(!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
			getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
			NoteBlockAPI = false;
			return;
		}

		registerUpgrades();
		registerPerks();
		registerKillstreaks();
		registerMegastreaks();
		registerLeaderboards();
		LeaderboardManager.init();

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
		new LeaderboardPlaceholders().register();

		loadConfig();

		ArcticAPI.configInit(this, "prefix", "error-prefix");
		playerList = new AData("player-list", "", false);

		CooldownManager.init();

		registerEnchants();
		registerCommands();
		registerListeners();
		registerBoosters();
		registerHelmetAbilities();
		registerKits();
	}

	@Override
	public void onDisable() {

		for(Tutorial value : TutorialManager.tutorials.values()) {
			value.cleanUp();
		}

		SpawnNPCs.removeNPCs();
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

		for(PitPlayer pitPlayer : PitPlayer.pitPlayers) if(pitPlayer.stats != null) pitPlayer.stats.save();
	}

	private void registerMaps() {
		MapManager.registerMap(new BiomesMap("biomes1", "biomes2"));
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
	}

	private void registerKillstreaks() {
		PerkManager.registerKillstreak(new NoKillstreak());

		PerkManager.registerKillstreak(new Dispersion());
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
		LeaderboardManager.registerLeaderboard(new GoldGrindedLeaderboard());
		LeaderboardManager.registerLeaderboard(new PlayerKillsLeaderboard());
		LeaderboardManager.registerLeaderboard(new BotKillsLeaderboard());
		LeaderboardManager.registerLeaderboard(new PlaytimeLeaderboard());
		LeaderboardManager.registerLeaderboard(new UbersCompletedLeaderboard());
		LeaderboardManager.registerLeaderboard(new JewelsCompletedLeaderboard());
		LeaderboardManager.registerLeaderboard(new FeathersLostLeaderboard());
	}

	private void registerCommands() {

		AMultiCommand adminCommand = new BaseAdminCommand("pitsim");
		getCommand("ps").setExecutor(adminCommand);
		AMultiCommand giveCommand = new BaseSetCommand(adminCommand, "give");
		AMultiCommand setCommand = new BaseSetCommand(adminCommand, "set");
//		adminCommand.registerCommand(new AnticheatCommand("check"));
		new HopperCommand(adminCommand, "hopper");
		new UUIDCommand(adminCommand, "uuid");
		new DupeCommand(adminCommand, "dupe");
		new RandomizeCommand(adminCommand, "randomize");
		new ReloadCommand(adminCommand, "reload");
		new BypassCommand(adminCommand, "bypass");
		new LockdownCommand(adminCommand, "lockdown");
		new SetPrestigeCommand(setCommand, "prestige");
		new SetLevelCommand(setCommand, "level");
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
		getCommand("captcha").setExecutor(new CaptchaCommand());
		SwitchCommand switchCommand = new SwitchCommand();
		getCommand("switch").setExecutor(switchCommand);
		getCommand("play").setExecutor(switchCommand);
		getCommand("pay").setExecutor(new PayCommand());
		getCommand("shutdown").setExecutor(new ShutdownCommand());
		getCommand("tutorial").setExecutor(new TutorialCommand());
		getCommand("kit").setExecutor(new KitCommand());
		getCommand("view").setExecutor(new ViewCommand());
	}

	private void registerListeners() {

		getServer().getPluginManager().registerEvents(new DamageManager(), this);
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
		getServer().getPluginManager().registerEvents(new SpawnNPCs(), this);
		getServer().getPluginManager().registerEvents(new BackwardsCompatibility(), this);
		getServer().getPluginManager().registerEvents(new YummyBread(), this);
		getServer().getPluginManager().registerEvents(new BoosterManager(), this);
		getServer().getPluginManager().registerEvents(new HopperManager(), this);
		getServer().getPluginManager().registerEvents(new ResourcePackManager(), this);
		getServer().getPluginManager().registerEvents(new StatManager(), this);
		getServer().getPluginManager().registerEvents(new LockdownManager(), this);
		getServer().getPluginManager().registerEvents(new DupeManager(), this);
		getServer().getPluginManager().registerEvents(new GoldenHelmet(), this);
		getServer().getPluginManager().registerEvents(new MapManager(), this);
		getServer().getPluginManager().registerEvents(new TaskListener(), this);
		getServer().getPluginManager().registerEvents(new MessageManager(), this);
		getServer().getPluginManager().registerEvents(new TutorialManager(), this);
		getServer().getPluginManager().registerEvents(new GuildIntegrationManager(), this);
		getServer().getPluginManager().registerEvents(new UpgradeManager(), this);
		getServer().getPluginManager().registerEvents(new KitManager(), this);
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
		UpgradeManager.registerUpgrade(new ShardHunter());
		UpgradeManager.registerUpgrade(new ReportAccess());
//		UpgradeManager.registerUpgrade(new SelfConfidence());
		UpgradeManager.registerUpgrade(new LuckyKill());
		UpgradeManager.registerUpgrade(new LifeInsurance());
		UpgradeManager.registerUpgrade(new TaxEvasion());
		UpgradeManager.registerUpgrade(new DoubleDeath());
		UpgradeManager.registerUpgrade(new XPComplex());
		UpgradeManager.registerUpgrade(new KillSteal());
		UpgradeManager.registerUpgrade(new UnlockCounterJanitor());
		UpgradeManager.registerUpgrade(new Celebrity());
		UpgradeManager.registerUpgrade(new FastPass());
	}

	private void registerHelmetAbilities() {
		HelmetAbility.registerHelmetAbility(new LeapAbility(null));
		HelmetAbility.registerHelmetAbility(new BlobAbility(null));
		HelmetAbility.registerHelmetAbility(new GoldRushAbility(null));
		HelmetAbility.registerHelmetAbility(new HermitAbility(null));
		HelmetAbility.registerHelmetAbility(new JudgementAbility(null));
		HelmetAbility.registerHelmetAbility(new PhoenixAbility(null));
	}

	private void registerKits() {
		KitManager.registerKit(new EssentialKit());
		KitManager.registerKit(new XPKit());
		KitManager.registerKit(new GoldKit());
		KitManager.registerKit(new PvPKit());
	}

	private void loadConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private boolean setupEconomy() {
		if(getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null) {
			return false;
		}
		VAULT = rsp.getProvider();
		return VAULT != null;
	}

	private void registerEnchants() {
		EnchantManager.registerEnchant(new ComboVenom());
//		EnchantManager.registerEnchant(new aCPLEnchant());
		EnchantManager.registerEnchant(new SelfCheckout());
		EnchantManager.registerEnchant(new aEntanglement());
		EnchantManager.registerEnchant(new aRetroGravityMinikloon());

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
		EnchantManager.registerEnchant(new aBowPlaceholder());
		EnchantManager.registerEnchant(new aBowPlaceholder());
		EnchantManager.registerEnchant(new aBowPlaceholder());
//		EnchantManager.registerEnchant(new BottomlessQuiver());

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

		EnchantManager.registerEnchant(new TaintedSoul());
	}
}
