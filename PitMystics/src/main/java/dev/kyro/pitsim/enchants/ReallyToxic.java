package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReallyToxic extends PitEnchant {
	public static List<UUID> toxicNotifCooldown = new ArrayList<>();

	public ReallyToxic() {
		super("Really Toxic", false, ApplyType.PANTS,
				"reallytoxic", "really-toxic", "toxic", "rt");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onHeal(HealEvent event) {
		int charge = HitCounter.getCharge(event.player, this);
		if(charge == 0) return;

		int reduction = Math.min(charge, getMaxReduction());
		event.multipliers.add(Misc.getReductionMultiplier(reduction));
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		int attackerCharge = HitCounter.getCharge(attackEvent.attacker, this);
		if(attackerCharge != 0 && !toxicNotifCooldown.contains(attackEvent.attacker.getUniqueId())) {
			AOutput.send(attackEvent.attacker, "&a&lTOXIC!&f You heal &a" + Math.min(attackerCharge, getMaxReduction()) + "% &aless");
			toxicNotifCooldown.add(attackEvent.attacker.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					toxicNotifCooldown.remove(attackEvent.attacker.getUniqueId());
				}
			}.runTaskLater(PitSim.INSTANCE, 40L);
		}

		if(!canApply(attackEvent)) return;
		if(attackEvent.event.getDamager() instanceof Arrow) {

		}

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int charge = HitCounter.getCharge(attackEvent.defender, this);
		if(charge == 0) {
//			ASound.play(attackEvent.attacker, Sound.SPIDER_IDLE, 1, 1);
//			ASound.play(attackEvent.defender, Sound.SPIDER_IDLE, 1, 1);
		}
		HitCounter.setCharge(attackEvent.defender, this, charge + getReductionPerHit(enchantLvl));

		PitEnchant thisEnchant = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				int charge = HitCounter.getCharge(attackEvent.defender, thisEnchant);
				HitCounter.setCharge(attackEvent.defender, thisEnchant, charge - getReductionPerHit(enchantLvl));
			}
		}.runTaskLater(PitSim.INSTANCE, getStackTime() * 20);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Strikes apply 1 stack of", "&aToxicity&7. Stacks last &4" + getStackTime() + "s", "&7and reduce all healing by",
				"&a" + getReductionPerHit(enchantLvl) + "%&7, up to &a" + getMaxReduction() + "%").getLore();
	}

	public int getReductionPerHit(int enchantLvl) {

		return enchantLvl + 1;
	}

	public int getMaxReduction() {

		return 40;
	}

	public long getStackTime() {

		return 8;
	}
}
