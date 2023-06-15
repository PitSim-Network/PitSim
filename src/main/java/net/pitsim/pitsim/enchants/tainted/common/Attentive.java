package net.pitsim.pitsim.enchants.tainted.common;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitCreeper;
import net.pitsim.pitsim.adarkzone.mobs.PitEnderman;
import net.pitsim.pitsim.adarkzone.mobs.PitSpider;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

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
	public int getBaseStatPercent(int enchantLvl) {
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
