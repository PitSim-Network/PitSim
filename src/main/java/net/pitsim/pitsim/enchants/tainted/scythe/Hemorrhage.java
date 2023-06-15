package net.pitsim.pitsim.enchants.tainted.scythe;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.Cooldown;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.cosmetics.particles.BlockCrackParticle;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
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
				if(attackEvent.getDefender().isDead()) {
					cancel();
					return;
				}

				if(++count == getSeconds(enchantLvl)) cancel();

				attackEvent.getDefender().setHealth(Math.max(attackEvent.getDefender().getHealth() -
						getBleedDamage(enchantLvl), 1));

				Location centerLocation = attackEvent.getDefender().getLocation().add(0, 1, 0);
				BlockCrackParticle particle = new BlockCrackParticle(new MaterialData(Material.REDSTONE_BLOCK));
				for(int i = 0; i < 50; i++) particle.display(Misc.getNearbyRealPlayers(centerLocation, 25), centerLocation);

				Sounds.HEMORRHAGE.play(attackEvent.getDefender().getLocation());
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Striking an enemy makes them &cbleed &7for " + getSeconds(enchantLvl) +
						" second" + (getSeconds(enchantLvl) == 1 ? "" : "s") + " but costs &b" +
						getManaCost(enchantLvl) + "[]mana &7(" + getCooldownSeconds(enchantLvl) + "s cooldown)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"causes your enemies to bleed out, dealing very true damage";
	}

	public static double getBleedDamage(int enchantLvl) {
		return 1.5;
	}

	public static int getSeconds(int enchantLvl) {
		return enchantLvl + 1;
	}

	public static int getCooldownSeconds(int enchantLvl) {
		return 8;
	}

	public static int getManaCost(int enchantLvl) {
		return 30;
	}
}
