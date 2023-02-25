package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class CommonDarkzoneEnchant extends PitEnchant {
	public CommonDarkzoneEnchant(String name, boolean isRare, ApplyType applyType, String... refNames) {
		super(name, isRare, applyType, refNames);
	}

	public abstract int getStatPercent(int enchantLvl);
	public abstract boolean isOffensive();
	public abstract List<Class<? extends PitMob>> getApplicableMobs();

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

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

		List<String> mobNames = new ArrayList<>();
		for(Class<? extends PitMob> mobClass : getApplicableMobs()) {
			try {
				Constructor<? extends PitMob> constructor = mobClass.getConstructor(Location.class);
				PitMob pitMob = constructor.newInstance((Object) null);
				mobNames.add(pitMob.getDisplayNamePlural().replaceAll("\u00A70", "\u00A78"));
			} catch(Exception exception) {
				exception.printStackTrace();
				throw new RuntimeException();
			}
		}
		if(mobNames.size() == 1) {
			description += mobNames.get(0);
		} else if(mobNames.size() == 2) {
			description += String.join(" &7and ", mobNames);
		} else {
			String lastMob = mobNames.remove(mobNames.size() - 1);
			description += String.join("&7, ", mobNames) + "&7, and " + lastMob;
		}
		return new PitLoreBuilder(description).getLore();
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return createDescription(getStatPercent(enchantLvl));
	}
}
