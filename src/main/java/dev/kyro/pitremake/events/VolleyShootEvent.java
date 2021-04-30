package dev.kyro.pitremake.events;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class VolleyShootEvent extends EntityShootBowEvent {

	public VolleyShootEvent(LivingEntity entity, ItemStack bow, Arrow arrow, Float force) {
		super(entity, bow, arrow, force);
	}
}
