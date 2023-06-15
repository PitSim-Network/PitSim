package net.pitsim.spigot.enchants.tainted.uncommon;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartingHand extends PitEnchant {
	public static StartingHand INSTANCE;
	public static Map<Player, List<LivingEntity>> hitPlayers = new HashMap<>();

	public StartingHand() {
		super("Starting Hand", false, ApplyType.SCYTHES,
				"startinghand", "starting", "start");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		int enchantLvl = EnchantManager.getEnchantLevel(attackEvent.getAttackerPlayer(), this);
		if(enchantLvl == 0) return;

		hitPlayers.putIfAbsent(attackEvent.getAttackerPlayer(), new ArrayList<>());
		List<LivingEntity> hitList = hitPlayers.get(attackEvent.getAttackerPlayer());

		if(hitList.contains(attackEvent.getDefender())) return;

		if(Math.random() < getGoodLuckChance(enchantLvl) / 100.0) {
			attackEvent.multipliers.add(2.0);
			Sounds.GAMBLE_YES.play(attackEvent.getAttackerPlayer());
		} else {
			attackEvent.multipliers.add(0.5);
			Sounds.GAMBLE_NO.play(attackEvent.getAttackerPlayer());
		}

		hitPlayers.get(attackEvent.getAttackerPlayer()).add(attackEvent.getDefender());
		new BukkitRunnable() {
			@Override
			public void run() {
				hitPlayers.get(attackEvent.getAttackerPlayer()).remove(attackEvent.getDefender());
			}
		}.runTaskLater(PitSim.INSTANCE, 120L);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&e" + getGoodLuckChance(enchantLvl) + "% &7chance for your first strike on a mob to deal &cdouble " +
						"damage &7instead of &chalf damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"starts off your encounters with opponents with either a very high damage or very low damage attack";
	}

	public static int getGoodLuckChance(int enchantLvl) {
		return enchantLvl * 18 + 16;
	}
}
