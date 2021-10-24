package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class aCPLEnchant extends PitEnchant {
	public static aCPLEnchant INSTANCE;

	public aCPLEnchant() {
		super("The King's Sword", true, ApplyType.NONE,
				"theking");
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int level = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(level == 0 || !isWorthy(player)) continue;

					if(Math.random() < 0.02) {
						Location effectLoc = player.getLocation().add(Math.random() * 6 - 3, 0, Math.random() * 6 - 3);
						player.getWorld().strikeLightningEffect(effectLoc);
					} else {
						player.getWorld().spigot().playEffect(player.getLocation().add(0, 1, 0),
								Effect.HAPPY_VILLAGER, 0, 0, (float) 5, (float) 5, (float) 5, (float) 0.01, 3, 50);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0 || !isWorthy(attackEvent.attacker)) return;

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);
		Non defendingNon = NonManager.getNon(attackEvent.defender);
		if(defendingNon != null) return;

//		if(Math.random() < 0.25 * enchantLvl) {
//
//			pitAttacker.heal(4);
//			AOutput.send(attackEvent.attacker, "You were blessed");
//			ASound.play(attackEvent.attacker, Sound.BURP, 1, 1);
//		}
//
//		if(Math.random() < 0.2 * enchantLvl) {
//
//			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.INCREASE_DAMAGE, 40, 0, true, false);
//			AOutput.send(attackEvent.attacker, "The power of the gods flows through you for a brief moment");
//			ASound.play(attackEvent.attacker, Sound.ENDERMAN_SCREAM, 1, 1);
//		}
//
//		if(Math.random() < 0.15 * enchantLvl) {
//
//			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.WITHER, 100, 2, true, false);
//			AOutput.send(attackEvent.attacker, "Your foe suffers at the wrath of your will");
//			ASound.play(attackEvent.attacker, Sound.WITHER_SHOOT, 1, 1);
//		}
//
//		if(Math.random() < 0.15 * enchantLvl) {
//
//			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true, false);
//			AOutput.send(attackEvent.attacker, "Your not gonna die now");
//			ASound.play(attackEvent.attacker, Sound.IRONGOLEM_HIT, 1, 1);
//		}
//
//		if(Math.random() < 0.1 * enchantLvl) {
//
//			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SLOW, 40, 4, true, false);
//			AOutput.send(attackEvent.attacker, "You stun your opponent");
//			ASound.play(attackEvent.attacker, Sound.ANVIL_LAND, 1, 1);
//		}
//
//		if(Math.random() < 0.05 * enchantLvl) {
//
//			attackEvent.defender.setHealth(attackEvent.defender.getHealth() / 2D);
//			AOutput.send(attackEvent.attacker, "Your opponent is crippled");
//			ASound.play(attackEvent.attacker, Sound.ZOMBIE_WOODBREAK, 1, 1);
//			attackEvent.defender.playSound(attackEvent.defender.getLocation(), "mob.guardian.curse", 1000, 1);
//		}
//
//		if(Math.random() < 0.05) {
//
//			AOutput.send(attackEvent.attacker, "Zeus > Perun");
//			ASound.play(attackEvent.attacker, Sound.ENDERDRAGON_GROWL, 1, 1);
//			ASound.play(attackEvent.defender, Sound.IRONGOLEM_DEATH, 1, 1);
//			new BukkitRunnable() {
//				int count = 0;
//				@Override
//				public void run() {
//					if(++count == 5) cancel();
//					attackEvent.defender.getWorld().strikeLightningEffect(attackEvent.defender.getLocation());
//					attackEvent.defender.setHealth(Math.max(attackEvent.defender.getHealth() - 2, 0));
//				}
//			}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
//		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Can only be weld by a true king...", "&7Try and see if you are worthy...").getLore();
	}

	public static boolean isWorthy(Player player) {

		if(true) return true;
		if(player.getName().equals("Fishduper") || player.getName().equals("Cpl_Horatius")) return true;

		return false;
	}
}
