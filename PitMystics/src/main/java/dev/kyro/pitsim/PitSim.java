package dev.kyro.pitsim;

import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import dev.kyro.arcticapi.ArcticAPI;
import dev.kyro.arcticapi.data.AData;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.hooks.AHook;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.commands.*;
import dev.kyro.pitsim.controllers.*;
//import dev.kyro.pitsim.controllers.market.MarketManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.*;
import dev.kyro.pitsim.killstreaks.*;
import dev.kyro.pitsim.misc.ItemRename;
import dev.kyro.pitsim.perks.*;
import dev.kyro.pitsim.pitevents.TestEvent;
import dev.kyro.pitsim.pitevents.TestEvent2;
import dev.kyro.pitsim.placeholders.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PitSim extends JavaPlugin {

	public static PitSim INSTANCE;
	public static Economy VAULT = null;
	public static AData playerList;
	private BukkitAudiences adventure;

	public @NonNull BukkitAudiences adventure() {
		if(this.adventure == null) {
			throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
		}
		return this.adventure;
	}

	@Override
	public void onEnable() {

		INSTANCE = this;

		MapManager.onStart();

		adventure = BukkitAudiences.create(this);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			BossBarManager bm = new BossBarManager();
			PlayerManager.bossBars.put(onlinePlayer, bm);
		}

		if (!setupEconomy()) {
			AOutput.log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
		} else {
			AOutput.log(String.format("Could not find PlaceholderAPI! This plugin is required."));
			Bukkit.getPluginManager().disablePlugin(this);
		}

		boolean NoteBlockAPI = true;
		if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")){
			getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
			NoteBlockAPI = false;
			return;
		}


		registerPitEvents();
		PitEventManager.eventWait();

		registerUpgrades();
		registerMegastreaks();



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
		AHook.registerPlaceholder(new LeaderboardPlaceholder());
		AHook.registerPlaceholder(new LeaderboardPlaceholder2());
		AHook.registerPlaceholder(new LeaderboardPlaceholder3());
		AHook.registerPlaceholder(new LeaderboardPlaceholder4());
		AHook.registerPlaceholder(new LeaderboardPlaceholder5());
		AHook.registerPlaceholder(new LeaderboardPlaceholder6());
		AHook.registerPlaceholder(new LeaderboardPlaceholder7());
		AHook.registerPlaceholder(new LeaderboardPlaceholder8());
		AHook.registerPlaceholder(new LeaderboardPlaceholder9());
		AHook.registerPlaceholder(new LeaderboardPlaceholder10());


		loadConfig();

		ArcticAPI.configInit(this, "prefix", "error-prefix");
		playerList = new AData("player-list", "", false);

		CooldownManager.init();

		registerEnchants();
		registerCommands();
		registerListeners();
	}

	@Override
	public void onDisable() {

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			PitPlayer pitplayer = PitPlayer.getPitPlayer(onlinePlayer);
			if(NonManager.getNon(onlinePlayer) != null) continue;
			FileConfiguration playerData = APlayerData.getPlayerData(onlinePlayer);
			playerData.set("level", pitplayer.playerLevel);
			playerData.set("playerkills", pitplayer.playerKills);
			playerData.set("xp", pitplayer.remainingXP);
			playerData.set("ubersleft", pitplayer.dailyUbersLeft);
			playerData.set("ubercooldown", pitplayer.uberReset);
			APlayerData.savePlayerData(onlinePlayer);
		}

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
	}

	private void registerEnchants() {
		EnchantManager.registerEnchant(new aCPLEnchant());
		EnchantManager.registerEnchant(new JewelHunter());

//		EnchantManager.registerEnchant(new ThePunch());
		EnchantManager.registerEnchant(new Billionaire());
		EnchantManager.registerEnchant(new Gamble());
		EnchantManager.registerEnchant(new Executioner());
		EnchantManager.registerEnchant(new ComboPerun());
		EnchantManager.registerEnchant(new ComboDamage());
		EnchantManager.registerEnchant(new ComboHeal());
		EnchantManager.registerEnchant(new Punisher());
		EnchantManager.registerEnchant(new KingBuster());
//		EnchantManager.registerEnchant(new Bruiser());
		EnchantManager.registerEnchant(new BeatTheSpammers());
		EnchantManager.registerEnchant(new Sharp());
		EnchantManager.registerEnchant(new Crush());
		EnchantManager.registerEnchant(new SpeedyHit());
		EnchantManager.registerEnchant(new ComboSwift());
		EnchantManager.registerEnchant(new DiamondStomp());
		EnchantManager.registerEnchant(new BulletTime());
		EnchantManager.registerEnchant(new Healer());
		EnchantManager.registerEnchant(new Duelist());
		EnchantManager.registerEnchant(new ComboStun());
		EnchantManager.registerEnchant(new GoldAndBoosted());
		EnchantManager.registerEnchant(new PainFocus());
		EnchantManager.registerEnchant(new Shark());
		EnchantManager.registerEnchant(new XpBump());
		EnchantManager.registerEnchant(new Sweaty());

		EnchantManager.registerEnchant(new MegaLongBow());
		EnchantManager.registerEnchant(new Volley());
		EnchantManager.registerEnchant(new Chipping());
		EnchantManager.registerEnchant(new Telebow());
		EnchantManager.registerEnchant(new Robinhood());
		EnchantManager.registerEnchant(new Fletching());
		EnchantManager.registerEnchant(new PushComesToShove());
		EnchantManager.registerEnchant(new Wasp());
		EnchantManager.registerEnchant(new SprintDrain());
		EnchantManager.registerEnchant(new BottomlessQuiver());
		EnchantManager.registerEnchant(new Parasite());
		EnchantManager.registerEnchant(new LuckyShot());
		EnchantManager.registerEnchant(new Pullbow());
		EnchantManager.registerEnchant(new Explosive());
		EnchantManager.registerEnchant(new FasterThanTheirShadows());
		EnchantManager.registerEnchant(new PinDown());

		EnchantManager.registerEnchant(new Solitude());
		EnchantManager.registerEnchant(new NotGladiator());
		EnchantManager.registerEnchant(new DiamondAllergy());
		EnchantManager.registerEnchant(new FractionalReserve());
		EnchantManager.registerEnchant(new Protection());
		EnchantManager.registerEnchant(new Hearts());
		EnchantManager.registerEnchant(new Prick());
		EnchantManager.registerEnchant(new RingArmor());
//		EnchantManager.registerEnchant(new PitBlob());
//		EnchantManager.registerEnchant(new WolfPack());
		EnchantManager.registerEnchant(new Peroxide());
		EnchantManager.registerEnchant(new NewDeal());
		EnchantManager.registerEnchant(new HeighHo());
		EnchantManager.registerEnchant(new GoldenHeart());
		EnchantManager.registerEnchant(new RetroGravityMicrocosm());
		EnchantManager.registerEnchant(new Mirror());
		EnchantManager.registerEnchant(new LastStand());
		EnchantManager.registerEnchant(new Booboo());
		EnchantManager.registerEnchant(new CriticallyFunky());
		EnchantManager.registerEnchant(new GottaGoFast());
		EnchantManager.registerEnchant(new Electrolytes());
		EnchantManager.registerEnchant(new CounterOffensive());
		EnchantManager.registerEnchant(new Stereo());

//		Resource Enchants
		EnchantManager.registerEnchant(new Moctezuma());
		EnchantManager.registerEnchant(new GoldBump());
		EnchantManager.registerEnchant(new GoldBoost());

//		After all
		EnchantManager.registerEnchant(new Regularity());
		EnchantManager.registerEnchant(new Lifesteal());
	}

	private void registerUpgrades() {

		PerkManager.registerUpgrade(new NoPerk());
		PerkManager.registerUpgrade(new Vampire());
		PerkManager.registerUpgrade(new Dirty());
		PerkManager.registerUpgrade(new StrengthChaining());
		PerkManager.registerUpgrade(new Gladiator());
		PerkManager.registerUpgrade(new Thick());
		PerkManager.registerUpgrade(new AssistantToTheStreaker());
	}

	private void registerMegastreaks() {

		PerkManager.registerMegastreak(new Overdrive(null));
		PerkManager.registerMegastreak(new Highlander(null));
		PerkManager.registerMegastreak(new Uberstreak(null));
		PerkManager.registerMegastreak(new NoMegastreak(null));
		PerkManager.registerMegastreak(new Beastmode(null));
	}

	private void registerPitEvents() {
		PitEventManager.registerPitEvent(new TestEvent());
		PitEventManager.registerPitEvent(new TestEvent2());
	}

	private void registerCommands() {

//		ABaseCommand marketCommand = new MarketCommand("market");
//		marketCommand.registerCommand(new ListCommand("list"));
//		marketCommand.registerCommand(new AuctionCommand("ah"));

//		getCommand("atest").setExecutor(new ATestCommand());
		getCommand("atest").setExecutor(new ATestCommand());
		getCommand("oof").setExecutor(new OofCommand());
		getCommand("perks").setExecutor(new PerkCommand());
		getCommand("non").setExecutor(new NonCommand());
		getCommand("enchant").setExecutor(new EnchantCommand());
		getCommand("fresh").setExecutor(new FreshCommand());
		getCommand("show").setExecutor(new ShowCommand());
		getCommand("jewel").setExecutor(new JewelCommand());
		getCommand("enchants").setExecutor(new EnchantListCommand());
		getCommand("setkills").setExecutor(new SetKillCommand());
		getCommand("donator").setExecutor(new DonatorCommand());
		getCommand("ks").setExecutor(new KsCommand());
		getCommand("bounty").setExecutor(new BountyCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("changemap").setExecutor(new ChangeMapCommand());
		getCommand("crategive").setExecutor(new CrateGiveCommand());
//		getCommand("togglestereo").setExecutor(new ToggleStereoCommand());
	}

	private void registerListeners() {

//		KarhuAPI.getEventRegistry().addListener(new BypassManager());
		getServer().getPluginManager().registerEvents(new DamageManager(), this);
//		getServer().getPluginManager().registerEvents(new NonManager(), this);
		getServer().getPluginManager().registerEvents(new PlayerManager(), this);
		getServer().getPluginManager().registerEvents(new ChatManager(), this);
		getServer().getPluginManager().registerEvents(new DamageIndicator(), this);
//		getServer().getPluginManager().registerEvents(new MarketManager(), this);
		getServer().getPluginManager().registerEvents(new ItemManager(), this);
		getServer().getPluginManager().registerEvents(new CombatManager(), this);
		getServer().getPluginManager().registerEvents(new SpawnManager(), this);
		getServer().getPluginManager().registerEvents(new ItemRename(), this);
		getServer().getPluginManager().registerEvents(new EnderChestManager(), this);

	}

	private void loadConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		VAULT = rsp.getProvider();
		return VAULT != null;
	}
}
