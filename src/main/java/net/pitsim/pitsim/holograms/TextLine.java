package net.pitsim.pitsim.holograms;

import net.pitsim.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Random;

public class TextLine {

	protected Hologram hologram;
	protected int entityId;

	public TextLine(Hologram hologram) {
		this.hologram = hologram;

		Random rand = new Random();
		this.entityId = rand.nextInt(-1 - (-65535) + 1) - 65535;
	}

	public void displayLine(Hologram hologram, Player player) {
		Location displayLocation = hologram.getSpawnLocation(this);
		EntityArmorStand stand = getPacketStand(displayLocation, player, true);

		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);

		//Use reflection to edit the id field in the packet
		try {
			Field field = packet.getClass().getDeclaredField("a");
			field.setAccessible(true);

			field.setInt(packet, entityId);
		} catch(NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		nmsPlayer.playerConnection.sendPacket(packet);

		PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(entityId, Misc.fromFixedPoint(displayLocation.getX()),
				Misc.fromFixedPoint(displayLocation.getY()), Misc.fromFixedPoint(displayLocation.getZ()), (byte) 0, (byte) 0, false);
		nmsPlayer.playerConnection.sendPacket(teleportPacket);
		updateLine(hologram, player);
	}

	public void updateLine(Hologram hologram, Player player) {
		Location displayLocation = hologram.getSpawnLocation(this);
		EntityArmorStand stand = getPacketStand(displayLocation, player, false);

		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityId, stand.getDataWatcher(), false);
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		nmsPlayer.playerConnection.sendPacket(packet);
	}

	public void removeLine(Player player) {
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityId);
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		nmsPlayer.playerConnection.sendPacket(packet);
	}

	public EntityArmorStand getPacketStand(Location displayLocation, Player player, boolean invisible) {
		World nmsWorld = ((CraftWorld) displayLocation.getWorld()).getHandle();

		int index = hologram.textLines.indexOf(this);
		String uncolored = hologram.getStrings(player).get(index);
		if(uncolored.isEmpty()) uncolored = "&7";
		String text = ChatColor.translateAlternateColorCodes('&', uncolored);

		EntityArmorStand stand = new EntityArmorStand(nmsWorld);
		stand.n(true);
		stand.setInvisible(true);
		stand.setCustomNameVisible(!invisible);
		stand.setArms(true);
		stand.setSmall(true);
		stand.setBasePlate(false);
		stand.setCustomName(text);
		stand.setGravity(false);
		stand.setLocation(displayLocation.getX(), 1000, displayLocation.getZ(), 0, 0);

		return stand;
	}
}
