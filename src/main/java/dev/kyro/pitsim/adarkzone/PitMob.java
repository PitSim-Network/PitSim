package dev.kyro.pitsim.adarkzone;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PitMob {

	private Creature mob;
	private Player target;
	private DropPool dropPool;

	public PitMob(Location spawnLocation) {
		spawn(spawnLocation);
	}

	public abstract String getDisplayName();
	public abstract EntityType getEntityType();
	public abstract int getMaxHealth();
	public abstract int getSpeedAmplifier();

	public String getRawDisplayName() {
		return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getDisplayName()));
	}

	public void onSpawn() {}

	public void spawn(Location spawnLocation) {
		mob = (Creature) spawnLocation.getWorld().spawnEntity(spawnLocation, getEntityType());
	}

	public void kill(Player killer) {
		dropPool.singleDistribution(killer);
		remove();
	}

	public void remove() {
		if(mob != null) mob.remove();
		getSubLevel().mobs.remove(this);
	}

	public void setTarget(Player target) {
		this.target = target;
		mob.setTarget(target);
	}

	public Player getTarget() {
		return target;
	}

	public void setDropPool(DropPool dropPool) {
		this.dropPool = dropPool;
	}

	public void addDrop(ItemStack item, double weight) {
		dropPool.dropPool.put(item, weight);
	}

	public void rewardKill(Player killer) {
		killer.getInventory().addItem(dropPool.getRandomDrop());
	}

	public Creature getMob() {
		return mob;
	}

	public void setMob(Creature mob) {
		this.mob = mob;
	}

	public SubLevel getSubLevel() {
		for(SubLevel subLevel : DarkzoneManager.subLevels) {
			for(PitMob pitMob : subLevel.mobs) if(pitMob == this) return subLevel;
		}
		throw new RuntimeException();
	}
}
