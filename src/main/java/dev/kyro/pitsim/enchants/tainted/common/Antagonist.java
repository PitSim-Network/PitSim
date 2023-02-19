package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitIronGolem;
import dev.kyro.pitsim.adarkzone.mobs.PitWolf;
import dev.kyro.pitsim.controllers.objects.CommonDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Antagonist extends CommonDarkzoneEnchant {
	public static Antagonist INSTANCE;

	public Antagonist() {
		super("Antagonist", false, ApplyType.SCYTHES,
				"antagonist");
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
		return Arrays.asList(PitWolf.class, PitIronGolem.class);
	}
}
