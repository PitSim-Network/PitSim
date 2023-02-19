package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitBlaze;
import dev.kyro.pitsim.adarkzone.mobs.PitSkeleton;
import dev.kyro.pitsim.controllers.objects.CommonDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class WhoNeedsBows extends CommonDarkzoneEnchant {
	public static WhoNeedsBows INSTANCE;

	public WhoNeedsBows() {
		super("Who Needs Bows?", false, ApplyType.SCYTHES,
				"whoneedsbows", "whatsabow");
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
		return Arrays.asList(PitSkeleton.class, PitBlaze.class);
	}
}
