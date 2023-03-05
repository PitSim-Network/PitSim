package dev.kyro.pitsim.enchants.tainted.uncommon.basic;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.*;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Guard extends BasicDarkzoneEnchant {
	public static Guard INSTANCE;

	public Guard() {
		super("Guard", false, ApplyType.CHESTPLATES,
				"guard", "shield1");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getStatPercent(int enchantLvl) {
		return enchantLvl * 5 + 4;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitZombie.class, PitSkeleton.class, PitSpider.class, PitWolf.class);
	}
}
