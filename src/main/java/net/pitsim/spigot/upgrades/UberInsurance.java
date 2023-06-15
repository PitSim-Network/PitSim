package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.UpgradeManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.controllers.objects.TieredRenownUpgrade;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.megastreaks.Uberstreak;
import net.pitsim.spigot.misc.PlayerItemLocation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UberInsurance extends TieredRenownUpgrade {
	public static UberInsurance INSTANCE;

	public UberInsurance() {
		super("Uber Insurance", "LIFE_INSURANCE", 20);
		INSTANCE = this;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isDeadPlayer() || !isApplicable(killEvent.getDeadPlayer())) return;
		for(Map.Entry<PlayerItemLocation, KillEvent.ItemInfo> entry : new ArrayList<>(killEvent.getVulnerableItems().entrySet())) {
			KillEvent.ItemInfo itemInfo = entry.getValue();
			if(!itemInfo.pitItem.isMystic) continue;
			killEvent.removeVulnerableItem(entry.getKey());
		}
	}

	public static boolean isApplicable(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		int tier = UpgradeManager.getTier(player, INSTANCE);

		if(!(pitPlayer.getMegastreak() instanceof Uberstreak)) return false;
		if(pitPlayer.getKills() >= 400 && tier >= 3) return true;
		if(pitPlayer.getKills() >= 450 && tier >= 2) return true;
		if(pitPlayer.getKills() >= 500 && tier >= 1) return true;
		return false;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.BOOK_AND_QUILL)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&7Retain lives on &dUber " + Math.max(550 - tier * 50, 100);
	}

	@Override
	public String getEffectPerTier() {
		return "&7Retain lives on &dMystics &7when completing &dUberstreaks &750 kills earlier";
	}

	@Override
	public String getSummary() {
		return "&dUber Insurance &7is a &erenown&7 perk that saves your lives while on an &dUberstreak&7 " +
				"based on how many &ckills&7 you got";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(40, 75, 150);
	}
}
