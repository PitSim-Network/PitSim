package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.adarkzone.PitMob;
import net.pitsim.spigot.adarkzone.mobs.PitCreeper;
import net.pitsim.spigot.adarkzone.mobs.PitEnderman;
import net.pitsim.spigot.adarkzone.mobs.PitSpider;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

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
