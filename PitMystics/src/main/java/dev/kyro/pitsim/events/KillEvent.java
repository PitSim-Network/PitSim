package dev.kyro.pitsim.events;

import dev.kyro.pitsim.nons.Non;
import dev.kyro.pitsim.nons.NonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KillEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public Player attacker;
	public Player defender;
	public boolean exeDeath;

	public int xpReward;
	public int goldReward = 5;

	public KillEvent(Player attacker, Player defender, boolean exeDeath) {
		this.attacker = attacker;
		this.defender = defender;
		this.exeDeath = exeDeath;

		Non defendingNon = NonManager.getNon(defender);
		xpReward = defendingNon == null ? 5 : 1;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
