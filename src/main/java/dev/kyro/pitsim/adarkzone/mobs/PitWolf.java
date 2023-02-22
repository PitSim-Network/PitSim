package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.aitems.mobdrops.Leather;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;

public class PitWolf extends PitMob {

	public PitWolf(Location spawnLocation) {
		super(spawnLocation);
	}

	@EventHandler
	public void onTame(EntityTameEvent event) {
		LivingEntity entity = event.getEntity();
		if(!isThisMob(entity)) return;
		event.setCancelled(true);
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Wolf wolf = spawnLocation.getWorld().spawn(spawnLocation, Wolf.class);
		wolf.setCustomNameVisible(false);
		wolf.setRemoveWhenFarAway(false);
		wolf.setCanPickupItems(false);

		return wolf;
	}

	@Override
	public String getRawDisplayName() {
		return "Wolf";
	}

	@Override
	public String getRawDisplayNamePlural() {
		return "Wolves";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.BLUE;
	}

	@Override
	public int getMaxHealth() {
		return 80;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return 4;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(Leather.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}