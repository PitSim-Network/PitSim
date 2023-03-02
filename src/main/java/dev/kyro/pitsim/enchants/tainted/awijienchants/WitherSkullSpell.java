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
	public static WitherSkullSpell INSTANCE;

	public WitherSkullSpell() {
		super("Necrotic", true, ApplyType.SCYTHES,
				"necrotic", "necro");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onDamage(AttackEvent.Pre attackEvent) {
		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getFireball() == null || attackEvent.getAttacker() != attackEvent.getDefender()) return;
		attackEvent.getAttackerEnchantMap().clear();
		attackEvent.getDefenderEnchantMap().clear();
		attackEvent.setCancelled(true);
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(event.getPlayer(), 20);
		if(cooldown.isOnCooldown()) return;
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(player);
			return;
		}
		cooldown.restart();

		player.launchProjectile(WitherSkull.class);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana&7, " +
						"shooting a &8wither skull&7 that was so kindly donated by a now-headless friend"
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 1;
	}
}
