package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.EnchantManager;
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

import java.util.List;

public class MegaLongBow extends PitEnchant {

	public MegaLongBow() {
		super("Mega Longbow", true, ApplyType.BOWS,
				"megalongbow", "mega-longbow", "mlb", "mega");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
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
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		critArrow(player, arrow);
		Misc.applyPotionEffect(player, PotionEffectType.JUMP, 40, getJumpMultiplier(enchantLvl), true, false);
	}

	public static void critArrow(Player player, Arrow arrow) {

		arrow.setCritical(true);
		arrow.setVelocity(player.getLocation().getDirection().multiply(2.95));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7One shot per second, this bow is",
				"&7automatically fully drawn and", "&7grants &aJump Boost " + AUtil.toRoman(getJumpMultiplier(enchantLvl) + 1) + " &7(2s)").getLore();
	}

	public int getJumpMultiplier(int enchantLvl) {

		return enchantLvl;
	}
}
