package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitremake.controllers.Cooldown;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class MegaLongBow extends PitEnchant {

	public MegaLongBow() {
		super("Mega Longbow", true, ApplyType.BOWS,
				"megalongbow", "mega-longbow", "mlb");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {
		return damageEvent;
	}

	@EventHandler
	public void onDamage(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player player = ((Player) event.getEntity()).getPlayer();
		Arrow arrow = (Arrow) event.getProjectile();

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, 20);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		arrow.setCritical(true);
		arrow.setVelocity(player.getLocation().getDirection().multiply(2.9));
		for(PotionEffect potionEffect : player.getActivePotionEffects()) {
			if(!potionEffect.getType().equals(PotionEffectType.JUMP) || potionEffect.getAmplifier() > getJumpMultiplier(enchantLvl)) continue;
			if(potionEffect.getAmplifier() == getJumpMultiplier(enchantLvl) && potionEffect.getDuration() >= 40) continue;
			player.removePotionEffect(PotionEffectType.JUMP);
			break;
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40, getJumpMultiplier(enchantLvl), true));
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
