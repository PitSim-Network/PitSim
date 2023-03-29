package dev.kyro.pitsim.enchants.tainted.scythe;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchantSpell;
import dev.kyro.pitsim.cosmetics.misc.kyrocosmetic.LeechParticle;
import dev.kyro.pitsim.events.SpellUseEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Leech extends PitEnchantSpell {
	public static Leech INSTANCE;

	public Leech() {
		super("Leech", "leech");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onUse(SpellUseEvent event) {
		if(!isThisSpell(event.getSpell())) return;
		Player player = event.getPlayer();

		LeechParticle leechParticle = new LeechParticle(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				leechParticle.remove();
			}
		}.runTaskLater(PitSim.INSTANCE, getLifetimeSeconds(event.getSpellLevel()) * 20L);
		Sounds.LEECH.play(player);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana&7, spawning a " +
						"&cleech particle &7that steals enemies' health for " + getLifetimeSeconds(enchantLvl) + " second" +
						(getLifetimeSeconds(enchantLvl) == 1 ? "" : "s")
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"spawns a particle that steals health from other opponents and gives it to you";
	}

	@Override
	public int getManaCost(int enchantLvl) {
		return 35;
	}

	@Override
	public int getCooldownTicks(int enchantLvl) {
		return 4;
	}

	public static int getLifetimeSeconds(int enchantLvl) {
		return enchantLvl * 4 + 4;
	}
}
