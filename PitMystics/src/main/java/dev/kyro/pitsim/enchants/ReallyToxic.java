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

		if(attackEvent.isAttackerIsPlayer()) {
			int attackerCharge = HitCounter.getCharge(attackEvent.getAttackerPlayer(), this);
			if(attackerCharge != 0 && !toxicNotifCooldown.contains(attackEvent.getAttacker().getUniqueId())) {
				AOutput.send(attackEvent.getAttacker(), "&a&lTOXIC!&f You heal &a" + Math.min(attackerCharge, getMaxReduction()) + "% &aless");
				toxicNotifCooldown.add(attackEvent.getAttacker().getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						toxicNotifCooldown.remove(attackEvent.getAttacker().getUniqueId());
					}
				}.runTaskLater(PitSim.INSTANCE, 40L);
			}
		}

		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.isDefenderIsPlayer()) {
			int charge = HitCounter.getCharge(attackEvent.getDefenderPlayer(), this);
			HitCounter.setCharge(attackEvent.getDefenderPlayer(), this, charge + getReductionPerHit(enchantLvl));

			PitEnchant thisEnchant = this;
			new BukkitRunnable() {
				@Override
				public void run() {
					int charge = HitCounter.getCharge(attackEvent.getDefenderPlayer(), thisEnchant);
					HitCounter.setCharge(attackEvent.getDefenderPlayer(), thisEnchant, charge - getReductionPerHit(enchantLvl));
				}
			}.runTaskLater(PitSim.INSTANCE, getStackTime(enchantLvl) * 20);
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7Strikes apply 1 stack of", "&aToxicity&7. Stacks last &4" + getStackTime(enchantLvl) + "s", "&7and reduce all healing by",
				"&a" + getReductionPerHit(enchantLvl) + "%&7, up to &a" + getMaxReduction() + "%").getLore();
	}

	public int getReductionPerHit(int enchantLvl) {
		return enchantLvl + 1;
	}

	public int getMaxReduction() {
		return 40;
	}

	public long getStackTime(int enchantLvl) {
		return 5;
	}
}
