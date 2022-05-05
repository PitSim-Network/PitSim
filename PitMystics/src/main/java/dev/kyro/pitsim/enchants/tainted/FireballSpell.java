package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class FireballSpell extends PitEnchant {

	public FireballSpell() {
		super("Fireball", true, ApplyType.SCYTHES,
				"fireball");
		tainted = true;
	}

	@EventHandler
	public void onDamage(AttackEvent.Pre attackEvent) {
		if(attackEvent.fireball == null || attackEvent.attacker != attackEvent.defender) return;
		attackEvent.getAttackerEnchantMap().clear();
		attackEvent.getDefenderEnchantMap().clear();
		attackEvent.event.setCancelled(true);
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(event.getPlayer(), 10);
		if(cooldown.isOnCooldown()) return;

		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(player);
			return;
		}

		cooldown.restart();

		Fireball fireball = (Fireball) player.getWorld().spawnEntity(player.getLocation().add(0, 2, 0), EntityType.FIREBALL);
		fireball.setIsIncendiary(false);
		fireball.setShooter(player);
	}

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Fireball) {
			if(((Fireball) event.getDamager()).getShooter() == event.getEntity()) event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(!(event.getEntity() instanceof Fireball)) return;
		event.blockList().clear();
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Enjoy").getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 120 - (30 * enchantLvl);
	}
}
