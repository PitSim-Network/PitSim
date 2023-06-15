package net.pitsim.spigot.brewing;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.battlepass.quests.BrewPotionsQuest;
import net.pitsim.spigot.brewing.objects.BrewingAnimation;
import net.pitsim.spigot.brewing.objects.BrewingSession;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.events.PitQuitEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class BrewingManager implements Listener {

	public static List<BrewingAnimation> brewingAnimations = new ArrayList<>();
	public static List<ArmorStand> brewingStands = new ArrayList<>();
	public static List<BrewingSession> brewingSessions = new ArrayList<>();
	public static List<Player> pausePlayers = new ArrayList<>();
	public static ArmorStand spinStand;
	public static int i = 0;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				BrewingAnimation anim = brewingAnimations.get(0);

				for(Entity nearbyEntity : anim.location.getWorld().getNearbyEntities(anim.location, 25, 25, 25)) {
					List<ArmorStand> destroyStands = new ArrayList<>(anim.personalStands);
					if(!(nearbyEntity instanceof Player)) continue;
					Player player = (Player) nearbyEntity;
					if(CitizensAPI.getNPCRegistry().isNPC(player)) continue;

					if(anim.players.contains(player)) {
						destroyStands.remove(anim.cancelStands.get(player));
						destroyStands.remove(anim.confirmStands.get(player));
						destroyStands.remove(anim.identityStands.get(player));
						destroyStands.remove(anim.potencyStands.get(player));
						destroyStands.remove(anim.durationStands.get(player));
						destroyStands.remove(anim.brewingTimeStands.get(player));
					}

					for(ArmorStand destroyStand : destroyStands) {
						PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(destroyStand, anim.location));
						((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyPacket);
					}

					if(anim.players.contains(player)) continue;
					if(pausePlayers.contains(player)) continue;
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					String[] text = new String[5];
					text[0] = "&e&lActively Brewing Potions";
					for(int i = 0; i < pitPlayer.brewingSessions.size(); i++) {
						BrewingSession session = getBrewingSession(player, i + 1);
						if(session != null) {
							int addTicks = (105 - session.reduction.getBrewingReductionMinutes()) * 60 * 20;
							int timeLeft = (int) ((int) (((session.startTime / 1000) * 20) + addTicks) - (((System.currentTimeMillis() / 1000) * 20)));
							if(timeLeft < 0) text[i + 1] = "&a&lREADY!";
							else
								text[i + 1] = session.identifier.color + session.identifier.name + " &f" + Misc.ticksToTime(timeLeft) + "";
						} else text[i + 1] = "&cSlot Locked!";
					}

					if(hasReadyPotions(player)) text[4] = "&aRight-Click to collect!";
					else if(getBrewingSlot(player) >= 0) text[4] = "&eRight Click to Brew!";
					else text[4] = "&cAll slots full!";

					anim.setText(player, text);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				BrewingAnimation anim = brewingAnimations.get(0);

				for(Entity nearbyEntity : anim.location.getWorld().getNearbyEntities(anim.location, 10, 10, 10)) {
					if(!(nearbyEntity instanceof Player)) continue;
					Player player = (Player) nearbyEntity;
					if(!hasPotions(player)) continue;

					PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook identityTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(spinStand, anim.location), (byte) 0, (byte) 0, (byte) 0, (byte) i, (byte) 0, false);
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(identityTpPacket);
					player.playEffect(anim.location.clone().add(0.5, 1.0, 0.5), Effect.POTION_SWIRL, 0);

					i += 4;
					if(i >= 256) i = 0;
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 2, 2);

		new BukkitRunnable() {
			@Override
			public void run() {
				BrewingAnimation anim = brewingAnimations.get(0);

				for(Entity nearbyEntity : anim.location.getWorld().getNearbyEntities(anim.location, 10, 10, 10)) {
					if(!(nearbyEntity instanceof Player)) continue;
					Player player = (Player) nearbyEntity;
					if(hasPotions(player)) Sounds.POTION_BUBBLE.play(player);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 100, 100);

	}

	public static void onStart() {
		brewingAnimations.add(new BrewingAnimation(new Location(Bukkit.getWorld("darkzone"), 222, 91, -102)));
		spinStand = (ArmorStand) brewingAnimations.get(0).location.getWorld().spawnEntity(brewingAnimations.get(0).location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
		spinStand.setItemInHand(new ItemStack(Material.STICK));
		spinStand.setArms(true);
		spinStand.setRightArmPose(new EulerAngle(Math.toRadians(330), Math.toRadians(345), Math.toRadians(0)));
		spinStand.setVisible(false);
		spinStand.setGravity(false);
		brewingStands.add(spinStand);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(event.getClickedBlock().getType() != Material.CAULDRON) return;
		if(player.getWorld() != Bukkit.getWorld("darkzone")) return;
		if(hasReadyPotions(player)) {
			for(int i = 0; i < 3; i++) {
				BrewingSession session = getBrewingSession(player, i + 1);
				if(session == null) continue;

				int addTicks = (105 - session.reduction.getBrewingReductionMinutes()) * 60 * 20;
				int timeLeft = (int) ((int) (((session.startTime / 1000) * 20) + addTicks) - (((System.currentTimeMillis() / 1000) * 20)));
				if(timeLeft < 0) {
					session.givePotion();
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					pitPlayer.stats.potionsBrewed++;
					BrewPotionsQuest.INSTANCE.brewPotion(pitPlayer);
				}
			}
			return;
		}
		if(getBrewingSlot(player) < 0) {
			pausePlayers.add(player);
			brewingAnimations.get(0).setText(player, new String[]{"&cAll of your Brewing Slots", "&care full!", "&c", "&cYou may unlock up to 3", "&cin the &eRenown Shop&c."});
			new BukkitRunnable() {
				@Override
				public void run() {
					pausePlayers.remove(player);
				}
			}.runTaskLater(PitSim.INSTANCE, 40);
			event.setCancelled(true);
			return;
		}

		event.setCancelled(true);
		for(BrewingAnimation brewingAnimation : brewingAnimations) {
			if(brewingAnimation.location.equals(event.getClickedBlock().getLocation())) {
				if(!brewingAnimation.players.contains(player)) brewingAnimation.addPlayer(player);
			}
		}
	}

	@EventHandler
	public void onStandClick(PlayerInteractAtEntityEvent event) {
		for(BrewingAnimation brewingAnimation : brewingAnimations) {
			brewingAnimation.onButtonPush(event);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		for(BrewingAnimation brewingAnimation : brewingAnimations) {
			brewingAnimation.onMove(event);
		}
	}

	@EventHandler
	public void onQuit(PitQuitEvent event) {
		for(BrewingAnimation brewingAnimation : brewingAnimations) {
			brewingAnimation.onQuit(event);
		}
	}

	@EventHandler
	public void onHit(AttackEvent.Pre pre) {
		if(!(pre.getDefender() instanceof ArmorStand)) return;
		if(pre.getDefender().getUniqueId().equals(spinStand.getUniqueId())) pre.setCancelled(true);
		for(ArmorStand brewingStand : brewingStands) {
			if(brewingStand.getUniqueId().equals(pre.getDefender().getUniqueId())) pre.setCancelled(true);
		}
	}

	public static int getBrewingSlot(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player.getPlayer());
		for(int i = 0; i < pitPlayer.brewingSessions.size(); i++) {
			if(pitPlayer.brewingSessions.get(i) == null) return i + 1;
		}
		return -1;
	}

	public static int getStandID(final ArmorStand stand, Location location) {
		for(final Entity entity : Bukkit.getWorld("darkzone").getNearbyEntities(location, 7.0, 7.0, 7.0)) {
			if(!(entity instanceof ArmorStand)) {
				continue;
			}
			if(entity.getUniqueId().equals(stand.getUniqueId())) {
				return entity.getEntityId();
			}
		}
		return 0;
	}

	public static boolean hasReadyPotions(Player player) {
		for(int i = 0; i < 3; i++) {
			BrewingSession session = getBrewingSession(player, i + 1);
			if(session == null) continue;

			int addTicks = (105 - session.reduction.getBrewingReductionMinutes()) * 60 * 20;
			int timeLeft = (int) ((int) (((session.startTime / 1000) * 20) + addTicks) - (((System.currentTimeMillis() / 1000) * 20)));
			if(timeLeft < 0) return true;
		}
		return false;
	}

	public static boolean hasPotions(Player player) {
		for(int i = 0; i < 3; i++) {
			if(getBrewingSession(player, i + 1) != null) return true;
		}
		return false;
	}

	public static BrewingSession getBrewingSession(Player player, int slot) {
		for(BrewingSession brewingSession : brewingSessions) {
			if(brewingSession.player == player && brewingSession.brewingSlot == slot) return brewingSession;
		}
		return null;
	}

	@EventHandler
	public void onThrow(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(Misc.isAirOrNull(event.getPlayer().getItemInHand())) return;
		if(event.getPlayer().getItemInHand().getType() == Material.ENDER_PEARL) {
			event.setCancelled(true);
			event.getPlayer().updateInventory();
		}

	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();

		if(item.getType() == Material.ROTTEN_FLESH) event.setCancelled(true);
		if(item.getType() == Material.SPIDER_EYE) event.setCancelled(true);
		if(item.getType() == Material.ROTTEN_FLESH) event.setCancelled(true);
		if(item.getType() == Material.PORK) event.setCancelled(true);
	}
}
