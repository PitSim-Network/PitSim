package net.pitsim.spigot.misc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayClientBlockDig extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Client.BLOCK_DIG;

	public enum Status {
		STARTED_DIGGING,    // 0
		CANCELLED_DIGGING,  // 1
		FINISHED_DIGGING,   // 2
		DROP_ITEM_STACK,    // 3
		DROP_ITEM, 	        // 4

		/**
		 * Shooting arrows or finishing eating.
		 */
		SHOOT_ARROW 	    // 5
	}

	public WrapperPlayClientBlockDig() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayClientBlockDig(PacketContainer packet) {
		super(packet, TYPE);
	}

	public BlockPosition getLocation() {
		return handle.getBlockPositionModifier().read(0);
	}

	/**
	 * Retrieve the action the player is taking against the block.
	 * @return The current Status
	 */
	public Status getStatus() {
		return Status.values()[handle.getIntegers().read(4)];
	}

	/**
	 * Set the action the player is taking against the block.
	 * @param value - new action.
	 */
	public void setStatus(Status value) {
		handle.getIntegers().write(4, value.ordinal());
	}

	/**
	 * Retrieve block position.
	 * @return The current X
	 */
	public int getX() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set block position.
	 * @param value - new value.
	 */
	public void setX(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve block position.
	 * @return The current Y
	 */
	public byte getY() {
		return handle.getIntegers().read(1).byteValue();
	}

	/**
	 * Set block position.
	 * @param value - new value.
	 */
	public void setY(byte value) {
		handle.getIntegers().write(1, (int) value);
	}

	/**
	 * Retrieve block position.
	 * @return The current Z
	 */
	public int getZ() {
		return handle.getIntegers().read(2);
	}

	/**
	 * Set block position.
	 * @param value - new value.
	 */
	public void setZ(int value) {
		handle.getIntegers().write(2, value);
	}

}