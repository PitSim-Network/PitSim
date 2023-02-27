package dev.kyro.pitsim.events;

import dev.kyro.pitsim.adarkzone.notdarkzone.EquipmentType;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EquipmentChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final PitPlayer pitPlayer;
	private final EquipmentType equipmentType;
	private final boolean onJoin;

	private final PitEquipment previousEquipment;
	private final PitEquipment currentEquipment;
	private final ItemStack previousItem;
	private final ItemStack currentItem;
	private final Map<PitEnchant, Integer> previousEnchantMap;
	private final Map<PitEnchant, Integer> currentEnchantMap;

	public EquipmentChangeEvent(Player player, EquipmentType equipmentType, PitEquipment previousEquipment,
								PitEquipment currentEquipment, boolean onJoin) {
		this.player = player;
		this.pitPlayer = PitPlayer.getPitPlayer(player);
		this.equipmentType = equipmentType;
		this.previousEquipment = previousEquipment;
		this.currentEquipment = currentEquipment;
		this.previousItem = previousEquipment.getItemStack(equipmentType);
		this.currentItem = currentEquipment.getItemStack(equipmentType);
		this.previousEnchantMap = EnchantManager.readEnchantsOnEquipment(previousEquipment);
		this.currentEnchantMap = EnchantManager.readEnchantsOnEquipment(currentEquipment);
		this.onJoin = onJoin;
	}

	public Player getPlayer() {
		return player;
	}

	public PitPlayer getPitPlayer() {
		return pitPlayer;
	}

	public EquipmentType getEquipmentType() {
		return equipmentType;
	}

	public boolean isOnJoin() {
		return onJoin;
	}

	public PitEquipment getPreviousEquipment() {
		return previousEquipment;
	}

	public PitEquipment getCurrentEquipment() {
		return currentEquipment;
	}

	public ItemStack getPreviousItem() {
		return previousItem;
	}

	public ItemStack getCurrentItem() {
		return currentItem;
	}

	public Map<PitEnchant, Integer> getPreviousEnchantMap() {
		return previousEnchantMap;
	}

	public Map<PitEnchant, Integer> getCurrentEnchantMap() {
		return currentEnchantMap;
	}

	public int getPreviousEnchantLvl(PitEnchant enchant) {
		return previousEnchantMap.getOrDefault(enchant, 0);
	}

	public int getCurrentEnchantLvl(PitEnchant enchant) {
		return currentEnchantMap.getOrDefault(enchant, 0);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
