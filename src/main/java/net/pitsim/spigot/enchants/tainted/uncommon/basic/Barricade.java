package net.pitsim.spigot.enchants.tainted.uncommon.basic;

import net.pitsim.spigot.darkzone.PitMob;
import net.pitsim.spigot.darkzone.mobs.PitCreeper;
import net.pitsim.spigot.darkzone.mobs.PitEnderman;
import net.pitsim.spigot.darkzone.mobs.PitIronGolem;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

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
