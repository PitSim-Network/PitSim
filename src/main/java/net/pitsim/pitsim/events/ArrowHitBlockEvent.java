package net.pitsim.pitsim.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

public class ArrowHitBlockEvent extends BlockEvent {

	private static final HandlerList handlers = new HandlerList();

	private Arrow arrow;

	public ArrowHitBlockEvent(Arrow arrow, Block block) {
		super(block);
		this.arrow = arrow;
	}

	public Arrow getArrow() {
		return arrow;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}