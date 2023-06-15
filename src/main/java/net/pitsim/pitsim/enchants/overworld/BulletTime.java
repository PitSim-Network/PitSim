package net.pitsim.pitsim.enchants.overworld;

import net.pitsim.pitsim.aserverstatistics.StatisticsManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.event.EventHandler;

import java.util.List;

public class BulletTime extends PitEnchant {
	public static BulletTime INSTANCE;

	public BulletTime() {
		super("Bullet Time", false, ApplyType.SWORDS,
				"bullettime", "bullet-time", "bullet", "bt");
		isUncommonEnchant = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isDefenderPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0 || !attackEvent.getDefenderPlayer().isBlocking()) return;
		attackEvent.multipliers.add(2.0);
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!attackEvent.isDefenderPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0 || attackEvent.getArrow() == null || !attackEvent.getDefenderPlayer().isBlocking()) return;

		attackEvent.setCancelled(true);
		attackEvent.getArrow().remove();

		PitPlayer pitDefender = attackEvent.getDefenderPitPlayer();
		pitDefender.heal(getHealing(enchantLvl));

		Sounds.BULLET_TIME.play(attackEvent.getDefender());
		attackEvent.getArrow().getWorld().playEffect(attackEvent.getArrow().getLocation(), Effect.EXPLOSION, 0, 30);

		StatisticsManager.logAttack(attackEvent);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Blocking destroys arrows that hit you. Destroying arrows this way heals &c" +
				Misc.getHearts(getHealing(enchantLvl)) + "&7. Blocking does not reduce damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that " +
				"destroys arrows when blocking your sword (and heals you too)";
	}

	public double getHealing(int enchantLvl) {
		if(enchantLvl == 1) return 0.2;
		return enchantLvl * 0.5 - 0.5;
	}
}
