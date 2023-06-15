package net.pitsim.pitsim.enchants.tainted.common;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitCreeper;
import net.pitsim.pitsim.adarkzone.mobs.PitSpider;
import net.pitsim.pitsim.adarkzone.mobs.PitWolf;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Huggable extends BasicDarkzoneEnchant {
	public static Huggable INSTANCE;

	public Huggable() {
		super("Huggable", false, ApplyType.CHESTPLATES,
				"huggable", "hug");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 5 + 5;
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
