package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.adarkzone.PitMob;
import net.pitsim.spigot.adarkzone.mobs.*;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class ShadowCloak extends BasicDarkzoneEnchant {
	public static ShadowCloak INSTANCE;

	public ShadowCloak() {
		super("Shadow Cloak", false, ApplyType.CHESTPLATES,
				"shadowcloak", "cloak");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 3 + 4;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitZombie.class, PitSkeleton.class, PitSpider.class, PitCreeper.class, PitEnderman.class);
	}
}
