package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class TaintedSoul extends PitEnchant {

	public TaintedSoul() {
		super("Tainted Soul", true, ApplyType.TAINTED,
				"taintedsoul", "soul");
		tainted = true;
		meleOnly = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!MapManager.inDarkzone(attackEvent.attacker)) return;
		if(!canApply(attackEvent)) return;
		if(!fakeHits && attackEvent.fakeHit) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.defenderIsPlayer && PitBoss.isPitBoss(attackEvent.defenderPlayer)) return;

		Cooldown cooldown = getCooldown(attackEvent.attackerPlayer, getCooldown() * 20);
		if(cooldown.isOnCooldown()) return; else cooldown.restart();

		if(attackEvent.defender.getHealth() > 1) {
			attackEvent.defender.setHealth(attackEvent.defender.getHealth() * 0.80);
		} else {
			attackEvent.veryTrueDamage = 1000;
		}
		attackEvent.defender.getWorld().strikeLightningEffect(attackEvent.defender.getLocation());
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Subtract &f1/5 &7of your enemy's", "&7current health (" + getCooldown() + "s cooldown)",
				"&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();
	}

	public static int getCooldown() {
		return 3;
	}

	public static int reduction(int enchantLvl) {
		return 80 - (20 * enchantLvl);
	}
}
