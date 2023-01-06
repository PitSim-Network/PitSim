package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.GrimManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MegaLongBow extends PitEnchant {
	public static MegaLongBow INSTANCE;
	public static List<UUID> mlbShots = new ArrayList<>();

	public MegaLongBow() {
		super("Mega Longbow", true, ApplyType.BOWS,
				"megalongbow", "mega-longbow", "mlb", "mega");
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		if(attackEvent.getArrow() == null || !mlbShots.contains(attackEvent.getArrow().getUniqueId())) return;

		attackEvent.increaseCalcDecrease.add(Misc.getReductionMultiplier(getReduction()));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBowShoot(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player player = ((Player) event.getEntity()).getPlayer();
		Arrow arrow = (Arrow) event.getProjectile();

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;

//		if(event instanceof VolleyShootEvent) {
//
//			critArrow(player, arrow);
//			return;
//		}

		Cooldown cooldown = getCooldown(player, 20);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		mlbShots.add(arrow.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				mlbShots.remove(arrow.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 200L);

		critArrow(player, arrow);
		Misc.applyPotionEffect(player, PotionEffectType.JUMP, 40, getJumpMultiplier(enchantLvl), true, false);
		GrimManager.exemptPlayer(player, 20 + getJumpMultiplier(enchantLvl) * 5L,
				GrimManager.FlagType.SIMULATION, GrimManager.FlagType.GROUND_SPOOF);
	}

	public static void critArrow(Player player, Arrow arrow) {

		arrow.setCritical(true);
		arrow.setVelocity(player.getLocation().getDirection().multiply(2.95));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7One shot per second, this bow is",
				"&7automatically fully drawn and", "&7grants &aJump Boost " + AUtil.toRoman(getJumpMultiplier(enchantLvl) + 1) + " &7(2s).",
				"&7Arrows deal &c-" + getReduction() + "% &7damage").getLore();
	}

	public static int getReduction() {
		return 50;
	}

	public int getJumpMultiplier(int enchantLvl) {

		return enchantLvl;
	}
}
