package net.pitsim.pitsim.enchants.tainted.uncommon.basic;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitCreeper;
import net.pitsim.pitsim.adarkzone.mobs.PitEnderman;
import net.pitsim.pitsim.adarkzone.mobs.PitIronGolem;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Barricade extends BasicDarkzoneEnchant {
	public static Barricade INSTANCE;

	public Barricade() {
		super("Barricade", false, ApplyType.CHESTPLATES,
				"barricade", "shield3");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 5 + 6;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitCreeper.class, PitIronGolem.class, PitEnderman.class);
	}
}
