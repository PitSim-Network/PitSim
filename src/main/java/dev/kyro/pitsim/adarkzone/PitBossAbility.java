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
	public PitBoss pitBoss;
	public boolean enabled = true;

	public PitBossAbility() {
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
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

//	Internal events (override to add functionality)
	public void onRoutineExecute() {}

	public boolean shouldExecuteRoutine() {
		return true;
	}

	public PitBossAbility pitBoss(PitBoss pitBoss) {
		this.pitBoss = pitBoss;
		return this;
	}

	public boolean isAssignedBoss(LivingEntity entity) {
		return pitBoss.boss == entity;
	}

	public void disable() {
		enabled = false;
		HandlerList.unregisterAll(this);
	}

	public int getFallTime(int totalHeight) {
		final double gravity = -0.03999999910593033;
		final double drag = 0.9800000190734863;

		double locY = 0;
		double motY = 0;

		int ticks = 0;

		while(locY <= totalHeight) {
			locY += motY;
			motY *= drag;
			motY -= gravity;

			ticks++;
		}

		return ticks;
	}
}
