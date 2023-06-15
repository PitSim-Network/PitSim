package net.pitsim.pitsim.controllers.objects;

import net.pitsim.pitsim.adarkzone.DarkzoneBalancing;
import net.pitsim.pitsim.adarkzone.DarkzoneManager;
import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicDarkzoneEnchant extends PitEnchant {
	public BasicDarkzoneEnchant(String name, boolean isRare, ApplyType applyType, String... refNames) {
		super(name, isRare, applyType, refNames);
	}

	public abstract int getBaseStatPercent(int enchantLvl);
	public abstract boolean isOffensive();
	public abstract List<Class<? extends PitMob>> getApplicableMobs();

	public int getStatPercent(int enchantLvl) {
		return (int) Math.round(getBaseStatPercent(enchantLvl) * DarkzoneBalancing.BASIC_DARKZONE_ENCHANT_MULTIPLIER);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		PitMob pitMob = DarkzoneManager.getPitMob(attackEvent.getDefender());
		if(pitMob == null || !getApplicableMobs().contains(pitMob.getClass())) return;

		if(isOffensive()) {
			int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
			if(enchantLvl != 0) attackEvent.increasePercent += getStatPercent(enchantLvl);
		} else {
			int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
			if(enchantLvl != 0) attackEvent.multipliers.add(Misc.getReductionMultiplier(getStatPercent(enchantLvl)));
		}
	}

	public List<String> createDescription(int statPercent) {
		String description;
		if(isOffensive()) {
			description = "&7Deal &c+" + statPercent + "% &7damage vs ";
		} else {
			description = "&7Receive &9-" + statPercent + "% &7damage from ";
		}

		description += getMobNames();
		return new PitLoreBuilder(description).getLore();
	}

	@Override
	public String getSummary() {
		if(isOffensive()) {
			return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
					"increases the damage you deal vs " + getMobNames();
		} else {
			return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
					"decreases the damage you take from " + getMobNames();
		}
	}

	public String getMobNames() {
		List<String> mobNames = new ArrayList<>();
		for(Class<? extends PitMob> mobClass : getApplicableMobs()) {
			mobNames.add(DarkzoneManager.getDummyMob(mobClass).getDisplayNamePlural().replaceAll("\u00A70", "\u00A78"));
		}
		if(mobNames.size() == 1) {
			return mobNames.get(0);
		} else if(mobNames.size() == 2) {
			return String.join(" &7and ", mobNames);
		} else {
			String lastMob = mobNames.remove(mobNames.size() - 1);
			return String.join("&7, ", mobNames) + "&7, and " + lastMob;
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return createDescription(getStatPercent(enchantLvl));
	}
}
