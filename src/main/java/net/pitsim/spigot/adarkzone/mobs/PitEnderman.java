package net.pitsim.spigot.adarkzone.mobs;

import net.pitsim.spigot.adarkzone.*;
import net.pitsim.spigot.aitems.mobdrops.EnderPearl;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.enums.MobStatus;
import net.pitsim.spigot.misc.CustomPitEnderman;
import net.pitsim.spigot.misc.EntityManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Enderman;

public class PitEnderman extends PitMob {

	static {
		EntityManager.registerEntity("PitEnderman", 58, CustomPitEnderman.class);
	}

	public PitEnderman(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ENDERMAN;
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Enderman enderman = spawnLocation.getWorld().spawn(spawnLocation, Enderman.class);
		enderman.setCustomNameVisible(false);
		enderman.setRemoveWhenFarAway(false);
		enderman.setCanPickupItems(false);

		return enderman;

//		World nmsWorld = ((CraftWorld) spawnLocation.getWorld()).getHandle();
//
//		CustomPitEnderman enderman = new CustomPitEnderman(nmsWorld, DarkzoneManager.getSubLevel(SubLevelType.ENDERMAN));
//		enderman.setLocation(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), 0, 0);
//		nmsWorld.addEntity(enderman);
//
//		return (Creature) enderman.getBukkitEntity();
	}

	@Override
	public String getRawDisplayName() {
		return "Enderman";
	}

	@Override
	public String getRawDisplayNamePlural() {
		return "Endermen";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.DARK_PURPLE;
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
				.addRareItem(() -> ItemFactory.getItem(EnderPearl.class).getItem(), DarkzoneBalancing.MOB_ITEM_DROP_PERCENT);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.BABY_RABBIT)
				.addMob(PitNameTag.RidingType.BABY_RABBIT);
	}
}