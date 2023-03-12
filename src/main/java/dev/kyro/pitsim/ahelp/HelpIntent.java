package dev.kyro.pitsim.ahelp;

import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPerk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum HelpIntent implements Summarizable {
	WHAT_IS_THE_DARKZONE("I'm going to murder you");

	private final String summary;

	HelpIntent(String summary) {
		this.summary = summary;
	}

	public static String getIdentifier(Summarizable summarizable) {
		if(summarizable instanceof HelpIntent) {
			HelpIntent helpIntent = (HelpIntent) summarizable;
			return helpIntent.name();
		} else if(summarizable instanceof PitEnchant) {
			PitEnchant pitEnchant = (PitEnchant) summarizable;
			return "ENCHANT_" + pitEnchant.refNames.get(0).toUpperCase().replaceAll("[- ]", "_");
		} else if(summarizable instanceof PitPerk) {
			PitPerk pitPerk = (PitPerk) summarizable;
			return "PERK_" + pitPerk.refName.toUpperCase().replaceAll("[- ]", "_");
		} else if(summarizable instanceof Killstreak) {
			Killstreak killstreak = (Killstreak) summarizable;
			return "KILLSTREAK_" + killstreak.refName.toUpperCase().replaceAll("[- ]", "_");
		} else if(summarizable instanceof Megastreak) {
			Megastreak megastreak = (Megastreak) summarizable;
			return "MEGASTREAK_" + megastreak.getRawName().toUpperCase().replaceAll("[- ]", "_");
		}
		throw new RuntimeException();
	}

	public static String getReply(String identifier) {
		for(HelpIntent value : values()) if(identifier.equals(value.name())) return value.summary;

		List<Summarizable> summarizables = new ArrayList<>(Arrays.asList(values()));
//		summarizables.addAll(EnchantManager.pitEnchants);
//		summarizables.addAll(PerkManager.pitPerks);
//		summarizables.addAll(PerkManager.killstreaks);
//		summarizables.addAll(PerkManager.megastreaks);

		for(Summarizable summarizable : summarizables) {
			String testIdentifier = getIdentifier(summarizable);
			if(identifier.equals(testIdentifier)) return summarizable.getSummary();
		}
		throw new RuntimeException("Could not find identifier: " + identifier);
	}

	@Override
	public String getSummary() {
		return summary;
	}
}
