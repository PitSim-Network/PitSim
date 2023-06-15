package net.pitsim.spigot.controllers.objects;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.ahelp.Summarizable;
import net.pitsim.spigot.controllers.Cooldown;
import net.pitsim.spigot.enchants.overworld.Regularity;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.enums.EnchantRarity;
import net.pitsim.spigot.aserverstatistics.StatisticCategory;
import net.pitsim.spigot.events.AttackEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public abstract class PitEnchant implements Listener, Summarizable {
	public String name;
	public List<String> refNames;
	public boolean isRare;
	public ApplyType applyType;
	public boolean isUncommonEnchant;
	public boolean levelStacks;
	public boolean meleeOnly;
	public boolean fakeHits;
	public boolean isTainted;
	public Map<UUID, Cooldown> cooldowns = new HashMap<>();

//	Server Statistics Information
	public List<StatisticCategory> statisticCategories;

	public PitEnchant(String name, boolean isRare, ApplyType applyType, String... refNames) {
		this.name = name;
		this.refNames = Arrays.asList(refNames);
		this.isRare = isRare;
		this.applyType = applyType;
		this.isUncommonEnchant = isRare;
	}

	public abstract List<String> getNormalDescription(int enchantLvl);

	public boolean isEnabled() {
		if(PitSim.status == PitSim.ServerStatus.STANDALONE) return true;
		if(isTainted) {
			return PitSim.getStatus() == PitSim.ServerStatus.DARKZONE;
		} else {
			return PitSim.getStatus() == PitSim.ServerStatus.OVERWORLD || applyType == ApplyType.BOWS;
		}
	}

	public List<String> getDisabledDescription() {
		return new ALoreBuilder(
				"&7Disabled in the " + (isTainted ? "&aOverworld" : "&5Darkzone")
		).getLore();
	}

	public List<String> getDescription(int enchantLvl) {
		return getDescription(enchantLvl, false);
	}

	public List<String> getDescription(int enchantLvl, boolean overrideDisableCheck) {
		return isEnabled() || overrideDisableCheck ? getNormalDescription(enchantLvl) : getDisabledDescription();
	}

	public String getDisplayName() {
		return getDisplayName(false);
	}

	public String getDisplayName(boolean displayUncommon) {
		return getDisplayName(displayUncommon, false);
	}

	public String getDisplayName(boolean displayUncommon, boolean overrideDisableCheck) {
		String prefix = "";
		if(isRare) {
			prefix += "&dRARE! ";
		} else if(isUncommonEnchant && (displayUncommon || isTainted)) {
			prefix += "&aUNC. ";
		}
		prefix += (isEnabled() || overrideDisableCheck ? "&9" : "&c");
		return ChatColor.translateAlternateColorCodes('&', prefix + name);
	}

	public void onDisable() {
	}

	public PitEnchant setDefaultCategories() {
		if(statisticCategories != null) return this;
		statisticCategories = new ArrayList<>();
		if(isTainted || applyType == ApplyType.BOWS) {
			statisticCategories.add(StatisticCategory.DARKZONE_VS_PLAYER);
			statisticCategories.add(StatisticCategory.DARKZONE_VS_MOB);
			statisticCategories.add(StatisticCategory.DARKZONE_VS_BOSS);
		}
		if(!isTainted) {
			statisticCategories.add(StatisticCategory.OVERWORLD_PVP);
			statisticCategories.add(StatisticCategory.OVERWORLD_STREAKING);
		}
		return this;
	}

	public boolean canApply(AttackEvent attackEvent) {

		if(!fakeHits && Regularity.isRegHit(attackEvent.getDefender()) && attackEvent.isFakeHit()) return false;
//		Skip if fake hit and enchant doesn't handle fake hits
//		if(!fakeHits && attackEvent.fakeHit) return false;
//		Skip enchant application if the enchant is a bow enchant and is used in mele
		if(applyType == ApplyType.BOWS && attackEvent.getArrow() == null) return false;
//		Skips enchant application if the enchant only works on mele hit and the event is from an arrow
		if(meleeOnly && attackEvent.getArrow() != null) return false;
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

	public EnchantRarity getRarity() {
		if(isRare) return EnchantRarity.RARE;
		if(isUncommonEnchant) return EnchantRarity.UNCOMMON;
		return EnchantRarity.COMMON;
	}

	@Override
	public String getIdentifier() {
		return "ENCHANT_" + refNames.get(0).toUpperCase().replaceAll("[- ]", "_");
	}

	@Override
	public List<String> getTrainingPhrases() {
		List<String> trainingPhrases = new ArrayList<>();
		trainingPhrases.add("what is " + ChatColor.stripColor(getDisplayName()) + "?");
		trainingPhrases.add("what does " + ChatColor.stripColor(getDisplayName()) + " do?");
		return trainingPhrases;
	}
}
