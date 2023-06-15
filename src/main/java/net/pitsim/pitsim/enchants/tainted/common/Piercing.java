package net.pitsim.pitsim.enchants.tainted.common;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitBlaze;
import net.pitsim.pitsim.adarkzone.mobs.PitIronGolem;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Piercing extends BasicDarkzoneEnchant {
	public static Piercing INSTANCE;

	public Piercing() {
		super("Piercing", false, ApplyType.SCYTHES,
				"piercing", "pierce");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 9 + 9;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitBlaze.class, PitIronGolem.class);
	}
}
