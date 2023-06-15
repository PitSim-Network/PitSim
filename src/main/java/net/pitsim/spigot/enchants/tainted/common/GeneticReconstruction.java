package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.darkzone.PitMob;
import net.pitsim.spigot.darkzone.mobs.PitWitherSkeleton;
import net.pitsim.spigot.darkzone.mobs.PitZombiePigman;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class GeneticReconstruction extends BasicDarkzoneEnchant {
	public static GeneticReconstruction INSTANCE;

	public GeneticReconstruction() {
		super("Genetic Reconstruction", false, ApplyType.CHESTPLATES,
				"geneticreconstruction", "gene", "genetic", "reconstruction", "reconstruct");
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
		return Arrays.asList(PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
