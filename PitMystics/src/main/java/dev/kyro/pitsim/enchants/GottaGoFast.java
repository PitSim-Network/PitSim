package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class GottaGoFast extends PitEnchant {
	public static GottaGoFast INSTANCE;

	public GottaGoFast() {
		super("Gotta go fast", false, ApplyType.PANTS,
				"gotta-go-fast", "gottagofast", "gtgf", "gotta", "fast");
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);

		attackEvent.multiplier.add(Misc.getReductionMultiplier(getDamageReduction(enchantLvl)));
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int level = EnchantManager.getEnchantLevel(player, INSTANCE);

					if(level == 0) player.setWalkSpeed(0.2F);
					if(level == 1) player.setWalkSpeed(0.2F + (0.2F * 0.04F));
					if(level == 2) player.setWalkSpeed(0.2F + (0.2F * 0.1F));
					if(level == 3) player.setWalkSpeed(0.2F + (0.2F * 0.2F));

					if(level != 0) {
						player.getWorld().spigot().playEffect(player.getLocation().subtract(0, 1, 0),
								Effect.SMOKE, 0, 0, (float) 0.5, (float) 0.5, (float) 0.5, (float) 0.01, 20, 50);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) + "% &7damage").getLore();
	}

	public double getDamageReduction(int enchantLvl) {

		return (int) Math.floor(Math.pow(enchantLvl, 1.3) * 2) + 2;
	}
}
