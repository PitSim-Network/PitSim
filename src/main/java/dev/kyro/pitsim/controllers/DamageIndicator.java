package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Random;

public class DamageIndicator implements Listener {
	public static DecimalFormat decimalFormat = new DecimalFormat("0");

	//    @EventHandler(priority = EventPriority.MONITOR)
	public static void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(attackEvent.isFakeHit()) return;

		Player attacker = attackEvent.getAttackerPlayer();
		LivingEntity defender = attackEvent.getDefender();

		PitMob pitDefender = DarkzoneManager.getPitMob(defender);
		if(pitDefender != null) {
			createDamageStand(attacker, pitDefender, attackEvent.getEvent().getFinalDamage());
			return;
		}

		EntityPlayer entityPlayer = null;
		if(defender instanceof Player) entityPlayer = ((CraftPlayer) defender).getHandle();

		int roundedDamageTaken = ((int) attackEvent.getEvent().getFinalDamage()) / getNum(defender);

		int originalHealth = ((int) defender.getHealth()) / getNum(defender);
		int maxHealth = ((int) defender.getMaxHealth()) / getNum(defender);

		int result = Math.max(originalHealth - roundedDamageTaken, 0);

		if((defender.getHealth() - attackEvent.getEvent().getFinalDamage()) % 2 < 1 && attackEvent.getEvent().getFinalDamage() > 1)
			roundedDamageTaken++;

		if(result == 0) {
			roundedDamageTaken = 0;

			for(int i = 0; i < originalHealth; i++) {
				roundedDamageTaken++;
			}
		}

		Non defendingNon = NonManager.getNon(defender);
		StringBuilder output = new StringBuilder();

		String playername = "&7%luckperms_prefix%" + (defendingNon == null ? "%player_name%" : defendingNon.displayName) + " ";
		if(defender instanceof Player)
			output.append(PlaceholderAPI.setPlaceholders(attackEvent.getDefenderPlayer(), playername));
		else if(DarkzoneManager.isPitMob(defender)) output.append(DarkzoneManager.getPitMob(defender).getDisplayName()).append(" ");
		else output.append(defender.getCustomName() + " ");

		for(int i = 0; i < Math.max(originalHealth - roundedDamageTaken, 0); i++) {
			output.append(ChatColor.DARK_RED).append("\u2764");
		}

		if(defender instanceof Player) {
			for(int i = 0; i < roundedDamageTaken - (int) entityPlayer.getAbsorptionHearts() / getNum(defender); i++) {
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

		if(defender instanceof Player) {
			for(int i = 0; i < (int) entityPlayer.getAbsorptionHearts() / getNum(defender); i++) {
				output.append(ChatColor.YELLOW).append("\u2764");
			}
		}

		ActionBarManager.sendActionBar(attacker, output.toString());
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer())) return;
		PitMob pitDead = DarkzoneManager.getPitMob(killEvent.getDead());
		if(pitDead == null) return;
		createDamageStand(killEvent.getKillerPlayer(), pitDead, killEvent.getEvent().getFinalDamage());
	}

	public static void createDamageStand(Player attacker, PitMob defenderMob, double damage) {
		LivingEntity defender = defenderMob.getMob();

		Vector vector = defender.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize().setY(0).multiply(0.3);
		Location displayLocation = defender.getLocation().add(0, defenderMob.getOffsetHeight(), 0).subtract(vector)
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

	public static int getNum(LivingEntity entity) {
		return Math.max(1, (int) (2 * (entity.getMaxHealth() / 20)));
	}

	public static int fromFixedPoint(double fixedPoint) {
		return (int) (fixedPoint * 32.0);
	}
}
