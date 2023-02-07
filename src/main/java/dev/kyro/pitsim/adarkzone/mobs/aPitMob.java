package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.mobdrops.RottenFlesh;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

public class aPitMob extends PitMob {

	public aPitMob(Location spawnLocation) {
		super(spawnLocation);
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Zombie zombie = spawnLocation.getWorld().spawn(spawnLocation, Zombie.class);
		zombie.setCustomNameVisible(false);
		zombie.setRemoveWhenFarAway(false);
		zombie.setCanPickupItems(false);

		new BukkitRunnable() {
			@Override
			public void run() {
				new PitEquipment().setEquipment(zombie);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		return zombie;
	}

	@Override
	public String getRawDisplayName() {
		return "MOBNAME";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.DARK_GREEN;
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(RottenFlesh.class).getItem(), 1);
	}

	@Override
	public double getOffsetHeight() {
		return 1.5;
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}