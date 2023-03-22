package dev.kyro.pitsim.enchants.tainted.scythe;

import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
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
import org.bukkit.event.EventPriority;

import java.util.List;

public class Necrotic extends PitEnchant {
	public static Necrotic INSTANCE;

	public Necrotic() {
		super("Necrotic", true, ApplyType.SCYTHES,
				"necrotic", "necro");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDamage(AttackEvent.Pre attackEvent) {
		if(!(attackEvent.getFireball() instanceof WitherSkull)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getAttacker() == attackEvent.getDefender()) {
			attackEvent.setCancelled(true);
			attackEvent.getAttackerEnchantMap().clear();
			attackEvent.getDefenderEnchantMap().clear();
			return;
		}

		attackEvent.getWrapperEvent().getSpigotEvent().setDamage(DarkzoneBalancing.SCYTHE_DAMAGE * 3);
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		Player player = event.getPlayer();

		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, 4);
		if(cooldown.isOnCooldown()) {
			Sounds.NO.play(player);
			return;
		}
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useManaForSpell(getManaCost(enchantLvl))) {
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

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"shoots a &8wither skull &7in the direction that you are looking";
	}

	public static int getManaCost(int enchantLvl) {
		return Math.max(22 - enchantLvl * 4, 0);
	}
}
