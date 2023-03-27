package dev.kyro.pitsim.enchants.tainted.chestplate;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inferno extends PitEnchant {
	public static Inferno INSTANCE;
	public static Map<LivingEntity, BukkitTask> fireDamageMap = new HashMap<>();

	public Inferno() {
		super("Inferno", true, ApplyType.CHESTPLATES,
				"inferno");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!fireDamageMap.containsKey(killEvent.getDead())) return;
		fireDamageMap.remove(killEvent.getDead()).cancel();
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getDefender().getFireTicks() > 0 || fireDamageMap.containsKey(attackEvent.getDefender())) return;

		if(!attackEvent.getAttackerPitPlayer().useManaForSpell(getManaCost(enchantLvl))) {
			Sounds.NO.play(attackEvent.getAttackerPlayer());
			return;
		}

		attackEvent.getDefender().setFireTicks(20 * getFireSeconds(enchantLvl));
		BukkitTask runnable = new BukkitRunnable() {
			int count = 1;
			final int total = getFireSeconds(enchantLvl);
			@Override
			public void run() {
				if(count == total) {
					cancel();
					fireDamageMap.remove(attackEvent.getDefender());
					return;
				}
				DamageManager.createIndirectAttack(null, attackEvent.getDefender(), 5);
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 20L, 20);
		fireDamageMap.put(attackEvent.getDefender(), runnable);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Strikes set your enemies &6ablaze &7for " + getFireSeconds(enchantLvl) + " seconds but costs &b" +
						getManaCost(enchantLvl) + " mana"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"sets your opponents &6ablaze";
	}

	public static int getManaCost(int enchantLvl) {
		return Math.max(16 - enchantLvl * 2, 0);
	}

	public static int getFireSeconds(int enchantLvl) {
		return enchantLvl + 2;
	}
}
