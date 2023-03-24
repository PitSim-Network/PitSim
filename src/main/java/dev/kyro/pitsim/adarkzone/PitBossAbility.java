package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class PitBossAbility implements Listener {
	private PitBoss pitBoss;
	private boolean enabled = true;
	private double routineWeight;

	public PitBossAbility() {
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public PitBossAbility(double routineWeight) {
		this();
		this.routineWeight = routineWeight;
	}

//	Internal events (override to add functionality)
	public void onRoutineExecute() {}
	public boolean shouldExecuteRoutine() {
		return true;
	}
	public void onEnable() {}
	public void onDisable() {}

	public PitBossAbility pitBoss(PitBoss pitBoss) {
		this.pitBoss = pitBoss;
		return this;
	}

	public boolean isAssignedBoss(LivingEntity entity) {
		return getPitBoss().boss == entity;
	}

	public void disable() {
		if(!enabled) return;
		enabled = false;
		HandlerList.unregisterAll(this);
		onDisable();
	}

	public List<Player> getViewers() {
		List<Player> viewers = new ArrayList<>();
		for(Entity entity : pitBoss.boss.getNearbyEntities(50, 50, 50)) {
			if(!(entity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(entity.getUniqueId());
			if(player != null) viewers.add(player);
		}
		return viewers;
	}

	public PitBoss getPitBoss() {
		return pitBoss;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public double getRoutineWeight() {
		return routineWeight;
	}
}
