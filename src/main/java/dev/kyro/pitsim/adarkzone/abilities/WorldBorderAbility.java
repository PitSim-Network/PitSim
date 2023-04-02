package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class WorldBorderAbility extends PitBossAbility {
	public List<Player> trappedPlayers = new ArrayList<>();
	public BukkitTask runnable;

	public SubLevel subLevel;
	public Location center;
	private double defaultSize;
	private double currentSize;

	@Override
	public void onEnable() {
		this.subLevel = getPitBoss().getSubLevel();
		this.center = subLevel.getMiddle().clone();
		this.defaultSize = getPitBoss().getSubLevel().spawnRadius + 3;
		this.currentSize = defaultSize;

		trapPlayer(getPitBoss().getSummoner());
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				List<Player> trappedPlayers = new ArrayList<>(WorldBorderAbility.this.trappedPlayers);
				for(Entity nearbyEntity : center.getWorld().getNearbyEntities(center, currentSize, 20, currentSize)) {
					if(!Misc.isEntity(nearbyEntity, PitEntityType.REAL_PLAYER)) continue;
					Player nearbyPlayer = (Player) nearbyEntity;
					trappedPlayers.remove(nearbyPlayer);
				}
				for(Player trappedPlayer : trappedPlayers) {
					DamageManager.createIndirectAttack(null, trappedPlayer, 0,
							attackEvent -> attackEvent.veryTrueDamage = 4);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 20L);
	}

	@Override
	public void onDisable() {
		runnable.cancel();
		for(Player trappedPlayer : new ArrayList<>(trappedPlayers)) removeBorder(trappedPlayer);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isDeadRealPlayer()) return;
		removeBorder(killEvent.getDeadPlayer());
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerRealPlayer() || trappedPlayers.contains(attackEvent.getAttackerPlayer())) return;

		PitBoss pitBoss = BossManager.getPitBoss(attackEvent.getDefender());
		if(pitBoss == null) return;
		if(pitBoss != super.getPitBoss()) return;

		trapPlayer(attackEvent.getAttackerPlayer());
	}

	public void trapPlayer(Player player) {
		if(trappedPlayers.contains(player)) return;
		setBorder(player);
		trappedPlayers.add(player);
	}

	public void setBorder(Player player) {
		WorldBorder worldBorder = new WorldBorder();
		worldBorder.setCenter(center.getX(), center.getZ());
		worldBorder.setSize(defaultSize * 2);
		worldBorder.setWarningDistance(0);
//		worldBorder.setWarningTime(5);
//		worldBorder.setDamageAmount(0.2F);
//		worldBorder.setDamageBuffer(5.0D);
//		worldBorder.transitionSizeBetween(oldSize, newSize, timeInSeconds);

		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
		entityPlayer.playerConnection.sendPacket(packet);
	}

	public void removeBorder(Player player) {
		if(!trappedPlayers.contains(player)) return;
		trappedPlayers.remove(player);

		WorldBorder worldBorder = new WorldBorder();
		worldBorder.setCenter(0, 0);
		worldBorder.setSize(1_000_000);

		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
		entityPlayer.playerConnection.sendPacket(packet);
	}
}
