package net.pitsim.spigot.misc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerUpdateSign extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.UPDATE_SIGN;

	public WrapperPlayServerUpdateSign() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerUpdateSign(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Location.
	 * <p>
	 * Notes: block Coordinates
	 * @return The current Location
	 */
	public BlockPosition getLocation() {
		return handle.getBlockPositionModifier().read(0);
	}

	/**
	 * Set Location.
	 * @param value - new value.
	 */
	public void setLocation(BlockPosition value) {
		handle.getBlockPositionModifier().write(0, value);
	}

	/**
	 * Retrieve this sign's lines of text represented by a chat component array.
	 * @return The current lines
	 */
	public WrappedChatComponent[] getLines() {
		return handle.getChatComponentArrays().read(0);
	}

	/**
	 * Set this sign's lines of text.
	 * @param value - Lines, must be 4 elements long
	 */
	public void setLines(WrappedChatComponent[] value) {
		if (value == null)
			throw new IllegalArgumentException("value cannot be null!");
		if (value.length != 4)
			throw new IllegalArgumentException("value must have 4 elements!");

		handle.getChatComponentArrays().write(0, value);
	}
}