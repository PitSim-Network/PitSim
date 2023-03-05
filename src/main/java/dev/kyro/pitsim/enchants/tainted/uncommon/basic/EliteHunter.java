package dev.kyro.pitsim.enchants.tainted.uncommon.basic;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitBlaze;
import dev.kyro.pitsim.adarkzone.mobs.PitWitherSkeleton;
import dev.kyro.pitsim.adarkzone.mobs.PitZombiePigman;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class EliteHunter extends BasicDarkzoneEnchant {
	public static EliteHunter INSTANCE;

	public EliteHunter() {
		super("Elite Hunter", false, ApplyType.SCYTHES,
				"elitehunter", "elite", "hunter2", "hunt2");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getStatPercent(int enchantLvl) {
		return enchantLvl * 11 + 12;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitBlaze.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
