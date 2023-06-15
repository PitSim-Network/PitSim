package net.pitsim.spigot.auction;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.holograms.Hologram;
import net.pitsim.spigot.holograms.RefreshMode;
import net.pitsim.spigot.holograms.ViewMode;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AuctionDisplays implements Listener {

	public static Location[] pedestalLocations = new Location[3];
	public static UUID[] pedestalItems = new UUID[3];
	public static UUID[] pedestalArmorStands = new UUID[3];

	public static Hologram[] bidInfoHolograms = new Hologram[3];

	public static Hologram timerHologram;
	public static Location timerLocation = new Location(MapManager.getDarkzone(), 178.5, 52, -1009.5);

	public static NPC[] clickables = new NPC[3];

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;
				if(!hasPlayers(pedestalLocations[0])) return;

				for(int i = 0; i < 3; i++) {

					Item item = getItem(pedestalItems[i]);
					if(item != null) item.teleport(pedestalLocations[i]);

					for(Entity nearbyEntity : MapManager.getDarkzone().getNearbyEntities(pedestalLocations[i], 1, 1, 1)) {
						if(!(nearbyEntity instanceof Item)) continue;
						if(nearbyEntity.getUniqueId().equals(pedestalItems[i])) continue;
						nearbyEntity.remove();
					}
				}

				for(int i = 0; i < clickables.length; i++) {
					NPC clickable = clickables[i];

					clickable.spawn(pedestalLocations[i]);
					clickable.teleport(pedestalLocations[i], PlayerTeleportEvent.TeleportCause.UNKNOWN);
					if(clickable.isSpawned())
						((LivingEntity) clickable.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
	}

	public static void onStart() {
		pedestalLocations[0] = new Location(MapManager.getDarkzone(), 172.5, 52, -1012.5);
		pedestalLocations[1] = new Location(MapManager.getDarkzone(), 178.5, 52, -1016.5);
		pedestalLocations[2] = new Location(MapManager.getDarkzone(), 184.5, 52, -1012.5);
		for(Location pedestalLocation : pedestalLocations) {
			pedestalLocation.getChunk().load();
		}

		timerHologram = new Hologram(timerLocation, ViewMode.ALL, RefreshMode.AUTOMATIC_FAST) {

			@Override
			public List<String> getStrings(Player player) {
				List<String> strings = new ArrayList<>();
				if(AuctionManager.haveAuctionsEnded())
					strings.add("&eEnding Soon");
				else
					strings.add("&eTime Left: &f" + AuctionManager.getRemainingTime());
				return strings;
			}
		};

		for(int i = 0; i < 3; i++) {

			NPCRegistry registry = CitizensAPI.getNPCRegistry();
			NPC npc = registry.createNPC(EntityType.MAGMA_CUBE, "");
			npc.spawn(pedestalLocations[i]);
			clickables[i] = npc;


			int finalI = i;
			bidInfoHolograms[i] = new Hologram(pedestalLocations[finalI].clone().add(0, 2, 0)) {

				@Override
				public List<String> getStrings(Player player) {
					AuctionItem auctionItem = AuctionManager.auctionItems[finalI];
					int highestBid = auctionItem == null ? 0 : auctionItem.getHighestBid();
					UUID highestBidder = auctionItem == null ? null : auctionItem.getHighestBidder();


					List<String> strings = new ArrayList<>();
					if(highestBidder != null)
						strings.add("&eHighest Bid: &f" + highestBid + " Tainted Souls");
					else
						strings.add("&eStarting Bid: &f" + highestBid + " Tainted Souls");
					
					String message = highestBidder == null ? "No One!" : Bukkit.getOfflinePlayer(highestBidder).getName();
					strings.add("&eBy: &6" + message);
					strings.add("&eRight-Click to Bid!");

					return strings;
				}
			};
		}
	}

	public static void updateHolograms() {
		for(Hologram bidInfoHologram : bidInfoHolograms) {
			if(bidInfoHologram != null) bidInfoHologram.updateHologram();
		}
	}

	public static void showItems() {

		for(int i = 0; i < pedestalLocations.length; i++) {
			Location pedestalLocation = pedestalLocations[i];
			ItemStack dropItem = AuctionManager.auctionItems[i].item.item.clone();
			ItemMeta meta = dropItem.getItemMeta();
			meta.setDisplayName(UUID.randomUUID().toString());
			dropItem.setItemMeta(meta);

			pedestalItems[i] = pedestalLocation.getWorld().dropItem(pedestalLocation, dropItem).getUniqueId();

			ArmorStand stand = (ArmorStand) pedestalLocation.getWorld().spawnEntity(pedestalLocation.clone().subtract(0, 1.33, 0), EntityType.ARMOR_STAND);
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setHelmet(new ItemStack(Material.GLASS));
			stand.setCustomName(AuctionManager.auctionItems[i].item.itemName);
			stand.setRemoveWhenFarAway(false);
			stand.setCustomNameVisible(true);
			pedestalArmorStands[i] = stand.getUniqueId();

		}
	}

	@EventHandler
	public void onUnload(ChunkUnloadEvent event) {
		if(event.getChunk().getWorld() != MapManager.getDarkzone()) return;
		for(Location pedestalLocation : pedestalLocations) {
			if(pedestalLocation.getChunk().equals(event.getChunk())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onRightClick(NPCRightClickEvent event) {
		for(int i = 0; i < clickables.length; i++) {
			NPC clickable = clickables[i];

			if(clickable.getId() == event.getNPC().getId()) {
				BidGUI bidGUI = new BidGUI(event.getClicker(), i);
				bidGUI.open();
			}
		}
	}

	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		if(event.getPlayer().getWorld() != MapManager.getDarkzone()) return;

		if(pedestalLocations[0] == null) return;

		if(pedestalLocations[0].distance(event.getPlayer().getLocation()) < 50) {
			event.setCancelled(true);
		}

		for(UUID pedestalItem : pedestalItems) {
			if(pedestalItem == null) continue;

			if(pedestalItem.equals(event.getItem().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDespawn(ItemDespawnEvent event) {
		if(!(event.getEntity() instanceof ArmorStand)) return;

		for(UUID pedestalItem : pedestalItems) {
			if(pedestalItem.equals(event.getEntity().getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAttack(AttackEvent.Pre event) {
		if(!(event.getDefender() instanceof ArmorStand)) return;

		List<UUID> stands = new ArrayList<>(Arrays.asList(pedestalArmorStands));
		for(UUID armorStand : stands) {
			if(armorStand.equals(event.getDefender().getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof ArmorStand)) return;

		for(UUID armorStand : pedestalArmorStands) {
			if(armorStand.equals(event.getEntity().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	public static ArmorStand getStand(UUID uuid) {
		for(Entity entity : MapManager.getDarkzone().getEntities()) {
			if(!(entity instanceof ArmorStand)) continue;
			if(entity.getUniqueId().equals(uuid)) return (ArmorStand) entity;
		}
		return null;
	}

	public static Item getItem(UUID uuid) {
		for(Entity entity : MapManager.getDarkzone().getEntities()) {
			if(!(entity instanceof Item)) continue;
			if(entity.getUniqueId().equals(uuid)) return (Item) entity;
		}
		return null;
	}

	public static boolean hasPlayers(Location location) {
		for(Entity entity : location.getWorld().getNearbyEntities(location, 50, 50, 50)) {
			if(entity instanceof Player) return true;
		}
		return false;
	}

	public static void onDisable() {

		for(UUID pedestalArmorStand : pedestalArmorStands) {
			ArmorStand stand = getStand(pedestalArmorStand);
			if(stand != null) stand.remove();
		}

		for(UUID pedestalItem : pedestalItems) {
			Item item = getItem(pedestalItem);
			if(item != null) item.remove();
		}
	}

}
