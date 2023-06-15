package net.pitsim.pitsim.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.controllers.UpgradeManager;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.controllers.objects.TieredRenownUpgrade;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Tenacity extends TieredRenownUpgrade {
	public static Tenacity INSTANCE;

	public Tenacity() {
		super("Tenacity", "TENACITY", 1);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || !killEvent.isDeadPlayer()) return;
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;

		int tier = UpgradeManager.getTier(killEvent.getKillerPlayer(), this);
		if(tier == 0) return;

		PitPlayer pitKiller = killEvent.getKillerPitPlayer();
		pitKiller.heal(tier);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.MAGMA_CREAM)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&7Heal &c" + Misc.getHearts(tier) + " &7on kill";
	}

	@Override
	public String getEffectPerTier() {
		return getCurrentEffect(1);
	}

	@Override
	public String getSummary() {
		return "Tenacity is a &erenown &7upgrade that &cheals you &7when you kill a bot or player, which is very useful for streaking";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 50);
	}
}
