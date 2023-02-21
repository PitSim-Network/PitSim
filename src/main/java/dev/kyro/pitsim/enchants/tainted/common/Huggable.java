package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitCreeper;
import dev.kyro.pitsim.adarkzone.mobs.PitSpider;
import dev.kyro.pitsim.adarkzone.mobs.PitWolf;
import dev.kyro.pitsim.controllers.objects.CommonDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Huggable extends CommonDarkzoneEnchant {
	public static Huggable INSTANCE;

	public Huggable() {
		super("Huggable", false, ApplyType.CHESTPLATES,
				"huggable", "hug");
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
		return Arrays.asList(PitSpider.class, PitWolf.class, PitCreeper.class);
	}
}
