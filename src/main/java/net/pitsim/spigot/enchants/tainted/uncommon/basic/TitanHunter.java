package net.pitsim.spigot.enchants.tainted.uncommon.basic;

import net.pitsim.spigot.darkzone.PitMob;
import net.pitsim.spigot.darkzone.mobs.PitCreeper;
import net.pitsim.spigot.darkzone.mobs.PitEnderman;
import net.pitsim.spigot.darkzone.mobs.PitIronGolem;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class TitanHunter extends BasicDarkzoneEnchant {
	public static TitanHunter INSTANCE;

	public TitanHunter() {
		super("Titan Hunter", false, ApplyType.SCYTHES,
				"titanhunter", "titan", "hunter3", "hunt3");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 11 + 15
				;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitCreeper.class, PitIronGolem.class, PitEnderman.class);
	}
}
