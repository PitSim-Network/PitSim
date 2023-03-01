package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hemorrhage extends PitEnchant {
	public static Hemorrhage INSTANCE;
	public static Map<LivingEntity, BukkitTask> bleedingPlayerMap = new HashMap<>();

	public Hemorrhage() {
		super("Hemorrhage", true, ApplyType.CHESTPLATES,
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
		if(!attackEvent.getAttackerPitPlayer().useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(attackEvent.getAttackerPlayer());
			return;
		}
		cooldown.restart();

		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(++count == getSeconds(enchantLvl)) cancel();

				Location location = attackEvent.getDefenderPlayer().getLocation();
				BlockCrackParticle particle = new BlockCrackParticle(new MaterialData(Material.REDSTONE_BLOCK));
				particle.display(Misc.getNearbyRealPlayers(location, 25), location);

				Sounds.HEMORRHAGE.play(attackEvent.getDefenderPlayer());
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 15L);

		Sounds.HEMORRHAGE.play(attackEvent.getAttackerPlayer());
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Striking an enemy makes them bleed for " + getSeconds(enchantLvl) +
						" second" + (getSeconds(enchantLvl) == 1 ? "" : "s") + " but costs &b" +
						getManaCost(enchantLvl) + " mana (" + getCooldownSeconds(enchantLvl) + " second" +
						(getCooldownSeconds(enchantLvl) == 1 ? "" : "s") + ")"
		).getLore();
	}

	public static int getSeconds(int enchantLvl) {
		return 1;
	}

	public static int getCooldownSeconds(int enchantLvl) {
		return 4;
	}

	public static int getManaCost(int enchantLvl) {
		return 1;
	}
}
