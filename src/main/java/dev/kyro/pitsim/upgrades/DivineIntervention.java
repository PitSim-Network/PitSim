package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class DivineIntervention extends TieredRenownUpgrade {
	public static DivineIntervention INSTANCE;

	public DivineIntervention() {
		super("Divine Intervention", "DIVINE_INTERVENTION", 16);
		INSTANCE = this;
	}

	public static boolean attemptDivine(Player player) {
		if(!UpgradeManager.hasUpgrade(player, INSTANCE)) return false;

		int tier = UpgradeManager.getTier(player, INSTANCE);
		if(tier == 0) return false;

		double chance = 0.01 * (tier * 5);

		boolean isDouble = Math.random() < chance;

		if(isDouble) {
			AOutput.send(player, "&b&lDIVINE INTERVENTION!&7 Inventory saved!");

			Sounds.SoundMoment soundMoment = new Sounds.SoundMoment(3);
			soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 1.5);
			soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 1.6);
			soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 1.7);
			soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 1.7);
			soundMoment.play(player);
		}

		return isDouble;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.QUARTZ_STAIRS)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&e" + (tier * 5) + "% chance";
	}

	@Override
	public String getEffectPerTier() {
		return "&e+5% chance &7to keep your inventory on death";
	}

	@Override
	public String getSummary() {
		return "&eDivine Intervention&7 is a &erenown&7 upgrade  gives you a small chance to save your inventory on death";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(25, 50, 75, 100, 125);
	}
}
