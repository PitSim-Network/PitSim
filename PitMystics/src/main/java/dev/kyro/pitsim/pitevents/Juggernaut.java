package dev.kyro.pitsim.pitevents;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.GameMap;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Juggernaut extends PitEvent {
    public static Juggernaut INSTANCE;

    public static Player juggernaut;
    public int swordSlot;
    public ItemStack sword;
    public static Boolean eventIsActive = false;
	public static List<Player> respawningPlayers = new ArrayList<>();
    public Boolean isStartingPeriod = false;
    public Map<Player, Integer> movements = new HashMap<>();
	public static Map<Player, Integer> juggernautDamage = new HashMap<>();
	public static int juggernautKills = 0;
	public static Boolean juggernautKilled = false;
	public int lastHealthUpdate = 100;
	public Map<UUID, Cooldown> cooldowns = new HashMap<>();
	public ItemStack pants = null;

    public Juggernaut() {
        super("Juggernaut", 5, true, "JUGGERNAUT", ChatColor.GOLD);
        INSTANCE = this;
    }

    @Override
    public String getName() {return "Juggernaut";}

    @Override
    public void prepare() {
    	isStartingPeriod = true;
    }

    @Override
    public void start() throws Exception {
    	PitEventManager.activeEvent = this;
    	eventIsActive = true;
    	isStartingPeriod = false;
    	pickJuggernaut();
	    for(Player player : Bukkit.getOnlinePlayers()) {
		    setCompass(player);
		    player.playEffect(MapManager.getMid(), Effect.RECORD_PLAY, Material.RECORD_8.getId());
	    }
    }

    @Override
    public void end() {
	    for(Player player : Bukkit.getOnlinePlayers()) {
//		    for(ItemStack itemStack : player.getInventory()) {
//			    if(!Misc.isAirOrNull(itemStack) && itemStack.getType().equals(Material.COMPASS)) player.getInventory().remove(itemStack);
//		    }
		    if(!AFKManager.AFKPlayers.contains(player)) player.teleport(MapManager.getPlayerSpawn());
		    player.setGameMode(GameMode.SURVIVAL);
	    }
	    juggernaut.getInventory().remove(sword);
	    if(Misc.isAirOrNull(pants)) juggernaut.getInventory().setLeggings(new ItemStack(Material.AIR));
	    else juggernaut.getInventory().setLeggings(pants);
	    PlayerManager.removeIllegalItems(juggernaut);
	    PitPlayer pitPlayer = PitPlayer.getPitPlayer(juggernaut);
	    String message = "%luckperms_prefix%";
	    pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
    	eventIsActive = false;
        juggernaut = null;
        movements.clear();
        juggernautDamage.clear();
        juggernautKills = 0;
        juggernautKilled = false;
        lastHealthUpdate = 100;
        pants = null;
	    PitEventManager.activeEvent = null;
    }

    public Location getLocation(String location) {
    	if(location == "JuggernautSpawn") {
    		return MapManager.getMid();
	    }
    	if(location == "PlayerSpawn") return randomPlayerSpawn();

    	return null;
    }

    public Location randomPlayerSpawn() {
    	List<Location> desertLocations = new ArrayList<>();
    	Location Desertlocation1 = new Location(Bukkit.getWorld("pit"), -101, 44, 168, 14, 3);
	    Location Desertlocation2 = new Location(Bukkit.getWorld("pit"), -144, 44, 233, -123, 5);
	    Location Desertlocation3 = new Location(Bukkit.getWorld("pit"), -98, 44, 233, 144, 0);
	    desertLocations.add(Desertlocation1);
	    desertLocations.add(Desertlocation2);
	    desertLocations.add(Desertlocation3);
	    List<Location> starwarsLocations = new ArrayList<>();
	    Location Starwarslocation1 = new Location(Bukkit.getWorld("pit"), -129, 10, 715, -90, 6);
	    Location Starwarslocation2 = new Location(Bukkit.getWorld("pit"), -96, 7, 685, 2, 4);
	    Location Starwarslocation3 = new Location(Bukkit.getWorld("pit"), -97, 7, 746, -180, 0);
	    starwarsLocations.add(Starwarslocation1);
	    starwarsLocations.add(Starwarslocation2);
	    starwarsLocations.add(Starwarslocation3);

	    Random rand = new Random();
	    if(MapManager.map.equals(GameMap.DESERT)) {
		    return desertLocations.get(rand.nextInt(desertLocations.size()));
	    }
	    if(MapManager.map.equals(GameMap.STARWARS)) {
		    return starwarsLocations.get(rand.nextInt(starwarsLocations.size()));
	    }

    	return null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
	    if(PitEventManager.activeEvent != this) return;
    	respawningPlayers.add(event.getPlayer());
    	respawn(event.getPlayer());
    	setCompass(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
    	if(event.getPlayer() == juggernaut && eventIsActive) {
		    juggernautKilled = true;
		    event.getPlayer().getInventory().remove(sword);
		    if(Misc.isAirOrNull(pants)) juggernaut.getInventory().setLeggings(new ItemStack(Material.AIR));
		    else juggernaut.getInventory().setLeggings(pants);
		    pants = null;
		    PlayerManager.removeIllegalItems(event.getPlayer());
		    event.getPlayer().setHealth(juggernaut.getMaxHealth());
		    getTopThree();
		    explosion(event.getPlayer());

		    for(Player player : Bukkit.getOnlinePlayers()) {
			    Sounds.JUGGERNAUT_END.play(player);
		    }

		    new BukkitRunnable() {
			    @Override
			    public void run() {
				    endEvent();
			    }
		    }.runTaskLater(PitSim.INSTANCE, 200L);

	    }
    	if(event.getPlayer() != juggernaut && eventIsActive) {
//		    for(ItemStack itemStack : event.getPlayer().getInventory()) {
//			    if(!Misc.isAirOrNull(itemStack) && itemStack.getType().equals(Material.COMPASS)) event.getPlayer().getInventory().remove(itemStack);
//		    }
		    event.getPlayer().teleport(MapManager.getPlayerSpawn());
		    event.getPlayer().setGameMode(GameMode.SURVIVAL);
	    }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

    	if(isStartingPeriod) {
    		if(movements.containsKey(event.getPlayer())) movements.put(event.getPlayer(), movements.get(event.getPlayer()) + 1);
    		else movements.put(event.getPlayer(), 1);
	    }
	    if(PitEventManager.activeEvent != this) return;
    	if(event.getPlayer() != juggernaut) return;
    	if(event.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
	    double distance = MapManager.getMid().distance(event.getPlayer().getLocation());
	    if(distance > 16) {

		    Cooldown cooldown = getCooldown(juggernaut, 40);
		    if(cooldown.isOnCooldown()) return; else cooldown.reset();

		    Misc.sendTitle(event.getPlayer(), "&cYou cannot leave mid!", 20);
		    Sounds.WARNING_LOUD.play(event.getPlayer());

		    Vector dirVector = MapManager.getMid().toVector().subtract(event.getPlayer().getLocation().toVector()).setY(0);
		    Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(2.5).add(dirVector.clone().multiply(0.03));
		    event.getPlayer().setVelocity(pullVector.multiply(0.5));
	    }
    }

	public Cooldown getCooldown(Player player, int time) {

		if(cooldowns.containsKey(player.getUniqueId())) {
			Cooldown cooldown = cooldowns.get(player.getUniqueId());
			cooldown.initialTime = time;
			return cooldown;
		}

		Cooldown cooldown = new Cooldown(time);
		cooldown.ticksLeft = 0;
		cooldowns.put(player.getUniqueId(), cooldown);
		return cooldown;
	}

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(PitEventManager.activeEvent != this) return;
        if(event.getAction().equals(InventoryAction.DROP_ONE_SLOT) || event.getAction().equals(InventoryAction.DROP_ALL_SLOT) || event.getAction().equals(InventoryAction.DROP_ALL_CURSOR) || event.getAction().equals(InventoryAction.DROP_ONE_CURSOR)) {
        	event.setCancelled(true);
	    }
        if(event.getWhoClicked() == juggernaut && event.getSlot() == swordSlot) event.setCancelled(true);
        if(event.getWhoClicked() != juggernaut && event.getSlot() == 8) event.setCancelled(true);

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
	    if(PitEventManager.activeEvent != this) return;
	    if(event.getPlayer() == juggernaut && event.getPlayer().getInventory().getHeldItemSlot() == swordSlot) event.setCancelled(true);
	    if(event.getPlayer() != juggernaut && event.getPlayer().getInventory().getHeldItemSlot() == 8) event.setCancelled(true);
    }

    @EventHandler
    public void onHeal(HealEvent event) {
	    if(PitEventManager.activeEvent != this) return;
        if(event.player != juggernaut) return;

        event.multipliers.add(0D);
    }

    @EventHandler
	public void onHeal(EntityRegainHealthEvent event) {
	    if(PitEventManager.activeEvent != this) return;
		if(!(event.getEntity() instanceof Player)) return;
		if(event.getEntity() != juggernaut) return;
		event.setCancelled(true);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCommandSend(PlayerCommandPreprocessEvent event) {

		if(event.getMessage().equalsIgnoreCase("/oof") && respawningPlayers.contains(event.getPlayer())) event.setCancelled(true);
		if(event.getMessage().equalsIgnoreCase("/spawn") && event.getPlayer() == juggernaut) {
		event.setCancelled(true);
		}
		if(event.getMessage().equalsIgnoreCase("/oof") && event.getPlayer() == juggernaut) {
			event.setCancelled(true);
		}
	}

    @EventHandler
    public void onPreAttack(AttackEvent.Pre attackEvent) {
    	if(PitEventManager.activeEvent != this) return;
    	if(attackEvent.attacker != juggernaut && attackEvent.defender != juggernaut) attackEvent.setCancelled(true);
    }

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(PitEventManager.activeEvent != this) return;
		if(attackEvent.defender == juggernaut && attackEvent.attacker != juggernaut) {
			int damage = (int) (attackEvent.getFinalDamageIncrease() / 6);
			if(juggernautDamage.containsKey(attackEvent.attacker)) juggernautDamage.put(attackEvent.attacker, juggernautDamage.get(attackEvent.attacker) + damage);
			else juggernautDamage.put(attackEvent.attacker, damage);

			if(juggernaut.getHealth() / 2 <= 50 && lastHealthUpdate > 50) {
				lastHealthUpdate = 50;
				sendTitle(ChatColor.RED + "50\u2764", color + "Juggernaut &chealth remaining!");
			}
			if(juggernaut.getHealth() / 2 <= 40 && lastHealthUpdate > 40) {
				lastHealthUpdate = 40;
				sendTitle(ChatColor.RED + "40\u2764", color + "Juggernaut &chealth remaining!");
			}
			if(juggernaut.getHealth() / 2 <= 30 && lastHealthUpdate > 30) {
				lastHealthUpdate = 30;
				sendTitle(ChatColor.RED + "30\u2764", color + "Juggernaut &chealth remaining!");
			}
			if(juggernaut.getHealth() / 2 <= 20 && lastHealthUpdate > 20) {
				lastHealthUpdate = 20;
				sendTitle(ChatColor.RED + "20\u2764", color + "Juggernaut &chealth remaining!");
			}
			if(juggernaut.getHealth() / 2 <= 10 && lastHealthUpdate > 10) {
				lastHealthUpdate = 10;
				sendTitle(ChatColor.RED + "10\u2764", color + "Juggernaut &chealth remaining!");
			}
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
    	if(!eventIsActive) return;
    	if(killEvent.killer == juggernaut) {
    		juggernautKills += 1;
    		String message = ChatColor.translateAlternateColorCodes('&', "%luckperms_prefix%" +
				    killEvent.dead.getDisplayName() + " &7was killed. The " + color + "Juggernaut &7now has &c" + juggernautKills + " &7total kills,");
    		Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(killEvent.dead, message));
	    }
    	if(killEvent.dead != juggernaut) {
    		respawningPlayers.add(killEvent.dead);
		    killEvent.dead.teleport(killEvent.dead.getLocation());
    		respawn(killEvent.dead);
	    } else {
    		juggernautKilled = true;
    		juggernaut.setGameMode(GameMode.SPECTATOR);
    		juggernaut.setHealth(juggernaut.getMaxHealth());
		    getTopThree();
		    explosion(killEvent.dead);
		    for(Player player : Bukkit.getOnlinePlayers()) {
				Sounds.JUGGERNAUT_END.play(player);
		    }

		    new BukkitRunnable() {
			    @Override
			    public void run() {
				    endEvent();
			    }
		    }.runTaskLater(PitSim.INSTANCE, 200L);

	    }
	}

	public void endEvent() {
		PitEventManager.majorEvent = false;
		PitEventManager.endEvent(this);
	}

	public void sendTitle(String title, String subtitle) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			Misc.sendTitle(player, title, 40);
			if(subtitle != null) Misc.sendSubTitle(player, subtitle, 40);
			Sounds.EVENT_PING.play(player);
		}
	}


    public void pickJuggernaut() throws Exception {
	    List<Player> keysAsArray = new ArrayList<>();

	    ArrayList<Player> allPlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
	    int random = new Random().nextInt(allPlayers.size());
	    Player picked = allPlayers.get(random);
		    makeJuggernaut(picked);
	    }

    public void makeJuggernaut(Player player) throws Exception {
    	if(AFKManager.AFKPlayers.contains(player)) {
    		pickJuggernaut();
    		return;
	    }
    	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

        juggernaut = player;
        ItemStack freshSword = FreshCommand.getFreshItem("sword");
        ItemStack updatedItem1 = EnchantManager.addEnchant(freshSword, EnchantManager.getEnchant("bill"), 5, false);
	    ItemStack updatedItem2 = EnchantManager.addEnchant(updatedItem1, EnchantManager.getEnchant("perun"), 5, false);
	    ItemStack updatedItem3 = EnchantManager.addEnchant(updatedItem2, EnchantManager.getEnchant("stomp"), 5, false);

        assert updatedItem3 != null;
        ItemMeta meta = updatedItem3.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "JUGGERNAUT SWORD");
        updatedItem3.setItemMeta(meta);

        int swordSlot = player.getInventory().getHeldItemSlot();
        ItemStack slotItem = player.getItemInHand();
        player.getInventory().setItem(swordSlot, updatedItem3);
        this.swordSlot = swordSlot;
        this.sword = updatedItem3;
        if(!Misc.isAirOrNull(slotItem))AUtil.giveItemSafely(player, slotItem, true);

	    ItemStack freshPants = FreshCommand.getFreshItem("orange");
	    ItemStack updatedPants1 = EnchantManager.addEnchant(freshPants, EnchantManager.getEnchant("prot"), 10, false);
	    ItemStack updatedPants2 = EnchantManager.addEnchant(updatedPants1, EnchantManager.getEnchant("cf"), 3, false);
	    ItemStack updatedPants3 = EnchantManager.addEnchant(updatedPants2, EnchantManager.getEnchant("mirror"), 3, false);

	    assert updatedPants3 != null;
	    ItemMeta pantsMeta = updatedPants3.getItemMeta();
	    pantsMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "JUGGERNAUT PANTS");
	    updatedPants3.setItemMeta(pantsMeta);

	    if(!Misc.isAirOrNull(player.getInventory().getLeggings())) this.pants = player.getInventory().getLeggings();
	    player.getInventory().setLeggings(updatedPants3);

        pitPlayer.updateMaxHealth();
        player.setHealth(player.getMaxHealth());
        player.teleport(getLocation("JuggernautSpawn"));

	    String message = "%luckperms_prefix%";
	    pitPlayer.prefix = color + "" + ChatColor.BOLD + "JUGGER &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
    }

    public void setCompass(Player player) {
//    	if(player == juggernaut) return;
//            ItemStack compass = new ItemStack(Material.COMPASS);
//            ItemMeta compassMeta = compass.getItemMeta();
//            compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lJUGGERNAUT LOCATION"));
//            compass.setItemMeta(compassMeta);
//
//            int compassSlot = 8;
//            ItemStack slotItem = player.getInventory().getItem(8);
//            player.getInventory().setItem(compassSlot, compass);
//            if(!Misc.isAirOrNull(slotItem))AUtil.giveItemSafely(player, slotItem);
//
//	    if(AFKManager.AFKPlayers.contains(player)) return;
//	    player.teleport(getLocation("PlayerSpawn"));
    }

	public void getTopThree() {
		Map<String, Integer> stringPlayers = new HashMap<>();
		Map<Player, Integer> renown = new HashMap<>();
		String playerOne = null;
		String playerTwo = null;
		String playerThree = null;
		String messageOne = null;
		String messageTwo = null;
		String messageThree = null;
		Player player1 = null;
		Player player2 = null;
		Player player3 = null;

		for(Map.Entry<Player, Integer> pair : juggernautDamage.entrySet()) {
			stringPlayers.put(pair.getKey().getName(), pair.getValue());

		}
		Map<String, Integer> sortedMap = LeaderboardManager.calculateEvent(stringPlayers);
		if(sortedMap.size() >= 1) {
			playerOne = (String) sortedMap.keySet().toArray()[0];
		}
		if(sortedMap.size() >= 2) {
			playerTwo = (String) sortedMap.keySet().toArray()[1];
		}
		if(sortedMap.size() >= 3) {
			playerThree = (String) sortedMap.keySet().toArray()[2];
		}


		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getDisplayName().equalsIgnoreCase(playerOne)) {
				messageOne = ChatColor.translateAlternateColorCodes('&',
						"   &e&l#1 %luckperms_prefix%" + player.getDisplayName() + " &ewith &c" + juggernautDamage.get(player) + "&c\u2764");
				if(renown.containsKey(player)) renown.put(player, renown.get(player) + 1);
				else renown.put(player, 1);
				if(Bukkit.getOnlinePlayers().size() >= 10 && UpgradeManager.hasUpgrade(player, "SELF_CONFIDENCE")) renown.put(player, renown.get(player) + 2);
				player1 = player;
			}
			if(player.getDisplayName().equalsIgnoreCase(playerTwo)) {
				 messageTwo = ChatColor.translateAlternateColorCodes('&',
						 "   &e&l#2 %luckperms_prefix%" + player.getDisplayName() + " &ewith &c" + juggernautDamage.get(player) + "&c\u2764");
				if(renown.containsKey(player)) renown.put(player, renown.get(player) + 1);
				else renown.put(player, 1);
				if(Bukkit.getOnlinePlayers().size() >= 10 && UpgradeManager.hasUpgrade(player, "SELF_CONFIDENCE")) renown.put(player, renown.get(player) + 1);
				player2 = player;
			}
			if(player.getDisplayName().equalsIgnoreCase(playerThree)) {
				 messageThree = ChatColor.translateAlternateColorCodes('&',
						 "   &e&l#3 %luckperms_prefix%" + player.getDisplayName() + " &ewith &c" + juggernautDamage.get(player) + "&c\u2764");
				if(renown.containsKey(player)) renown.put(player, renown.get(player) + 1);
				else renown.put(player, 1);
				if(Bukkit.getOnlinePlayers().size() >= 10 && UpgradeManager.hasUpgrade(player, "SELF_CONFIDENCE")) renown.put(player, renown.get(player) + 1);
				player3 = player;
			}
		}

		for(Player player : Bukkit.getOnlinePlayers()) {
			if(juggernaut == player) {
				if(!juggernautKilled) {
					if(renown.containsKey(player)) renown.put(player, renown.get(player) + 1);
					else renown.put(player, 3);
				}
			}
			if(juggernaut != player) {
				if(juggernautKilled) {
					if(renown.containsKey(player)) renown.put(player, renown.get(player) + 1);
					else renown.put(player, 1);
				}
			}
		}

		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&m------------------------"));
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lPIT EVENT ENDED: " +
				this.color + "" + ChatColor.BOLD + this.getName().toUpperCase(Locale.ROOT) + "&6&l!"));
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(renown.get(player) == null) player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYour rewards: &cNONE"));
			else  {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYour rewards: &e+" + renown.get(player) + " Renown"));
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				pitPlayer.renown += renown.get(player);
				FileConfiguration playerData = APlayerData.getPlayerData(player);
				playerData.set("renown", pitPlayer.renown);
				APlayerData.savePlayerData(player);
			}
		}


		if(juggernautKilled) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lJuggernaut Status: &c&lDEAD"));
		else Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lJuggernaut status: &a&lALIVE &7(&c" + (int) juggernaut.getHealth() / 2+ "&c\u2764&7)"));

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(juggernautKilled) {
				if(onlinePlayer == juggernaut) onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSurvival bonus: &c&lFAILED! &7You were killed by the players."));
				else onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSlayer bonus: &a&lSUCCESS! &7The Juggernaut was killed!"));
			}
			if(!juggernautKilled) {
				if(onlinePlayer == juggernaut) onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSurvival bonus: &a&lSUCCESS! &7You survived the players!"));
				else onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lSlayer bonus: &c&lFAILED! &7The Juggernaut was not killed."));
			}


			if(onlinePlayer == juggernaut) onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYou: &a" + juggernautKills + " &aPlayers killed"));
			else if(juggernautDamage.get(onlinePlayer) != null) onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYou: &c" + juggernautDamage.get(onlinePlayer) + "&c\u2764"));
			else onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYou: 0&c\u2764"));
		}
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lTop players:"));
		if(messageOne != null) Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player1, messageOne));
		if(messageTwo != null) Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player2, messageTwo));
		if(messageThree != null) Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player3, messageThree));
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&m------------------------"));
	}

	public void respawn(Player player) {

		player.setGameMode(GameMode.SPECTATOR);
		Misc.sendTitle(player, "&cYOU DIED!", 40);
		Misc.sendSubTitle(player, "&eYou will respawn in &c5 &eseconds!", 20);
		new BukkitRunnable() {
			@Override
			public void run() {
				Misc.sendTitle(player, "&cYOU DIED!", 40);
				Misc.sendSubTitle(player, "&eYou will respawn in &c4 &eseconds!", 20);

				new BukkitRunnable() {
					@Override
					public void run() {
						Misc.sendTitle(player, "&cYOU DIED!", 40);
						Misc.sendSubTitle(player, "&eYou will respawn in &c3 &eseconds!", 20);

						new BukkitRunnable() {
							@Override
							public void run() {
								Misc.sendTitle(player, "&cYOU DIED!", 40);
								Misc.sendSubTitle(player, "&eYou will respawn in &c2 &eseconds!", 20);

								new BukkitRunnable() {
									@Override
									public void run() {
										Misc.sendTitle(player, "&cYOU DIED!", 40);
										Misc.sendSubTitle(player, "&eYou will respawn in &c1 &esecond!", 20);

										new BukkitRunnable() {
											@Override
											public void run() {
												player.setGameMode(GameMode.SURVIVAL);
												player.teleport(getLocation("PlayerSpawn"));
												respawningPlayers.remove(player);
											}
										}.runTaskLater(PitSim.INSTANCE, 20L);
									}
								}.runTaskLater(PitSim.INSTANCE, 20L);
							}
						}.runTaskLater(PitSim.INSTANCE, 20L);
					}
				}.runTaskLater(PitSim.INSTANCE, 20L);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L);


	}

	public void explosion(Player player) {
		List<Player> explosionPlayers = new ArrayList<>();
		List<Entity> nearbyPlayers = player.getNearbyEntities(10, 10, 10);
		for(Entity nearbyPlayer : nearbyPlayers) {
			if(nearbyPlayer instanceof Player) explosionPlayers.add((Player) nearbyPlayer);
		}

		for(Player explosionPlayer : explosionPlayers) {
			Vector force = explosionPlayer.getLocation().toVector().subtract(player.getLocation().toVector())
					.setY(1).normalize().multiply(5);
			player.setVelocity(force);
		}
		Sounds.JUGGERNAUT_EXPLOSION.play(player.getLocation());
		player.getLocation().getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_HUGE,  200, 200);
	}

    static {
	    new BukkitRunnable() {
		    @Override
		    public void run() {
		    	if(eventIsActive) {
//				    for(Player player : Bukkit.getOnlinePlayers()) {
//					    player.setCompassTarget(juggernaut.getLocation());
//				    }
				    int resistanceLevel = 0;
				    if(AFKManager.onlineActivePlayers > 5 && AFKManager.onlineActivePlayers <= 10) resistanceLevel = 1;
				    if(AFKManager.onlineActivePlayers > 10) resistanceLevel = 2;

				    Misc.applyPotionEffect(juggernaut, PotionEffectType.DAMAGE_RESISTANCE, 40, resistanceLevel, false, false);
				    juggernaut.getWorld().spigot().playEffect(juggernaut.getLocation(),
						    Effect.FLAME, 0, 2, 0F, 1F, 0F,0.08F, 100, 6);
			    }
		    }
	    }.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
    }
}
