package dev.kyro.pitsim.pitevents;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.controllers.objects.PitEvent;
import dev.kyro.pitsim.enums.GameMap;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Juggernaut extends PitEvent {
    public static Juggernaut INSTANCE;

    public static Player juggernaut;
    public int swordSlot;
    public static Boolean eventIsActive = false;
	public static List<Player> respawningPlayers = new ArrayList<>();
    public Boolean isStartingPeriod = false;
    public Map<Player, Integer> movements = new HashMap<>();
	public static Map<Player, Integer> juggernautDamage = new HashMap<>();
	public static int juggernautKills = 0;
	public static Boolean juggernautKilled = false;

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
	    }
    }

    @Override
    public void end() {
	    for(Player player : Bukkit.getOnlinePlayers()) {
		    player.teleport(MapManager.getPlayerSpawn());
	    }
    	eventIsActive = false;
        getTopThree();
        juggernaut = null;
        movements.clear();
        juggernautDamage.clear();
        juggernautKills = 0;
        juggernautKilled = false;
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
	    Location Desertlocation2 = new Location(Bukkit.getWorld("pit"), -153, 44, 233, -123, 5);
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
    public void onMove(PlayerMoveEvent event) {
    	if(isStartingPeriod) {
    		if(movements.containsKey(event.getPlayer())) movements.put(event.getPlayer(), movements.get(event.getPlayer()) + 1);
    		else movements.put(event.getPlayer(), 1);
	    }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(PitEventManager.activeEvent != this) return;
        if(event.getWhoClicked() == juggernaut && event.getSlot() == swordSlot) event.setCancelled(true);
        if(event.getWhoClicked() != juggernaut && event.getSlot() == 9) event.setCancelled(true);

    }

    @EventHandler
    public void onHeal(HealEvent event) {
        if(event.player != juggernaut) return;

        event.multipliers.add(0D);
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
			int damage = (int) (attackEvent.getFinalDamageIncrease() / 2);
			if(juggernautDamage.containsKey(attackEvent.attacker)) juggernautDamage.put(attackEvent.attacker, juggernautDamage.get(attackEvent.attacker) + damage);
			else juggernautDamage.put(attackEvent.attacker, damage);

			Bukkit.broadcastMessage(String.valueOf(juggernautDamage.get(attackEvent.attacker)));
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
    	if(!eventIsActive) return;
    	if(killEvent.dead != juggernaut) {
    		respawningPlayers.add(killEvent.dead);
    		respawn(killEvent.dead);
	    } else {
    		juggernautKilled = true;
    		PitEventManager.endEvent(this);
	    }
	}


    public void pickJuggernaut() throws Exception {
	    List<Player> keysAsArray = new ArrayList<>(movements.keySet());
	    Random r = new Random();
	    Player randomPlayer = (Player) movements.keySet().toArray()[r.nextInt(keysAsArray.size())];

	    makeJuggernaut(randomPlayer);
    }

    public void makeJuggernaut(Player player) throws Exception {
    	if(movements.get(player) < 100) {
    		pickJuggernaut();
    		return;
	    }

        juggernaut = player;
        ItemStack freshSword = FreshCommand.getFreshItem("sword");
        ItemStack updatedItem1 = EnchantManager.addEnchant(freshSword, EnchantManager.getEnchant("bill"), 10, false);
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
        if(!Misc.isAirOrNull(slotItem))AUtil.giveItemSafely(player, slotItem);

        player.setMaxHealth(40);
        player.setHealth(player.getMaxHealth());
        player.teleport(getLocation("JuggernautSpawn"));
    }

    public void setCompass(Player player) {
    	if(player == juggernaut) return;
            ItemStack compass = new ItemStack(Material.COMPASS);
            ItemMeta compassMeta = compass.getItemMeta();
            compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lJUGGERNAUT LOCATION"));
            compass.setItemMeta(compassMeta);

            int compassSlot = 9;
            ItemStack slotItem = player.getInventory().getItem(9);
            player.getInventory().setItem(compassSlot, compass);
            if(!Misc.isAirOrNull(slotItem))AUtil.giveItemSafely(player, slotItem);

	    if(movements.get(player) < 100) return;
	    player.teleport(getLocation("PlayerSpawn"));
    }

	public void getTopThree() {
		Map<String, Integer> stringPlayers = new HashMap<>();
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
				player1 = player;
			}
			if(player.getDisplayName().equalsIgnoreCase(playerTwo)) {
				 messageTwo = ChatColor.translateAlternateColorCodes('&',
						 "   &e&l#2 %luckperms_prefix%" + player.getDisplayName() + " &ewith &c" + juggernautDamage.get(player) + "&c\u2764");
				player2 = player;
			}
			if(player.getDisplayName().equalsIgnoreCase(playerThree)) {
				 messageThree = ChatColor.translateAlternateColorCodes('&',
						 "   &e&l#3 %luckperms_prefix%" + player.getDisplayName() + " &ewith &c" + juggernautDamage.get(player) + "&c\u2764");
				player3 = player;
			}
		}

		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&m------------------------"));
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lPIT EVENT ENDED: " +
				this.color + "" + ChatColor.BOLD + this.getName().toUpperCase(Locale.ROOT) + "&6&l!"));

		if(juggernautKilled) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lJuggernaut Status: &c&lDEAD"));
		else Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lJuggernaut status: &a&lALIVE &7(&c" + juggernaut.getHealth() + "&c\u2764&7)"));

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
			else onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lYou: &c" + juggernautDamage.get(onlinePlayer) + "&c\u2764"));
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

    static {
	    new BukkitRunnable() {
		    @Override
		    public void run() {
		    	if(eventIsActive) {
				    for(Player player : Bukkit.getOnlinePlayers()) {
					    player.setCompassTarget(juggernaut.getLocation());
				    }
				    Misc.applyPotionEffect(juggernaut, PotionEffectType.SLOW, 40, 1, false, false);
			    }
		    }
	    }.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
    }
}
