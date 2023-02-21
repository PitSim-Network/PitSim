package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.aitems.mobdrops.EnderPearl;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Enderman;

public class PitEnderman extends PitMob {

	public PitEnderman(Location spawnLocation) {
		super(spawnLocation);
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Enderman enderman = spawnLocation.getWorld().spawn(spawnLocation, Enderman.class);
		enderman.setCustomNameVisible(false);
		enderman.setRemoveWhenFarAway(false);
		enderman.setCanPickupItems(false);

		return enderman;
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
		return 200;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public double getOffsetHeight() {
		return 2.0;
	}

	@Override
	public int getDroppedSouls() {
		return 10;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(EnderPearl.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.BABY_RABBIT)
				.addMob(PitNameTag.RidingType.BABY_RABBIT);
	}
}