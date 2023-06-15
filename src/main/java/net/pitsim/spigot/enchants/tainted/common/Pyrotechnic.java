package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.adarkzone.PitMob;
import net.pitsim.spigot.adarkzone.mobs.PitBlaze;
import net.pitsim.spigot.adarkzone.mobs.PitWitherSkeleton;
import net.pitsim.spigot.adarkzone.mobs.PitZombiePigman;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Pyrotechnic extends BasicDarkzoneEnchant {
	public static Pyrotechnic INSTANCE;

	public Pyrotechnic() {
		super("Pyrotechnic", false, ApplyType.CHESTPLATES,
				"pyrotechnic", "pyro");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 4 + 4;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitBlaze.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
