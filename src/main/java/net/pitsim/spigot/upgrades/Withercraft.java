package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.UnlockableRenownUpgrade;
import net.pitsim.spigot.inventories.WithercraftPanel;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Withercraft extends UnlockableRenownUpgrade {
	public static Withercraft INSTANCE;

	public Withercraft() {
		super("Withercraft", "WITHERCRAFT", 18, WithercraftPanel.class);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.COAL)
				.getItemStack();
	}

	@Override
	public String getEffect() {
		return "&7Abililty to right-click a &5Chunk of Vile &7and sacrifice it to repair a life on a &3Jewel &7item";
	}

	@Override
	public String getSummary() {
		return "&eWithercraft is a &erenown&7 upgrade that lets you use &5Chunks of Vile&7 to repair lives on &3Jewel &7items";
	}

	@Override
	public int getUnlockCost() {
		return 50;
	}
}
