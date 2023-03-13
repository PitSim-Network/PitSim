package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Adrenaline extends PitEnchant {
	public static Adrenaline INSTANCE;

	public Adrenaline() {
		super("Adrenaline", false, ApplyType.CHESTPLATES,
				"adrenaline");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;

		if(!isEnabled()) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(enchantLvl == 0) continue;

					if(player.getHealth() / player.getMaxHealth() > getThresholdPercent(enchantLvl) / 100.0) continue;

					Misc.applyPotionEffect(player, PotionEffectType.SPEED, 60,
							getAmplifier(enchantLvl), true, false);
					Sounds.ADRENALINE.play(player);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getAttacker().getHealth() / attackEvent.getAttacker().getMaxHealth() >
				getThresholdPercent(enchantLvl) / 100.0) return;

		attackEvent.increasePercent += getDamageIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7When you have less than &c" + getThresholdPercent(enchantLvl) + "% &7of your max xp, gain " +
						"&eSpeed " + AUtil.toRoman(getAmplifier(enchantLvl)) + " &7and deal &c+" +
						getDamageIncrease(enchantLvl) + "% &7more damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"gives you &eSpeed &7and increases the damage you deal when you are low";
	}

	public static int getThresholdPercent(int enchantLvl) {
		return 30;
	}

	public static int getDamageIncrease(int enchantLvl) {
		return enchantLvl * 10 + 15;
	}

	public static int getAmplifier(int enchantLvl) {
		return enchantLvl - 1;
	}
}
