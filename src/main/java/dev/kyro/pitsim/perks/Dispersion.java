package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dispersion extends PitPerk {
	public static Dispersion INSTANCE;

	public Dispersion() {
		super("Dispersion", "dispersion");
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer() || !hasPerk(attackEvent.getDefenderPlayer())) return;

		if(MapManager.currentMap.world != attackEvent.getDefenderPlayer().getWorld()) return;
		if(MapManager.currentMap.getMid().distance(attackEvent.getDefenderPlayer().getLocation()) > getRange()) return;

		List<PitEnchant> toRemove = new ArrayList<>();
		for(Map.Entry<PitEnchant, Integer> entry : attackEvent.getAttackerEnchantMap().entrySet()) {
			if(Math.random() > getChance() / 100.0) continue;
			toRemove.add(entry.getKey());
		}
		for(PitEnchant pitEnchant : toRemove) attackEvent.getAttackerEnchantMap().remove(pitEnchant);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.WEB)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&dDisperse " + getChance() + "% &7of the enchants on your opponent's attacks while in middle"
		);
	}

	@Override
	public String getSummary() {
		return "&aDispersion &7is a perk that spreads out the enchants of your opponents attacks in middle";
	}

	public static int getRange() {
		return 10;
	}

	public static int getChance() {
		return 30;
	}
}
