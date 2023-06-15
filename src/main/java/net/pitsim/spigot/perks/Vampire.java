package net.pitsim.spigot.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.PitPerk;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.events.HealEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class Vampire extends PitPerk {
	public static Vampire INSTANCE;

	public static double initialHealing = 1;

	public Vampire() {
		super("Vampire", "vampire");
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!hasPerk(attackEvent.getAttacker())) return;

		double healing = initialHealing;
		if(attackEvent.getArrow() != null && attackEvent.getArrow().isCritical()) healing *= 2;
		HealEvent healEvent = attackEvent.getAttackerPitPlayer().heal(healing);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.FERMENTED_SPIDER_EYE)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&7Heal &c" + Misc.getHearts(initialHealing) + " &7on hit. Doubled on arrow crit"
		);
	}

	@Override
	public String getSummary() {
		return "&aVampire &7is a perk that heals you on hit";
	}
}
