package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.objects.PitEnchantSpell;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class SpellUseEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private boolean isCancelled;

	private final PitEnchantSpell spell;
	private final int spellLevel;

	public SpellUseEvent(Player player, PitEnchantSpell spell, int spellLevel) {
		super(player);
		this.spell = spell;
		this.spellLevel = spellLevel;
	}

	public PitEnchantSpell getSpell() {
		return spell;
	}

	public int getSpellLevel() {
		return spellLevel;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}
