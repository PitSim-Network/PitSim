package dev.kyro.pitsim;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

public interface NameTaggable {
	LivingEntity getTaggableEntity();
	String getDisplayName();
	ChatColor getChatColor();
}
