package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.UpgradeManager;
import net.pitsim.spigot.controllers.objects.UnlockableRenownUpgrade;
import net.pitsim.spigot.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class XPComplex extends UnlockableRenownUpgrade {
	public static XPComplex INSTANCE;

	public XPComplex() {
		super("Experience-Industrial Complex", "XP_COMPLEX", 23);
		INSTANCE = this;
	}

	@Override
	public String getSummary() {
		return "&eExperience-Industrial Complex&7 is a &erenown&7 upgrade that increases your max &bXP";
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.DIAMOND_BARDING)
				.getItemStack();
	}

	@Override
	public String getEffect() {
		return "&7Gain &b+150 max XP";
	}

	@Override
	public int getUnlockCost() {
		return 50;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;

		killEvent.xpCap += 150;
	}
}
