package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitBlaze;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Evasive extends BasicDarkzoneEnchant {
	public static Evasive INSTANCE;

	public Evasive() {
		super("Evasive", false, ApplyType.CHESTPLATES,
				"evasive");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 6 + 4;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitBlaze.class);
	}
}
