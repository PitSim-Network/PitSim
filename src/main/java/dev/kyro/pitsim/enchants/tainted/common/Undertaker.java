package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitSkeleton;
import dev.kyro.pitsim.adarkzone.mobs.PitWitherSkeleton;
import dev.kyro.pitsim.adarkzone.mobs.PitZombie;
import dev.kyro.pitsim.adarkzone.mobs.PitZombiePigman;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Undertaker extends BasicDarkzoneEnchant {
	public static Undertaker INSTANCE;

	public Undertaker() {
		super("Undertaker", false, ApplyType.SCYTHES,
				"undertaker", "undertake", "under");
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
		return Arrays.asList(PitZombie.class, PitSkeleton.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
