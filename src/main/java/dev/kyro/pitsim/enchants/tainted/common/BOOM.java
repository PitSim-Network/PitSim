package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitCreeper;
import dev.kyro.pitsim.controllers.objects.CommonDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class BOOM extends CommonDarkzoneEnchant {
	public static BOOM INSTANCE;

	public BOOM() {
		super("BOOM!", false, ApplyType.CHESTPLATES,
				"boom");
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
		return Arrays.asList(PitCreeper.class);
	}
}
