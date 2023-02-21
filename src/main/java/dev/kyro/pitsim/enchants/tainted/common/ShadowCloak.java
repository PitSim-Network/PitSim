package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.*;
import dev.kyro.pitsim.controllers.objects.CommonDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class ShadowCloak extends CommonDarkzoneEnchant {
	public static ShadowCloak INSTANCE;

	public ShadowCloak() {
		super("Shadow Cloak", false, ApplyType.CHESTPLATES,
				"shadowcloak", "cloak");
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
		return Arrays.asList(PitZombie.class, PitSkeleton.class, PitSpider.class, PitCreeper.class, PitEnderman.class);
	}
}
