package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Map;

public class PitPlayerAttemptAbilityEvent extends PlayerEvent {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private final Map<PitEnchant, Integer> enchantMap;

	public PitPlayerAttemptAbilityEvent(Player player) {
		super(player);
		this.enchantMap = EnchantManager.getEnchantsOnPlayer(player);
	}

	public int getEnchantLevel(PitEnchant pitEnchant) {

		return enchantMap.getOrDefault(pitEnchant, 0);
	}

	public Map<PitEnchant, Integer> getEnchantMap() {
		return enchantMap;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}
