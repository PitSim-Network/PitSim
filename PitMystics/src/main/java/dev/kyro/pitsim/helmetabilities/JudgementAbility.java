package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HelmetListeners;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class JudgementAbility extends HelmetAbility {
	public JudgementAbility(Player player) {

		super(player,"Judgement", "judgement", true, 13);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!isActive(attackEvent.attacker)) return;

		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(attackEvent.attacker);
		assert goldenHelmet != null;
		if(!goldenHelmet.withdrawGold(10000)) {
			AOutput.error(attackEvent.attacker,"&cNot enough gold!");
			goldenHelmet.deactivate();
			ASound.play(attackEvent.attacker, Sound.VILLAGER_NO, 1F, 1F);
			return;
		}

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);

		if(Math.random() < 0.25) {

			pitAttacker.heal(2);
			ASound.play(attackEvent.attacker, Sound.BURP, 1, 1);
		}

		if(Math.random() < 0.20) {

			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.WITHER, 100, 2, true, false);
			ASound.play(attackEvent.attacker, Sound.WITHER_SHOOT, 1, 1);
		}

		if(Math.random() < 0.15) {

			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true, false);
			ASound.play(attackEvent.attacker, Sound.IRONGOLEM_HIT, 1, 1);
		}

		if(Math.random() < 0.10) {

			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.INCREASE_DAMAGE, 40, 0, true, false);
			ASound.play(attackEvent.attacker, Sound.ENDERMAN_SCREAM, 1, 1);
		}

		if(Math.random() < 0.05) {

			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SLOW, 40, 4, true, false);
			ASound.play(attackEvent.attacker, Sound.ANVIL_LAND, 1, 1);
		}

		if(Math.random() < 0.03) {

			attackEvent.defender.setHealth(attackEvent.defender.getHealth() / 2D);
			ASound.play(attackEvent.attacker, Sound.ZOMBIE_WOODBREAK, 1, 1);
			attackEvent.defender.playSound(attackEvent.defender.getLocation(), "mob.guardian.curse", 1000, 1);
		}

		if(Math.random() < 0.02) {

			ASound.play(attackEvent.attacker, Sound.ENDERDRAGON_GROWL, 1, 1);
			ASound.play(attackEvent.defender, Sound.IRONGOLEM_DEATH, 1, 1);
			new BukkitRunnable() {
				int count = 0;
				@Override
				public void run() {
					if(++count == 5) cancel();
					attackEvent.defender.getWorld().strikeLightningEffect(attackEvent.defender.getLocation());
					attackEvent.defender.setHealth(Math.max(attackEvent.defender.getHealth() - 2, 1));
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
		}
	}

	@Override
	public void onActivate() {
		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);
		assert goldenHelmet != null;

		ASound.play(player, Sound.NOTE_PLING, 1.3F, 2);
		AOutput.send(player, "&6&lGOLDEN HELMET! &aActivated &9Judgement&7. (&6-10,000g&7 per hit)");
	}

	@Override
	public boolean shouldActivate() {
		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);

		assert goldenHelmet != null;
		if(!goldenHelmet.withdrawGold(10000)) {
			AOutput.error(player,"&cNot enough gold!");
			ASound.play(player, Sound.VILLAGER_NO, 1F, 1F);
			return false;
		}
		return true;
	}

	@Override
	public void onDeactivate() {
		AOutput.send(player, "&6&lGOLDEN HELMET! &cDeactivated &9Judgement&c.");
	}

	@Override
	public void onProc() { }

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to toggle", "&7Judgement. Annihilate your", "&7opponents with RNGesus", "",
				"&7Cost: &6" + formatter.format(10000) + "g &7per hit");
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.BEACON);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}
}
