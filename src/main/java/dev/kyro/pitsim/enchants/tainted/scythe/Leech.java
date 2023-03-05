package dev.kyro.pitsim.enchants.tainted.scythe;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.misc.kyrocosmetic.LeechParticle;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Leech extends PitEnchant {
	public static Leech INSTANCE;

	public Leech() {
		super("Leech", true, ApplyType.SCYTHES,
				"leech");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		Player player = event.getPlayer();
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, 10);
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

		LeechParticle leechParticle = new LeechParticle(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				leechParticle.remove();
			}
		}.runTaskLater(PitSim.INSTANCE, getLifetimeSeconds(enchantLvl) * 20L);
		Sounds.LEECH.play(player);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana&7, spawning a " +
						"&cleech particle &7for " + getLifetimeSeconds(enchantLvl) + " second" +
						(getLifetimeSeconds(enchantLvl) == 1 ? "" : "s")
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 1;
	}

	public static int getLifetimeSeconds(int enchantLvl) {
		return enchantLvl * 4 + 12;
	}
}
