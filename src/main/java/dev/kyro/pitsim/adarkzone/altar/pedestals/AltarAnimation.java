package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.altar.AltarManager;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class AltarAnimation {

	public static final int STREAM_TICKS = 40;

	public Player player;
	public int totalSouls;

	public List<AltarPedestal> activatedPedestals;
	public BukkitRunnable onComplete;
	public double turmoilMultiplier;

	public final Map<AltarPedestal, BukkitTask> streamRunnables = new HashMap<>();
	public final List<EntityItem> soulItems = new ArrayList<>();
	public final List<PedestalSpin> pedestalSpins = new ArrayList<>();
	public boolean activeTurmoil = false;

	public AltarAnimation(Player player, int totalSouls, List<AltarPedestal> activatedPedestals, double turmoil, BukkitRunnable onComplete) {
		this.player = player;
		this.totalSouls = totalSouls;
		this.activatedPedestals = activatedPedestals;
		this.turmoilMultiplier = turmoil;
		this.onComplete = onComplete;

		playSoulAnimation();
	}

	public void playSoulAnimation() {
		Location location = AltarManager.TEXT_LOCATION.clone().add(0, 1, 0);

		new BukkitRunnable() {
			public final double RADIUS = 1.25;
			public final int degree_interval = 15;
			public int ticks = 0;

			@Override
			public void run() {
				double x = Math.cos(Math.toRadians(ticks * degree_interval)) * RADIUS;
				double z = Math.sin(Math.toRadians(ticks * degree_interval)) * RADIUS;

				World world = ((CraftWorld) (player.getWorld())).getHandle();
				EntityItem entityItem = new EntityItem(world);
				entityItem.setPosition(location.getX() + x, location.getY(), location.getZ() + z);
				entityItem.setItemStack(CraftItemStack.asNMSCopy(getItemStack()));

				PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(entityItem, 2, 1);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

				PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(meta);

				PacketPlayOutEntityVelocity velocity = new PacketPlayOutEntityVelocity(entityItem.getId(), Math.random() * 0.02, 0, Math.random() * 0.02);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(velocity);

				ticks ++;
				soulItems.add(entityItem);
				Sounds.SOUL_DROP.play(player, 1f, Math.min(0.05f * ticks, 2f));

				if(soulItems.size() >= (totalSouls / 5) + 4) {
					cancel();
					drawPedestalStreams();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 1);
	}

	public void drawPedestalStreams() {
		int delay = 0;
		for(AltarPedestal pedestal : activatedPedestals) {
			new BukkitRunnable() {
				@Override
				public void run() {
					drawPedestalStream(pedestal);
					pedestalSpins.add(new PedestalSpin(player, pedestal));

					if(pedestal instanceof TurmoilPedestal) {
						new BukkitRunnable() {
							@Override
							public void run() {
								turmoilAnimation();
							}
						}.runTaskLater(PitSim.INSTANCE, 40);
					}
				}
			}.runTaskLater(PitSim.INSTANCE, delay);
			delay += 40;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				endAnimation();
			}
		}.runTaskLater(PitSim.INSTANCE, delay + 50);
	}

	public void drawPedestalStream(AltarPedestal pedestal) {
		RedstoneParticle particle = new RedstoneParticle();

		streamRunnables.put(pedestal, new BukkitRunnable() {
			public int ticks = 0;
			public final int PARTICLES_PER_TICK = 1;

			public final List<Location> previousLocations = new ArrayList<>();

			@Override
			public void run() {
				PedestalSpin pedestalSpin = null;
				for(PedestalSpin spin : pedestalSpins) {
					if(spin.pedestal == pedestal) {
						pedestalSpin = spin;
					}
				}

				double y = pedestalSpin == null ? 0 : pedestalSpin.getLocationY();
				Vector vector = AltarManager.ALTAR_CENTER.clone().subtract(0, 0.25, 0).toVector().subtract(pedestal.location.clone().add(0, 1.75 + y, 0).toVector());

				for(int i = 0; i < PARTICLES_PER_TICK; i++) {
					double percent = (double) ticks / (double) STREAM_TICKS;
					Location location = pedestal.location.clone().add(0, 1.75, 0).add(vector.clone().multiply(percent));
					previousLocations.add(location);
					for(Location previousLocation : previousLocations) {
						particle.display(player, previousLocation.clone().add(0, y, 0), pedestal.getParticleColor());
					}
				}

				if(ticks >= STREAM_TICKS) {
					for(Location previousLocation : previousLocations) {
						particle.display(player, previousLocation.clone().add(0, y, 0), pedestal.getParticleColor());
					}
					return;
				} else Sounds.BEAM_CONNECT.play(player, 0.1f, Math.min(0.05f * ticks, 2f));

				ticks++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 1));
	}

	public void turmoilAnimation() {
		activeTurmoil = true;

		Location spawn = AltarManager.ALTAR_CENTER;
		World world = ((CraftWorld) (spawn.getWorld())).getHandle();
		EntityArmorStand stand = new EntityArmorStand(world, spawn.getX(), spawn.getY(), spawn.getZ());
		stand.setInvisible(true);
		stand.setGravity(false);
		stand.setCustomNameVisible(true);
		stand.setBasePlate(true);

		PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(stand);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnPacket);

		DataWatcher dataWatcher = stand.getDataWatcher();

		int rotations = 0;
		for(double i = 0; i < turmoilMultiplier; i += 0.1) {
			BigDecimal bd = new BigDecimal(Double.toString(i));
			bd = bd.setScale(1, RoundingMode.HALF_UP);

			String text = "&2&lTURMOIL " + (1 + bd.doubleValue()) + "x";

			int finalRotations = rotations;
			new BukkitRunnable() {
				@Override
				public void run() {
					dataWatcher.watch(2, (Object) ChatColor.translateAlternateColorCodes('&', text));
					PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(stand.getId(), dataWatcher, true);
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(meta);

					Sounds.TURMOIL.play(player, 1.5f, Math.min(2f, 0.025F * finalRotations));
				}
			}.runTaskLater(PitSim.INSTANCE, 2L * rotations);

			if(i >= turmoilMultiplier - 0.1) {
				new BukkitRunnable() {
					@Override
					public void run() {
						PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(stand.getId());
						((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
						activeTurmoil = false;
					}
				}.runTaskLater(PitSim.INSTANCE, (rotations * 2L) + 60);
			}

			rotations++;
		}
	}

	public void endAnimation() {
		if(activeTurmoil) {
			new BukkitRunnable() {
				@Override
				public void run() {
					endAnimation();
				}
			}.runTaskLater(PitSim.INSTANCE, 10);
			return;
		}


		if(onComplete != null) onComplete.runTask(PitSim.INSTANCE);
		for(EntityItem soulItem : soulItems) {
			PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(soulItem.getId());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
		}

		for(BukkitTask streamRunnable : streamRunnables.values()) {
			streamRunnable.cancel();
		}

		for(PedestalSpin pedestalSpin : pedestalSpins) {
			pedestalSpin.stop();
		}

		AltarManager.animations.remove(this);
	}

	public void onQuit() {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.taintedSouls += totalSouls;

		onComplete = null;
		endAnimation();
	}

	public static ItemStack getItemStack() {
		ItemStack item = new ItemStack(org.bukkit.Material.GHAST_TEAR);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(Collections.singletonList(UUID.randomUUID().toString()));
		item.setItemMeta(meta);
		return item;
	}
}
