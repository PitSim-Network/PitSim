package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitSkeleton;
import dev.kyro.pitsim.adarkzone.mobs.PitWitherSkeleton;
import dev.kyro.pitsim.adarkzone.mobs.PitZombie;
import dev.kyro.pitsim.adarkzone.mobs.PitZombiePigman;
import dev.kyro.pitsim.controllers.objects.CommonDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Embalm extends CommonDarkzoneEnchant {
	public static Embalm INSTANCE;

	public Embalm() {
		super("Embalm", false, ApplyType.CHESTPLATES,
				"embalm");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getStatPercent(int enchantLvl) {
		return enchantLvl * 10;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitZombie.class, PitSkeleton.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
