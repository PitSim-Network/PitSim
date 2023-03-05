package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitCreeper;
import dev.kyro.pitsim.adarkzone.mobs.PitEnderman;
import dev.kyro.pitsim.adarkzone.mobs.PitSpider;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Attentive extends BasicDarkzoneEnchant {
	public static Attentive INSTANCE;

	public Attentive() {
		super("Attentive", false, ApplyType.CHESTPLATES,
				"attentive");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getStatPercent(int enchantLvl) {
		return enchantLvl * 5 + 3;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitSpider.class, PitCreeper.class, PitEnderman.class);
	}
}
