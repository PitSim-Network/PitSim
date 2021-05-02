package dev.kyro.pitremake.events;

import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class VolleyShootEvent extends EntityShootBowEvent {

	public Map<PitEnchant, Integer> enchantMap;

	public VolleyShootEvent(LivingEntity entity, ItemStack bow, Arrow arrow, Float force) {
		super(entity, bow, arrow, force);
		enchantMap = EnchantManager.getEnchantsOnPlayer((Player) entity);
	}
}
