package net.pitsim.pitsim.enchants.tainted.uncommon.basic;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.*;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Hunter extends BasicDarkzoneEnchant {
	public static Hunter INSTANCE;

	public Hunter() {
		super("Hunter", false, ApplyType.SCYTHES,
				"hunter", "hunt");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 11 + 11;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitZombie.class, PitSkeleton.class, PitSpider.class, PitWolf.class);
	}
}
