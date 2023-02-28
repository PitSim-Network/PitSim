package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
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
	public void cancel(AttackEvent.Pre attackEvent) {
		if(!attackEvent.isDefenderPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0 || attackEvent.getArrow() == null || !(attackEvent.getDefenderPlayer().isBlocking())) return;

		Cooldown cooldown = getCooldown(attackEvent.getDefenderPlayer(), getCooldownSeconds(enchantLvl) * 20);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		attackEvent.setCancelled(true);
		attackEvent.getArrow().remove();

		PitPlayer pitDefender = attackEvent.getDefenderPitPlayer();
		pitDefender.heal(getHealing(enchantLvl));

		Sounds.BULLET_TIME.play(attackEvent.getDefender());
		attackEvent.getArrow().getWorld().playEffect(attackEvent.getArrow().getLocation(), Effect.EXPLOSION, 0, 30);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7Blocking destroys arrows that hit", "&7you. Destroying arrows this way",
				"&7heals &c" + Misc.getHearts(getHealing(enchantLvl)) + " &7(" +
				getCooldownSeconds(enchantLvl) + "s cooldown)").getLore();
	}

	public static int getCooldownSeconds(int enchantLvl) {
		return Math.max(11 - enchantLvl * 2, 0);
	}

	public double getHealing(int enchantLvl) {
		return 3;
	}
}
