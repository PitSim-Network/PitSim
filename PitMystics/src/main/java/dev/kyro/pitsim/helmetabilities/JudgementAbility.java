package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HelmetListeners;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
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

		super(player,"Judgement", "judgement", true, 15);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!isActive(attackEvent.attacker)) return;

		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(attackEvent.attacker);
		assert goldenHelmet != null;
		if(!goldenHelmet.withdrawGold(5000)) {
			AOutput.error(attackEvent.attacker,"&cNot enough gold!");
			goldenHelmet.deactivate();
			Sounds.NO.play(attackEvent.attacker);
			return;
		}

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);

		if(Math.random() < 0.25) {

			pitAttacker.heal(2);
			Sounds.JUDGEMENT_HEAL.play(attackEvent.attacker);
		}

		if(Math.random() < 0.20) {

			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.WITHER, 100, 2, true, false);
			Sounds.JUDGEMENT_WITHER.play(attackEvent.attacker);
		}

		if(Math.random() < 0.15) {

			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true, false);
			Sounds.JUDGEMENT_RESISTANCE.play(attackEvent.attacker);
		}

		if(Math.random() < 0.10) {

			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.INCREASE_DAMAGE, 40, 0, true, false);
			Sounds.JUDGEMENT_STRENGTH.play(attackEvent.attacker);
		}

		if(Math.random() < 0.05) {

			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SLOW, 40, 4, true, false);
			Sounds.JUDGEMENT_SLOW.play(attackEvent.attacker);
		}

		if(Math.random() < 0.03) {

			attackEvent.defender.setHealth(attackEvent.defender.getHealth() / 2D);
			Sounds.JUDGEMENT_HALF_ATTACKER.play(attackEvent.attacker);
			Sounds.JUDGEMENT_HALF_DEFENDER.play(attackEvent.defender);
		}

		if(Math.random() < 0.02) {

			Sounds.JUDGEMENT_ZEUS_ATTACKER.play(attackEvent.attacker);
			Sounds.JUDGEMENT_ZEUS_DEFENDER.play(attackEvent.defender);
			new BukkitRunnable() {
				int count = 0;
				@Override
				public void run() {
					if(++count == 5) cancel();
					Misc.strikeLightningForPlayers(attackEvent.defender.getLocation(), 10);
					attackEvent.defender.setHealth(Math.max(attackEvent.defender.getHealth() - 2, 1));
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
		}

		if(Math.random() < 0.01 && !HopperManager.isHopper(attackEvent.defender)) {

			Hopper hopper = HopperManager.callHopper("PayForTruce", Hopper.Type.MYSTIC, attackEvent.defender);
			hopper.team.add(attackEvent.attacker.getUniqueId());
			Sounds.JUDGEMENT_HOPPER.play(attackEvent.attacker);
			Sounds.JUDGEMENT_HOPPER.play(attackEvent.defender);
		}
	}

	@Override
	public void onActivate() {
		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);
		assert goldenHelmet != null;

		Sounds.HELMET_ACTIVATE.play(player);
		AOutput.send(player, "&6&lGOLDEN HELMET! &aActivated &9Judgement&7. (&6-5,000g&7 per hit)");
	}

	@Override
	public boolean shouldActivate() {
		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);

		assert goldenHelmet != null;
		if(PitSim.VAULT.getBalance(player) < 5000) {
			AOutput.error(player,"&cNot enough gold!");
			Sounds.NO.play(player);
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
				"&7Cost: &6" + formatter.format(5000) + "g &7per hit");
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
