package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.aitems.mobdrops.SpiderEye;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Spider;

public class PitSpider extends PitMob {

	public PitSpider(Location spawnLocation) {
		super(spawnLocation);
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Spider spider = spawnLocation.getWorld().spawn(spawnLocation, Spider.class);
		spider.setCustomNameVisible(false);
		spider.setRemoveWhenFarAway(false);
		spider.setCanPickupItems(false);

		return spider;
	}

	@Override
	public String getRawDisplayName() {
		return "Spider";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.BLACK;
	}

	@Override
	public int getMaxHealth() {
		return 60;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public double getOffsetHeight() {
		return 5.5;
	}

	@Override
	public int getDroppedSouls() {
		return 3;
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(SpiderEye.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}