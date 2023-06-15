package net.pitsim.spigot.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.DamageManager;
import net.pitsim.spigot.controllers.HopperManager;
import net.pitsim.spigot.controllers.objects.*;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.events.MessageEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;

public class JudgementAbility extends HelmetAbility {
	public static JudgementAbility INSTANCE;
	public static Map<UUID, Integer> cooldownMap = new HashMap<>();
	public static Map<Player, Integer> maxActivationMap = new HashMap<>();
	public static final int GOLD_COST = 2_000;
	public BukkitTask runnable;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<UUID, Integer> entry : new ArrayList<>(cooldownMap.entrySet())) {
					int time = entry.getValue();
					if(time > 0) cooldownMap.put(entry.getKey(), --time);
					else cooldownMap.remove(entry.getKey());
				}

				for(Map.Entry<Player, Integer> entry : new ArrayList<>(maxActivationMap.entrySet())) {
					int time = entry.getValue();
					if(time > 0) maxActivationMap.put(entry.getKey(), --time);
					else {
						AOutput.send(entry.getKey(), "&6&lGOLDEN HELMET! &cAuto Deactivated &9Judgement &7(can only be on for " +
								getMaxActivationSeconds() + " seconds");
						HelmetManager.deactivate(entry.getKey());
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public JudgementAbility(Player player) {
		super(player, "Judgement", "judgement", true, 15);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!isActive || player != attackEvent.getAttacker()) return;
		ItemStack goldenHelmet = HelmetManager.getHelmet(attackEvent.getAttacker());
		assert goldenHelmet != null;
		if(!HelmetManager.withdrawGold(player, goldenHelmet, GOLD_COST)) {
			AOutput.error(player, "&cNot enough gold!");
			HelmetManager.deactivate(player);
			Sounds.NO.play(player);
			return;
		}

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.getAttackerPlayer());

		if(Math.random() < 0.2) {

			pitAttacker.heal(2);
			Sounds.JUDGEMENT_HEAL.play(attackEvent.getAttacker());
		}

		if(Math.random() < 0.12) {

			Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.WITHER, 60, 2, true, false);
			Sounds.JUDGEMENT_WITHER.play(attackEvent.getAttacker());
		}

		if(Math.random() < 0.10) {

			Misc.applyPotionEffect(attackEvent.getAttacker(), PotionEffectType.DAMAGE_RESISTANCE, 60, 0, true, false);
			Sounds.JUDGEMENT_RESISTANCE.play(attackEvent.getAttacker());
		}

		if(Math.random() < 0.07) {

			Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.SLOW, 40, 4, true, false);
			Sounds.JUDGEMENT_SLOW.play(attackEvent.getAttacker());
		}

		if(Math.random() < 0.04) {

			Misc.applyPotionEffect(attackEvent.getAttacker(), PotionEffectType.INCREASE_DAMAGE, 40, 0, true, false);
			Sounds.JUDGEMENT_STRENGTH.play(attackEvent.getAttacker());
		}

		if(Math.random() < 0.02) {

			attackEvent.getDefender().setHealth(attackEvent.getDefender().getHealth() * 3.0 / 4.0);
			Sounds.JUDGEMENT_HALF_ATTACKER.play(attackEvent.getAttacker());
			Sounds.JUDGEMENT_HALF_DEFENDER.play(attackEvent.getDefender());
		}

		if(Math.random() < 0.01) {

			Sounds.JUDGEMENT_ZEUS_ATTACKER.play(attackEvent.getAttacker());
			Sounds.JUDGEMENT_ZEUS_DEFENDER.play(attackEvent.getDefender());
			new BukkitRunnable() {
				int count = 0;

				@Override
				public void run() {
					if(++count == 4) cancel();
					Misc.strikeLightningForPlayers(attackEvent.getDefender().getLocation(), 10);
					player.setNoDamageTicks(0);
					DamageManager.createDirectAttack(attackEvent.getAttacker(), attackEvent.getDefender(), 0,
							null, null, newEvent -> newEvent.veryTrueDamage = 2);
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
		}

		if(Math.random() < 0.002 && !HopperManager.isHopper(attackEvent.getDefender()) && attackEvent.isDefenderPlayer()) {

			Hopper hopper = HopperManager.callHopper("PayForTruce", Hopper.Type.getRandomGSet(), attackEvent.getDefenderPlayer());
			hopper.judgementPlayer = attackEvent.getAttackerPlayer();
			hopper.team.add(attackEvent.getAttacker().getUniqueId());
			Sounds.JUDGEMENT_HOPPER.play(attackEvent.getAttacker());
			Sounds.JUDGEMENT_HOPPER.play(attackEvent.getDefender());
		}
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		if(strings.isEmpty() || !strings.get(0).equals("JUDGEMENT")) return;
		String uuidString = strings.get(1);
		putOnCooldown(UUID.fromString(uuidString));
	}

	@Override
	public void onActivate() {
		ItemStack goldenHelmet = HelmetManager.getHelmet(player);
		assert goldenHelmet != null;

		maxActivationMap.put(player, 20 * getMaxActivationSeconds());

		Sounds.HELMET_ACTIVATE.play(player);
		DecimalFormat decimalFormat = new DecimalFormat("#,###");
		AOutput.send(player, "&6&lGOLDEN HELMET! &aActivated &9Judgement&7. (&6-" + decimalFormat.format(GOLD_COST) + "g&7 per hit)");

		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				player.getWorld().playEffect(player.getLocation().add(0, 2, 0), Effect.VILLAGER_THUNDERCLOUD, 1);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 4);
	}

	@Override
	public boolean shouldActivate() {
		if(cooldownMap.containsKey(player.getUniqueId())) {
			int cooldownSeconds = getCooldownSeconds(player.getUniqueId());
			Sounds.NO.play(player);
			AOutput.error(player, "&c&lCOOLDOWN!&7 On cooldown for " + cooldownSeconds + " second" + (cooldownSeconds == 1 ? "" : "s"));
			return false;
		}

		if(HelmetManager.getUsedHelmetGold(player) < GOLD_COST) {
			Sounds.NO.play(player);
			AOutput.error(player, "&cNot enough gold!");
			return false;
		}
		return true;
	}

	@Override
	public void onDeactivate() {
		putOnCooldown(player.getUniqueId());
		maxActivationMap.remove(player);
		AOutput.send(player, "&6&lGOLDEN HELMET! &cDeactivated &9Judgement&c. &7(" + getCooldownSeconds() + "s reactivation cooldown)");
		if(runnable != null) runnable.cancel();

		new PluginMessage()
				.writeString("JUDGEMENT")
				.writeString(PitSim.serverName)
				.writeString(player.getUniqueId().toString())
				.send();
	}

	@Override
	public void onProc() {}

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to toggle", "&7Judgement. Annihilate your", "&7opponents with &eRNGesus&7.",
				"&7Auto-disables after " + getMaxActivationSeconds() + "s",
				"&7(" + getCooldownSeconds() + "s &7cooldown)",
				"",
				"&7Cost: &6" + formatter.format(GOLD_COST) + "g &7per hit");
	}

	@Override
	public ItemStack getDisplayStack() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.BEACON);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}

	public static void putOnCooldown(UUID uuid) {
		cooldownMap.put(uuid, 20 * getCooldownSeconds());
	}

	public static int getCooldownSeconds(UUID uuid) {
		return (cooldownMap.get(uuid) - 1) / 20 + 1;
	}

	public static int getCooldownSeconds() {
		return 60 * 5;
	}

	public static int getMaxActivationSeconds() {
		return 30;
	}
}
