package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.mobdrops.RawPork;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.PigZombie;
import org.bukkit.scheduler.BukkitRunnable;

public class PitZombiePigman extends PitMob {

	public PitZombiePigman(Location spawnLocation) {
		super(spawnLocation);
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		PigZombie zombiePigman = spawnLocation.getWorld().spawn(spawnLocation, PigZombie.class);
		zombiePigman.setCustomNameVisible(false);
		zombiePigman.setRemoveWhenFarAway(false);
		zombiePigman.setCanPickupItems(false);

		zombiePigman.setBaby(false);

		new BukkitRunnable() {
			@Override
			public void run() {
				new PitEquipment().setEquipment(zombiePigman);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		return zombiePigman;
	}

	@Override
	public String getRawDisplayName() {
		return "Zombie Pigman";
	}

	@Override
	public String getRawDisplayNamePlural() {
		return "Zombie Pigmen";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.RED;
	}

	@Override
	public int getMaxHealth() {
		return 120;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public double getOffsetHeight() {
		return 1.5;
	}

	@Override
	public int getDroppedSouls() {
		return 6;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(RawPork.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}