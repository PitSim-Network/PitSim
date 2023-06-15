package net.pitsim.pitsim.enchants.tainted.common;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitCreeper;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class BOOM extends BasicDarkzoneEnchant {
	public static BOOM INSTANCE;

	public BOOM() {
		super("BOOM!", false, ApplyType.CHESTPLATES,
				"boom");
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
		return Arrays.asList(PitCreeper.class);
	}
}
