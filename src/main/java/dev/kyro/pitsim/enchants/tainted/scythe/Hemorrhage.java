package dev.kyro.pitsim.enchants.tainted.scythe;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.cosmetics.particles.BlockCrackParticle;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Hemorrhage extends PitEnchant {
	public static Hemorrhage INSTANCE;

	public Hemorrhage() {
		super("Hemorrhage", true, ApplyType.SCYTHES,
				"hemorrhage", "hemo");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), getCooldownSeconds(enchantLvl) * 20);
		if(cooldown.isOnCooldown()) {
			Sounds.NO.play(attackEvent.getAttackerPlayer());
			return;
		}
		if(!attackEvent.getAttackerPitPlayer().useManaForSpell(getManaCost(enchantLvl))) {
			Sounds.NO.play(attackEvent.getAttackerPlayer());
			return;
		}
		cooldown.restart();

		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(++count == getSeconds(enchantLvl)) cancel();

				attackEvent.getDefender().setHealth(Math.max(attackEvent.getDefender().getHealth() -
						getBleedDamage(enchantLvl), 1));

				Location centerLocation = attackEvent.getDefenderPlayer().getLocation().add(0, 1, 0);
				BlockCrackParticle particle = new BlockCrackParticle(new MaterialData(Material.REDSTONE_BLOCK));
				for(int i = 0; i < 50; i++) particle.display(Misc.getNearbyRealPlayers(centerLocation, 25), centerLocation);

				Sounds.HEMORRHAGE.play(attackEvent.getDefenderPlayer().getLocation());
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Striking an enemy makes them &cbleed &7for " + getSeconds(enchantLvl) +
						" second" + (getSeconds(enchantLvl) == 1 ? "" : "s") + " but costs &b" +
						getManaCost(enchantLvl) + " mana &7(" + getCooldownSeconds(enchantLvl) + " second cooldown"
		).getLore();
	}

	public static double getBleedDamage(int enchantLvl) {
		return 0.5;
	}

	public static int getSeconds(int enchantLvl) {
		return 5;
	}

	public static int getCooldownSeconds(int enchantLvl) {
		return 10;
	}

	public static int getManaCost(int enchantLvl) {
		return 1;
	}
}
