package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.aitems.mobdrops.RottenFlesh;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class PitZombie extends PitMob {

	public PitZombie(Location spawnLocation) {
		super(spawnLocation);
	}

	@Override
	public String getRawDisplayName() {
		return "Zombie";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.DARK_GREEN;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.ZOMBIE;
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	@Override
	public int getSpeedAmplifier() {
		return 0;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(RottenFlesh.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}