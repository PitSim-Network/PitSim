package dev.kyro.pitremake.controllers;

import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public abstract class PitEnchant {

	public String name;
	public List<String> refNames;
	public boolean isRare;
	public ApplyType applyType;

	private String overrideName;

	public PitEnchant(String name, boolean isRare, ApplyType applyType, String... refNames) {
		this.name = name;
		this.refNames = Arrays.asList(refNames);
		this.isRare = isRare;
		this.applyType = applyType;
	}

	public abstract List<String> getDescription();
	public abstract DamageEvent onDamage(DamageEvent damageEvent);

	public String getDisplayName() {

		return overrideName != null ? overrideName : ChatColor.translateAlternateColorCodes('&', isRare ? "&dRARE! &9" + name : "&9" + name);
	}

	public void setOverrideName(String overrideName) {

		this.overrideName = overrideName;
	}
}
