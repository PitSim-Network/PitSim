package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dispersion extends PitPerk {
	public static Dispersion INSTANCE;

	public Dispersion() {
		super("Dispersion", "dispersion", new ItemStack(Material.WEB), 22, false, "", INSTANCE, false);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.attackerIsPlayer || !attackEvent.defenderIsPlayer) return;
		if(!playerHasUpgrade(attackEvent.defenderPlayer)) return;

		if(!MapManager.currentMap.lobbies.contains(attackEvent.defenderPlayer.getWorld())) return;
		if(MapManager.currentMap.getMid(attackEvent.defender.getWorld()).distance(attackEvent.defenderPlayer.getLocation()) > getRange()) return;

		List<PitEnchant> toRemove = new ArrayList<>();
		for(Map.Entry<PitEnchant, Integer> entry : attackEvent.getAttackerEnchantMap().entrySet()) {
			if(Math.random() > getChance() / 100.0) continue;
			toRemove.add(entry.getKey());
		}
		for(PitEnchant pitEnchant : toRemove) attackEvent.getAttackerEnchantMap().remove(pitEnchant);
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&dDisperse " + getChance() + "% &7of the enchants",
				"&7on your opponent's attacks for",
				"&7while in middle").getLore();
	}

	public static int getRange() {
		return 8;
	}

	public static int getChance() {
		return 60;
	}
}
