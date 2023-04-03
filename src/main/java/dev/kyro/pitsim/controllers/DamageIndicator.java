package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Random;

public class DamageIndicator implements Listener {
	public static DecimalFormat decimalFormat = new DecimalFormat("0");

	//	No handler necessary
	public static void onAttack(AttackEvent.Apply attackEvent, double finalDamage) {
		if(!attackEvent.isAttackerPlayer() || attackEvent.isFakeHit() || attackEvent.getDefender().isDead() ||
				attackEvent.isCancelled()) return;

		PitMob defenderMob = DarkzoneManager.getPitMob(attackEvent.getDefender());
		PitBoss defenderBoss = BossManager.getPitBoss(attackEvent.getDefender());
		if(defenderMob != null || defenderBoss != null) {
			createDamageStand(attackEvent.getAttackerPlayer(), attackEvent.getDefender(), finalDamage * DarkzoneBalancing.SPOOFED_HEALTH_INCREASE);
//			TODO: Enable this to remove boss bar damage indicator
			return;
		}
//		TODO: Remove this to remove boss bar damage indicator
//		if(defenderMob != null) return;

//		TODO: Damage indicators for players
//		if(attackEvent.isDefenderRealPlayer()) createDamageStand(attackEvent.getAttackerPlayer(), attackEvent.getDefender(), finalDamage);

		EntityPlayer entityPlayer = null;
		if(attackEvent.isDefenderPlayer()) entityPlayer = ((CraftPlayer) attackEvent.getDefender()).getHandle();

		int roundedDamageTaken = ((int) finalDamage) / getDivisor(attackEvent.getDefender());

		int originalHealth = ((int) attackEvent.getDefender().getHealth()) / getDivisor(attackEvent.getDefender());
		int maxHealth = ((int) attackEvent.getDefender().getMaxHealth()) / getDivisor(attackEvent.getDefender());

		int result = Math.max(originalHealth - roundedDamageTaken, 0);

		if((attackEvent.getDefender().getHealth() - finalDamage) % 2 < 1 && finalDamage > 1)
			roundedDamageTaken++;

		if(result == 0) {
			roundedDamageTaken = 0;
			for(int i = 0; i < originalHealth; i++) roundedDamageTaken++;
		}

		Non defendingNon = NonManager.getNon(attackEvent.getDefender());
		StringBuilder output = new StringBuilder();

		String playerName = "&7%luckperms_prefix%" + (defendingNon == null ? "%player_name%" : defendingNon.displayName) + " ";
		if(attackEvent.isDefenderPlayer())
			output.append(PlaceholderAPI.setPlaceholders(attackEvent.getDefenderPlayer(), playerName));
		else if(DarkzoneManager.isPitMob(attackEvent.getDefender())) output.append(defenderMob.getDisplayName()).append(" ");
		else output.append(attackEvent.getDefender().getCustomName() + " ");

		for(int i = 0; i < Math.max(originalHealth - roundedDamageTaken, 0); i++) {
			output.append(ChatColor.DARK_RED).append("\u2764");
		}

		if(attackEvent.isDefenderPlayer()) {
			for(int i = 0; i < roundedDamageTaken - (int) entityPlayer.getAbsorptionHearts() / getDivisor(attackEvent.getDefender()); i++) {
				output.append(ChatColor.RED).append("\u2764");
			}
		} else {
			for(int i = 0; i < roundedDamageTaken; i++) {
				output.append(ChatColor.RED).append("\u2764");
			}
		}

		for(int i = originalHealth; i < maxHealth; i++) {
			output.append(ChatColor.BLACK).append("\u2764");
		}

		if(attackEvent.isDefenderPlayer()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.getDefender());
			for(int i = 0; i < (int) entityPlayer.getAbsorptionHearts() / getDivisor(attackEvent.getDefender()); i++) {
				output.append(ChatColor.YELLOW).append("\u2764");
			}
			if(pitPlayer.shield.isActive()) {
				output.append(" &8[").append(AUtil.createProgressBar("|", ChatColor.BLUE, ChatColor.GRAY,
						(int) Math.ceil(Math.sqrt(pitPlayer.shield.getMaxShield())),
						pitPlayer.shield.getPreciseAmount() / pitPlayer.shield.getMaxShield())).append("&8]");
			}
		}

		ActionBarManager.sendActionBar(attackEvent.getAttackerPlayer(), ActionBarManager.LockTime.SHORT_TIME, output.toString());
	}

	public static void createDamageStand(Player attacker, LivingEntity defender, double damage) {
		Vector vector = defender.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize().setY(0).multiply(0.3);
		Location displayLocation = defender.getLocation().add(0, defender.getEyeHeight(), 0).subtract(vector)
				.add(Misc.randomOffset(0.7), Misc.randomOffset(0.7), Misc.randomOffset(0.7));

		WorldServer server = ((CraftWorld) defender.getWorld()).getHandle();
		EntityArmorStand stand = new EntityArmorStand(server);

		stand.setLocation(displayLocation.getX(), 1000, displayLocation.getZ(), 0, 0);
		stand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c" + Misc.getHearts(damage)));
		stand.setCustomNameVisible(true);
		stand.setGravity(false);
		stand.setSmall(true);
		stand.setInvisible(true);

		NBTTagCompound compoundTag = new NBTTagCompound();
		stand.c(compoundTag);
		compoundTag.setBoolean("Marker", true);
		stand.f(compoundTag);

		PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(stand);
		((CraftPlayer) attacker).getHandle().playerConnection.sendPacket(spawnPacket);
		PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(stand.getId(), fromFixedPoint(displayLocation.getX()),
				fromFixedPoint(displayLocation.getY()), fromFixedPoint(displayLocation.getZ()), (byte) 0, (byte) 0, false);
		((CraftPlayer) attacker).getHandle().playerConnection.sendPacket(teleportPacket);

		new BukkitRunnable() {
			@Override
			public void run() {
				PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(stand.getId());
				((CraftPlayer) attacker).getHandle().playerConnection.sendPacket(destroyPacket);
			}
		}.runTaskLater(PitSim.INSTANCE, 20 + new Random().nextInt(11));
	}

	public static int getDivisor(LivingEntity entity) {
		if(PlayerManager.isRealPlayer(entity)) return 2;
		return Math.max(1, (int) (2 * (entity.getMaxHealth() / 20)));
	}

	public static int fromFixedPoint(double fixedPoint) {
		return (int) (fixedPoint * 32.0);
	}
}
