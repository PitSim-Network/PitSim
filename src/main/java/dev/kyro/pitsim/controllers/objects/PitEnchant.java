package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
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
	public boolean isTainted = false;
	public Map<UUID, Cooldown> cooldowns = new HashMap<>();

	public PitEnchant(String name, boolean isRare, ApplyType applyType, String... refNames) {
		this.name = name;
		this.refNames = Arrays.asList(refNames);
		this.isRare = isRare;
		this.applyType = applyType;
		this.isUncommonEnchant = isRare;
	}

	public abstract List<String> getNormalDescription(int enchantLvl);

	public List<String> getDisabledDescription() {
		return new ALoreBuilder(
				"&7Disabled in the " + (isTainted ? "&aOverworld" : "&5Darkzone")
		).getLore();
	}

	public List<String> getDescription(int enchantLvl) {
		if(isTainted) {
			return PitSim.getStatus() == PitSim.ServerStatus.OVERWORLD ? getDisabledDescription() : getNormalDescription(enchantLvl);
		} else {
			return PitSim.getStatus() == PitSim.ServerStatus.DARKZONE ? getDisabledDescription() : getNormalDescription(enchantLvl);
		}
	}

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
		return getDisplayName(false);
	}

	public String getDisplayName(boolean displayUncommon) {
		String displayName = "";
		if(isRare) {
			if(applyType == ApplyType.SCYTHES) displayName += "&dSPELL!";
			else if(applyType == ApplyType.CHESTPLATES) displayName += "&dEFFECT!";
			else displayName += "&dRARE!";
		} else if(isUncommonEnchant && displayUncommon) {
			displayName += "&aUNC.";
		}
		if(isTainted) {
			displayName += PitSim.getStatus() != PitSim.ServerStatus.OVERWORLD ? "&5" : "&c";
		} else {
			displayName += PitSim.getStatus() != PitSim.ServerStatus.DARKZONE ? "&9" : "&c";
		}
		return ChatColor.translateAlternateColorCodes('&', displayName + " " + name);
	}

	public EnchantRarity getRarity() {
		if(isRare) return EnchantRarity.RARE;
		if(isUncommonEnchant) return EnchantRarity.UNCOMMON;
		return EnchantRarity.COMMON;
	}
}
