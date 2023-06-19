package net.pitsim.spigot.enchants.tainted.scythe;

import net.pitsim.spigot.darkzone.DarkzoneBalancing;
import net.pitsim.spigot.controllers.objects.PitEnchantSpell;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.events.SpellUseEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class Necrotic extends PitEnchantSpell {
	public static Necrotic INSTANCE;

	public Necrotic() {
		super("Necrotic",
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
	public void onUse(SpellUseEvent event) {
		if(!isThisSpell(event.getSpell())) return;
		Player player = event.getPlayer();

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

	@Override
	public int getManaCost(int enchantLvl) {
		return Math.max(26 - enchantLvl * 4, 0);
	}

	@Override
	public int getCooldownTicks(int enchantLvl) {
		return 4;
	}
}
