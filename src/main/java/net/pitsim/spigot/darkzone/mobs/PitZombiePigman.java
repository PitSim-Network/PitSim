package net.pitsim.spigot.darkzone.mobs;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.darkzone.*;
import net.pitsim.spigot.misc.PitEquipment;
import net.pitsim.spigot.items.mobdrops.RawPork;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.enums.MobStatus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.PigZombie;
import org.bukkit.scheduler.BukkitRunnable;

public class PitZombiePigman extends PitMob {

	public PitZombiePigman(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ZOMBIE_PIGMAN;
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
		return isMinion() ? "Minion Pigman" : "Zombie Pigman";
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
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_HEALTH);
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_DAMAGE);
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsRandomInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addRareItem(() -> ItemFactory.getItem(RawPork.class).getItem(), DarkzoneBalancing.MOB_ITEM_DROP_PERCENT);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}