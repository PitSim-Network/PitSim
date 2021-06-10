package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class VolleyShootEvent extends EntityShootBowEvent {

	public Map<PitEnchant, Integer> shooterEnchantMap;

	public VolleyShootEvent(LivingEntity entity, ItemStack bow, Arrow arrow, Float force) {
		super(entity, bow, arrow, force);
		shooterEnchantMap = EnchantManager.getEnchantsOnPlayer((Player) entity);
	}
}
