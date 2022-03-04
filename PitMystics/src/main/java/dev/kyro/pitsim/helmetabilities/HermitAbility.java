package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class HermitAbility extends HelmetAbility {
	public BukkitTask runnable;

	public HermitAbility(Player player) {

		super(player, "Hermit", "hermit", true, 14);
		cost = 1500;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!isActive || player != attackEvent.defender) return;
		attackEvent.trueDamage = 0;
	}

	@Override
	public void onActivate() {
		runnable = new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				ItemStack goldenHelmet = GoldenHelmet.getHelmet(player);
				assert goldenHelmet != null;
				if(!GoldenHelmet.withdrawGold(player, goldenHelmet, cost)) {
					AOutput.error(player, "&cNot enough gold!");
					GoldenHelmet.deactivate(player);
					Sounds.NO.play(player);
				} else {
					Sounds.HELMET_TICK.play(player);
					if(count++ % 2 == 0) {
						Misc.applyPotionEffect(player, PotionEffectType.SLOW, 100, 1, true, false);
						Misc.applyPotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 100, 1, true, false);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20L, 20);
		Sounds.HELMET_ACTIVATE.play(player);
		AOutput.send(player, "&6&lGOLDEN HELMET! &aActivated &9Hermit&7. (&6-1,500g&7 per second)");
	}

	@Override
	public boolean shouldActivate() {
		ItemStack goldenHelmet = GoldenHelmet.getHelmet(player);
		assert goldenHelmet != null;
		if(!GoldenHelmet.withdrawGold(player, goldenHelmet, cost * 10)) {
			AOutput.error(player, "&cNot enough gold!");
			Sounds.NO.play(player);
			return false;
		}
		return true;
	}

	@Override
	public void onDeactivate() {
		runnable.cancel();
		AOutput.send(player, "&6&lGOLDEN HELMET! &cDeactivated &9Hermit&c.");
	}

	@Override
	public void onProc() {
	}

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to toggle Hermit.", "&7Receive permanent resistance I,", "&7slowness II, and true damage immunity", "",
				"&7Cost: &6" + formatter.format(cost * 10L) + "g &7on activation", "&7Cost: &6" + formatter.format(cost) + "g &7per second");
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.BEDROCK);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}
}
