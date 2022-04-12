package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.MobType;
import dev.kyro.pitsim.controllers.MobManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class PitMob implements Listener {

	public MobType type;
	public Location spawnLoc;
	public int subLevel;
	public LivingEntity entity;
	public String displayName;

	public PitMob(MobType type, Location spawnLoc, int subLevel, String displayName) {
		this.type = type;
		this.spawnLoc = spawnLoc;
		this.subLevel = subLevel;
		this.displayName = displayName;

		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
		MobManager.mobs.add(this);

		this.entity = spawnMob(spawnLoc);
	}

	public abstract LivingEntity spawnMob(Location spawnLoc);

	public abstract Map<ItemStack, Integer> getDrops();

	public static boolean isPitMob(LivingEntity entity) {
		for(PitMob mob : MobManager.mobs) {
			if(mob.entity.getUniqueId().equals(entity.getUniqueId())) return true;
		}
		return false;
	}

	public static PitMob getPitMob(LivingEntity entity) {
		for(PitMob mob : MobManager.mobs) {
			if(mob.entity == null) return null;
			if(mob.entity.getUniqueId().equals(entity.getUniqueId())) return mob;
		}
		return null;
	}
}
