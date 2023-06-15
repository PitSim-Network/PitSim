package net.pitsim.spigot.controllers;

import net.pitsim.spigot.PitSim;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CooldownManager {
	public static List<Cooldown> cooldownList = new ArrayList<>();
	public static Map<UUID, List<CooldownData>> cooldownDataMap = new HashMap<>();

	public static boolean hasModifier(Player player, Cooldown.CooldownModifier cooldownModifier) {
		if(!cooldownDataMap.containsKey(player.getUniqueId())) return false;
		for(CooldownData cooldownData : cooldownDataMap.get(player.getUniqueId()))
			if(cooldownData.cooldownModifier == cooldownModifier) return true;
		return false;
	}

	public static void init() {

		new BukkitRunnable() {
			@Override
			public void run() {
//				Other cooldown systems
				PitSim.currentTick++;

//				This cooldown system
				List<Cooldown> toRemove = new ArrayList<>();
				cooldown:
				for(Cooldown cooldown : cooldownList) {
					if(!cooldown.getCooldownModifiers().isEmpty() && cooldownDataMap.containsKey(cooldown.playerUUID)) {
						List<Cooldown.CooldownModifier> cooldownModifiers = cooldown.getCooldownModifiers();
						for(CooldownData cooldownData : cooldownDataMap.get(cooldown.playerUUID)) {
							if(!cooldownModifiers.contains(cooldownData.cooldownModifier)) continue;
							continue cooldown;
						}
					}
					boolean shouldRemove = cooldown.tick();
					if(shouldRemove) toRemove.add(cooldown);
				}
				cooldownList.removeAll(toRemove);

				for(Map.Entry<UUID, List<CooldownData>> playerEntry : new ArrayList<>(cooldownDataMap.entrySet())) {
					for(CooldownData dataEntry : new ArrayList<>(playerEntry.getValue())) {
						if(--dataEntry.effectiveTicks != 0) continue;
						playerEntry.getValue().remove(dataEntry);
					}
					if(playerEntry.getValue().isEmpty()) cooldownDataMap.remove(playerEntry.getKey());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public static void add(Cooldown cooldownCooldown) {
		if(!cooldownList.contains(cooldownCooldown)) cooldownList.add(cooldownCooldown);
	}

	public static void remove(Cooldown cooldownCooldown) {
		cooldownList.remove(cooldownCooldown);
	}

	public static void addModifierForPlayer(Player player, CooldownData cooldownData) {
		if(!cooldownDataMap.containsKey(player.getUniqueId()))
			cooldownDataMap.put(player.getUniqueId(), new ArrayList<>());
		cooldownDataMap.get(player.getUniqueId()).add(cooldownData);
	}

	public static class CooldownData {
		public Cooldown.CooldownModifier cooldownModifier;
		public int effectiveTicks;

		public CooldownData(Cooldown.CooldownModifier cooldownModifier, int effectiveTicks) {
			this.cooldownModifier = cooldownModifier;
			this.effectiveTicks = effectiveTicks;
		}
	}
}
