package net.pitsim.spigot.misc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerSpawnEntity extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY;

	private static PacketConstructor entityConstructor;

	public WrapperPlayServerSpawnEntity() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerSpawnEntity(PacketContainer packet) {
		super(packet, TYPE);
	}

	public WrapperPlayServerSpawnEntity(Entity entity, int type, int objectData) {
		super(fromEntity(entity, type, objectData), TYPE);
	}

	// Useful constructor
	private static PacketContainer fromEntity(Entity entity, int type, int objectData) {
		if (entityConstructor == null)
			entityConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, entity, type, objectData);
		return entityConstructor.createPacket(entity, type, objectData);
	}

	public int getEntityID() {
		return handle.getIntegers().read(0);
	}

	public Entity getEntity(World world) {
		return handle.getEntityModifier(world).read(0);
	}

	public Entity getEntity(PacketEvent event) {
		return getEntity(event.getPlayer().getWorld());
	}

	public void setEntityID(int value) {
		handle.getIntegers().write(0, value);
	}

	public int getType() {
		return handle.getIntegers().read(9);
	}

	public void setType(int value) {
		handle.getIntegers().write(9, value);
	}

	public double getX() {
		return handle.getIntegers().read(1) / 32.0D;
	}

	public void setX(double value) {
		handle.getIntegers().write(1, (int) Math.floor(value * 32.0D));
	}

	public double getY() {
		return handle.getIntegers().read(2) / 32.0D;
	}

	public void setY(double value) {
		handle.getIntegers().write(2, (int) Math.floor(value * 32.0D));
	}

	public double getZ() {
		return handle.getIntegers().read(3) / 32.0D;
	}

	public void setZ(double value) {
		handle.getIntegers().write(3, (int) Math.floor(value * 32.0D));
	}

	public double getOptionalSpeedX() {
		return handle.getIntegers().read(4) / 8000.0D;
	}

	public void setOptionalSpeedX(double value) {
		handle.getIntegers().write(4, (int) (value * 8000.0D));
	}

	public double getOptionalSpeedY() {
		return handle.getIntegers().read(5) / 8000.0D;
	}

	public void setOptionalSpeedY(double value) {
		handle.getIntegers().write(5, (int) (value * 8000.0D));
	}

	public double getOptionalSpeedZ() {
		return handle.getIntegers().read(6) / 8000.0D;
	}

	public void setOptionalSpeedZ(double value) {
		handle.getIntegers().write(6, (int) (value * 8000.0D));
	}

	public float getYaw() {
		return (handle.getIntegers().read(7) * 360.F) / 256.0F;
	}

	public void setYaw(float value) {
		handle.getIntegers().write(7, (int) (value * 256.0F / 360.0F));
	}

	public float getPitch() {
		return (handle.getIntegers().read(8) * 360.F) / 256.0F;
	}

	public void setPitch(float value) {
		handle.getIntegers().write(8, (int) (value * 256.0F / 360.0F));
	}

	public int getObjectData() {
		return handle.getIntegers().read(10);
	}

	public void setObjectData(int value) {
		handle.getIntegers().write(10, value);
	}
}