package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PhoenixAbility extends HelmetAbility {
	public static List<UUID> alreadyActivatedList = new ArrayList<>();
	public static int cost = 40_000;

	public PhoenixAbility(Player player) {

		super(player, "Phoenix", "phoenix", false, 13);
	}

	@EventHandler
	public static void onKill(KillEvent killEvent) {
		if(killEvent.isDeadPlayer()) alreadyActivatedList.remove(killEvent.deadPlayer.getUniqueId());

//		TODO: Switch method
		if(!killEvent.isKillerPlayer() || !killEvent.isDeadPlayer() ||
				!Bukkit.getOnlinePlayers().contains(killEvent.getKillerPlayer()) || !Bukkit.getOnlinePlayers().contains(killEvent.getDeadPlayer())) return;
		alreadyActivatedList.remove(killEvent.getKillerPlayer().getUniqueId());
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

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.megastreak.getClass() == RNGesus.class) {
			AOutput.error(player, "&c&lERROR!&7 You cannot do this while &e&lRNGESUS&7 is equipped");
			Sounds.NO.play(player);
			return;
		}

		if(alreadyActivatedList.contains(player.getUniqueId())) {
			AOutput.error(player, "&cAbility has already been used!");
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

		pitPlayer.heal(player.getMaxHealth());
		pitPlayer.heal(player.getMaxHealth() * 2, HealEvent.HealType.ABSORPTION, (int) player.getMaxHealth() * 2);
		alreadyActivatedList.add(player.getUniqueId());
		if(!SpawnManager.isInSpawn(player.getLocation())) {
			for(Entity entity : player.getNearbyEntities(5, 5, 5)) {
				if(!(entity instanceof Player)) continue;
				Player target = (Player) entity;
				Non non = NonManager.getNon(target);

				if(target == player) continue;
				PitPlayer pitTarget = PitPlayer.getPitPlayer(target);
				if(non == null) {
					if(pitTarget.megastreak.getClass() == Uberstreak.class && pitTarget.megastreak.isOnMega()) continue;
					if(SpawnManager.isInSpawn(target.getLocation())) continue;
					Vector force = target.getLocation().toVector().subtract(player.getLocation().toVector())
							.setY(1).normalize().multiply(1.15);
					target.setVelocity(force);
				}
			}
		}

		DecimalFormat decimalFormat = new DecimalFormat("#,###");
		AOutput.send(player, "&6&lGOLDEN HELMET! &7Used &9Phoenix&7! (&6-" + decimalFormat.format(cost) + "g&7)");
		Sounds.PHOENIX.play(player.getLocation(), 15);

		World world = player.getWorld();
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(count++ == 5) {
					cancel();
					return;
				}

				for(int i = 0; i < 50; i++) {
					double x = player.getLocation().getX() + Math.random() * 20 - 10;
					double y = player.getLocation().getY() + Math.random() * 12 - 2;
					double z = player.getLocation().getZ() + Math.random() * 20 - 10;

					Location particleLoc = new Location(world, x, y, z);
					double distance = particleLoc.distance(player.getLocation());
					if(Math.min(20 - distance, 10) > Math.random() * 20) continue;

					world.playEffect(particleLoc, Effect.LAVA_POP, 1);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1);
	}

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to rebirth:",
				"&a\u25a0 &7Heal to &cfull HP",
				"&a\u25a0 &cStrength I &7(10s)",
				"&a\u25a0 &7Gain &6absorption &7equal to",
				"&72x your max hp",
				"&c\u25a0 &7You cannot heal until you die,",
				"&7spawn, or get a player kill",
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
