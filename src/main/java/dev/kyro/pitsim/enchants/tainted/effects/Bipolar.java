package dev.kyro.pitsim.enchants.tainted.effects;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.ManaRegenEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Bipolar extends PitEnchant {
	public static Bipolar INSTANCE;
	public static List<Player> vengefulPlayers = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(enchantLvl == 0) continue;
					if(vengefulPlayers.contains(player)) {
						Misc.applyPotionEffect(player, PotionEffectType.SPEED, 60,
								getSpeedAmplifier(enchantLvl), true, false);
					} else {
						Misc.applyPotionEffect(player, PotionEffectType.REGENERATION, 60,
								getRegenerationAmplifier(enchantLvl), true, false);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public Bipolar() {
		super("Bipolar", true, ApplyType.CHESTPLATES,
				"bipolar", "polar");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(vengefulPlayers.contains(attackEvent.getAttackerPlayer())) {
			attackEvent.increasePercent += getDamageIncrease(enchantLvl);
		} else {
			attackEvent.multipliers.add(Misc.getReductionMultiplier(getDamageDecrease(enchantLvl)));
		}
	}

	@EventHandler
	public void onManaRegen(ManaRegenEvent event) {
		Player player = event.getPlayer();
		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;
		event.multipliers.add(Misc.getReductionMultiplier(getManaReduction(enchantLvl)));
	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if(player.isSneaking()) return;

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, 20 * 5);
		if(cooldown.isOnCooldown()) {
			Sounds.NO.play(player);
			return;
		}
		cooldown.restart();

		if(vengefulPlayers.contains(player)) {
			Misc.sendTitle(player, "&a&lPEACE", 20);
			Misc.sendSubTitle(player, "&7You take a deep breath and sigh", 20);
			Sounds.BIPOLAR_PEACE.play(player);
			vengefulPlayers.remove(player);
		} else {
			Misc.sendTitle(player, "&c&lVENGEANCE", 20);
			Misc.sendSubTitle(player, "&7A sudden rage fills your body", 20);
			Sounds.BIPOLAR_VENGEANCE.play(player);
			vengefulPlayers.add(player);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		vengefulPlayers.remove(player);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new ALoreBuilder(
				"&7Sneaking toggles between &cVengeful",
				"&7and &aPeaceful &7modes (5s cooldown):",
				"&c\u25a0 Vengeful&7: Deal &c+" + getDamageDecrease(enchantLvl) + "% &7damage,",
				"&7gain &eSpeed " + AUtil.toRoman(getSpeedAmplifier(enchantLvl)),
				"&a\u25a0 Peaceful&7: Deal &9-" + getDamageDecrease(enchantLvl) + "% &7damage,",
				"&7gain &cRegeneration " + AUtil.toRoman(getRegenerationAmplifier(enchantLvl)),
				"&7While worn, regain mana &b" + getManaReduction(enchantLvl) + "% &7slower"
		).getLore();
	}

	public static int getManaReduction(int enchantLvl) {
		return 50;
	}

	public static int getDamageIncrease(int enchantLvl) {
		return enchantLvl * 10;
	}

	public static int getSpeedAmplifier(int enchantLvl) {
		return Misc.linearEnchant(enchantLvl, 0.5, -0.5);
	}

	public static int getDamageDecrease(int enchantLvl) {
		return enchantLvl * 5;
	}

	public static int getRegenerationAmplifier(int enchantLvl) {
		return Misc.linearEnchant(enchantLvl, 0.5, 0);
	}
}
