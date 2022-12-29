package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.EnchantRarity;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class PitEnchant implements Listener {

	public String name;
	public List<String> refNames;
	public boolean isRare;
	public ApplyType applyType;
	public boolean isUncommonEnchant;
	public boolean levelStacks = false;
	public boolean meleOnly = false;
	public boolean fakeHits = false;
	public boolean tainted = false;
	public Map<UUID, Cooldown> cooldowns = new HashMap<>();

	private String overrideName;

	public PitEnchant(String name, boolean isRare, ApplyType applyType, String... refNames) {
		this.name = name;
		this.refNames = Arrays.asList(refNames);
		this.isRare = isRare;
		this.applyType = applyType;
		this.isUncommonEnchant = isRare;
	}

	public abstract List<String> getDescription(int enchantLvl);

	public void onDisable() {
	}

	public boolean canApply(AttackEvent attackEvent) {

		if(!fakeHits && Regularity.isRegHit(attackEvent.getDefender()) && attackEvent.isFakeHit()) return false;
//		Skip if fake hit and enchant doesn't handle fake hits
//		if(!fakeHits && attackEvent.fakeHit) return false;
//		Skip enchant application if the enchant is a bow enchant and is used in mele
		if(applyType == ApplyType.BOWS && attackEvent.getArrow() == null) return false;
		if(applyType == ApplyType.SCYTHES || applyType == ApplyType.CHESTPLATES) {
			if(attackEvent.getAttacker().getWorld() != MapManager.getDarkzone()) return false;
		}
//		Skips enchant application if the enchant only works on mele hit and the event is from an arrow
		if(meleOnly && attackEvent.getArrow() != null) return false;
		return true;
	}

	public Cooldown getCooldown(Player player, int time) {

		if(cooldowns.containsKey(player.getUniqueId())) {
			Cooldown cooldown = cooldowns.get(player.getUniqueId());
			cooldown.initialTime = time;
			return cooldown;
		}

		Cooldown cooldown = new Cooldown(player.getUniqueId(), time);
		cooldown.ticksLeft = 0;
		cooldowns.put(player.getUniqueId(), cooldown);
		return cooldown;
	}

	public String getDisplayName() {
		if(overrideName != null) return overrideName;
		if(tainted) {
			if(applyType == ApplyType.SCYTHES)
				return ChatColor.translateAlternateColorCodes('&', isRare ? "&dSPELL! &5" + name : "&5" + name);
			else return ChatColor.translateAlternateColorCodes('&', isRare ? "&dEFFECT! &5" + name : "&5" + name);
		}
		if(isRare) return ChatColor.translateAlternateColorCodes('&', "&dRARE! &9" + name);
		if(isUncommonEnchant) return ChatColor.translateAlternateColorCodes('&', "&aUNC. &9" + name);
		return ChatColor.translateAlternateColorCodes('&', "&9" + name);
	}

	public void setOverrideName(String overrideName) {

		this.overrideName = overrideName;
	}

	public EnchantRarity getRarity() {

		if(isRare) return EnchantRarity.RARE;
		if(isUncommonEnchant) return EnchantRarity.UNCOMMON;
		return EnchantRarity.COMMON;
	}
}