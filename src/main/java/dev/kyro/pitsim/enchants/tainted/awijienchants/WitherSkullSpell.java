package dev.kyro.pitsim.enchants.tainted.awijienchants;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;

import java.util.List;

public class WitherSkullSpell extends PitEnchant {
	public static int MANA_COST = 30;

	public WitherSkullSpell() {
		super("Wither Skull", true, ApplyType.SCYTHES,
				"witherskull", "wither");
		isTainted = true;
	}

	@EventHandler
	public void onDamage(AttackEvent.Pre attackEvent) {
		if(attackEvent.getFireball() == null || attackEvent.getAttacker() != attackEvent.getDefender()) return;
		attackEvent.getAttackerEnchantMap().clear();
		attackEvent.getDefenderEnchantMap().clear();
		attackEvent.getEvent().setCancelled(true);
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(event.getPlayer(), 20);
		if(cooldown.isOnCooldown()) return;

		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useMana(MANA_COST)) {
			Sounds.NO.play(player);
			return;
		}

		cooldown.restart();

		WitherSkull witherSkull = player.getWorld().spawn(player.getLocation().add(0, 2, 0), WitherSkull.class);
		witherSkull.setShooter(player);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Enjoy"
		).getLore();
	}
}
