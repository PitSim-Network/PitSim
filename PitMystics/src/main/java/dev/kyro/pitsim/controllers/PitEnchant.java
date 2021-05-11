package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class PitEnchant implements Listener {

	public String name;
	public List<String> refNames;
	public boolean isRare;
	public ApplyType applyType;
	public boolean effectStacks = false;
	public boolean meleOnly = false;
	public Map<UUID, Cooldown> cooldowns = new HashMap<>();

	private String overrideName;

	public PitEnchant(String name, boolean isRare, ApplyType applyType, String... refNames) {
		this.name = name;
		this.refNames = Arrays.asList(refNames);
		this.isRare = isRare;
		this.applyType = applyType;
	}

	public abstract DamageEvent onDamage(DamageEvent damageEvent);
	public abstract List<String> getDescription(int enchantLvl);

	public void onDisable() {}

	public Cooldown getCooldown(Player player, int time) {

		if(cooldowns.containsKey(player.getUniqueId())) {
			Cooldown cooldown = cooldowns.get(player.getUniqueId());
			cooldown.initialTime = time;
			return cooldown;
		}

		Cooldown cooldown = new Cooldown(time);
		cooldown.ticksLeft = 0;
		cooldowns.put(player.getUniqueId(), cooldown);
		return cooldown;
	}

	public String getDisplayName() {

		return overrideName != null ? overrideName : ChatColor.translateAlternateColorCodes('&', isRare ? "&dRARE! &9" + name : "&9" + name);
	}

	public void setOverrideName(String overrideName) {

		this.overrideName = overrideName;
	}
}
