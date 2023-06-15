package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.UnlockableRenownUpgrade;
import net.pitsim.spigot.inventories.HelmetryPanel;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Helmetry extends UnlockableRenownUpgrade {
	public static Helmetry INSTANCE;

	public Helmetry() {
		super("Helmetry", "HELMETRY", 15, HelmetryPanel.class);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.GOLD_HELMET)
				.getItemStack();
	}

	@Override
	public String getEffect() {
		return "&7Unlock the ability to craft &6Golden Helmets &7for &e10 Renown &7each. " +
				"They can be upgraded by putting &6gold &7into them";
	}

	@Override
	public String getSummary() {
		return "&eHelmetry&7 is a &erenown upgrade that sells you a &6Golden Helmet&7 for&e 5 renown&7, which grants " +
				"various buffs based on how much &6gold&7 is in it";
	}

	@Override
	public int getUnlockCost() {
		return 25;
	}
}
