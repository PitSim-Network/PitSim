package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PhoenixAbility extends HelmetAbility {
	public static List<UUID> alreadyActivatedList = new ArrayList<>();
	public static int cost = 75_000;

	public PhoenixAbility(Player player) {

		super(player, "Phoenix", "phoenix", false, 13);
	}

	@EventHandler
	public static void onKill(KillEvent killEvent) {
		alreadyActivatedList.remove(killEvent.dead.getUniqueId());
	}

	@EventHandler
	public static void onLogout(PlayerQuitEvent event) {
		alreadyActivatedList.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public static void onOof(OofEvent event) {
		alreadyActivatedList.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public static void onHeal(HealEvent healEvent) {
		if(!alreadyActivatedList.contains(healEvent.player.getUniqueId())) return;
		healEvent.multipliers.add(0D);
	}

	@Override
	public void onProc() {
		ItemStack goldenHelmet = GoldenHelmet.getHelmet(player);

		if(alreadyActivatedList.contains(player.getUniqueId())) {
			AOutput.error(player, "&cAbility can only be used once per life!");
			Sounds.NO.play(player);
			return;
		}

		assert goldenHelmet != null;
		if(!GoldenHelmet.withdrawGold(player, goldenHelmet, cost)) {
			AOutput.error(player, "&cNot enough gold!");
			Sounds.NO.play(player);
			return;
		}

		Misc.applyPotionEffect(player, PotionEffectType.INCREASE_DAMAGE, 200, 0, true, false);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.heal(player.getMaxHealth());
		pitPlayer.heal(player.getMaxHealth() * 2, HealEvent.HealType.ABSORPTION, (int) player.getMaxHealth() * 2);
		alreadyActivatedList.add(player.getUniqueId());
		for(Entity entity : player.getNearbyEntities(5, 5, 5)) {
			if(!(entity instanceof Player)) continue;
			Player target = (Player) entity;
			Non non = NonManager.getNon(target);

			if(target == player) continue;
			PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
			if(non == null) {
				if(pitTarget.megastreak.getClass() == Uberstreak.class && pitTarget.megastreak.isOnMega()) continue;
				Vector force = target.getLocation().toVector().subtract(player.getLocation().toVector())
						.setY(1).normalize().multiply(1.15);
				target.setVelocity(force);
			}
		}

		DecimalFormat decimalFormat = new DecimalFormat("#,###");
		AOutput.send(player, "&6&lGOLDEN HELMET! &7Used &9Phoenix&7! (&6-" + decimalFormat.format(cost) + "g&7)");
		Sounds.PHOENIX.play(player);
	}

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to rebirth:",
				"&a\u25a0 &7Heal to &cfull HP",
				"&a\u25a0 &cStrength I &7(10s)",
				"&a\u25a0 &7Gain &6absorption &7equal to 2x your max hp",
				"&c\u25a0 &7You cannot heal until you die or spawn",
				"", "&7Cost: &6" + formatter.format(cost) + "g");
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.BLAZE_POWDER);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}

	@Override
	public void onActivate() {
	}

	@Override
	public boolean shouldActivate() {
		return false;
	}

	@Override
	public void onDeactivate() {
	}
}
