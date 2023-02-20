package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitBlaze;
import dev.kyro.pitsim.adarkzone.mobs.PitWitherSkeleton;
import dev.kyro.pitsim.adarkzone.mobs.PitZombiePigman;
import dev.kyro.pitsim.controllers.objects.CommonDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Extinguish extends CommonDarkzoneEnchant {
	public static Extinguish INSTANCE;

	public Extinguish() {
		super("Extinguish", false, ApplyType.SCYTHES,
				"extinguish");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getStatPercent(int enchantLvl) {
		return enchantLvl * 10;
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
