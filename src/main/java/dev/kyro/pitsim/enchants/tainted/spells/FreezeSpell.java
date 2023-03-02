package dev.kyro.pitsim.enchants.tainted.spells;

import com.sk89q.worldedit.EditSession;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.SchematicPaste;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreezeSpell extends PitEnchant {
	public static Map<EditSession, Location> sessionMap = new HashMap<>();
	public static Map<Location, Material> blockMap = new HashMap<>();

	public FreezeSpell() {
		super("Freeze", true, ApplyType.SCYTHES, "freeze", "cold");
		isTainted = true;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Block block = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
		if(block.getType().equals(Material.AIR) && event.getPlayer().getLocation().subtract(0, 2, 0).getBlock().getType() == Material.AIR) {
			AOutput.send(event.getPlayer(), "&c&lERROR!&7 Must be standing on a block!");
			Sounds.NO.play(event.getPlayer());
			return;
		}

		Cooldown cooldown = getCooldown(event.getPlayer(), 40);
		if(cooldown.isOnCooldown()) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(event.getPlayer());
			return;
		}
		cooldown.restart();

		for(Location value : sessionMap.values()) {
			if(value.distance(event.getPlayer().getLocation()) < 12) {
				AOutput.error(event.getPlayer(), "&c&lERROR!&7 Too close to another spell!");
				Sounds.NO.play(event.getPlayer());
				return;
			}
		}

		Player player = event.getPlayer();
		Location location;
		if(player.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
			location = player.getLocation().subtract(0, 1, 0);
		else location = player.getLocation();

		player.spigot().playEffect(player.getLocation().add(0, 1, 0),
				Effect.SNOWBALL_BREAK, 1, 1, 2F, 2F, 2F, 0.2F, 250, 6);
		player.spigot().playEffect(player.getLocation().add(0, 1, 0),
				Effect.STEP_SOUND, 174, 1, 2F, 2F, 2F, 0.2F, 250, 6);
		Sounds.FREEZE1.play(player);

		for(Entity nearbyEntity : event.getPlayer().getNearbyEntities(6, 6, 6)) {
			if(!Misc.isEntity(nearbyEntity, PitEntityType.REAL_PLAYER, PitEntityType.PIT_MOB, PitEntityType.PIT_BOSS))

			Misc.applyPotionEffect((LivingEntity) nearbyEntity, PotionEffectType.SLOW, 40, 100, false, false);
			Misc.applyPotionEffect((LivingEntity) nearbyEntity, PotionEffectType.WEAKNESS, 40, 100, false, false);
			nearbyEntity.getWorld().playEffect(nearbyEntity.getLocation(), Effect.SNOW_SHOVEL, 5);
			nearbyEntity.getWorld().playEffect(nearbyEntity.getLocation().add(0, 1, 0), Effect.SNOWBALL_BREAK, 5);

			if(!blockMap.containsKey(nearbyEntity.getLocation().getBlock().getLocation()) && nearbyEntity.getLocation().getBlock().getType() == Material.AIR) {
				blockMap.put(nearbyEntity.getLocation().getBlock().getLocation(), nearbyEntity.getLocation().getBlock().getType());
				nearbyEntity.getLocation().getBlock().setType(Material.ICE);
			}
			if(!blockMap.containsKey(nearbyEntity.getLocation().add(0, 1, 0).getBlock().getLocation()) && nearbyEntity.getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
				blockMap.put(nearbyEntity.getLocation().add(0, 1, 0).getBlock().getLocation(), nearbyEntity.getLocation().add(0, 1, 0).getBlock().getType());
				nearbyEntity.getLocation().add(0, 1, 0).getBlock().setType(Material.ICE);
			}

			Location tp = nearbyEntity.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
			((CraftEntity) nearbyEntity).getHandle().setLocation(tp.getX(), tp.getY(), tp.getZ(), tp.getPitch(), tp.getYaw());
			nearbyEntity.teleport(tp);
		}

		EditSession session = SchematicPaste.loadSchematicAir(new File("plugins/WorldEdit/schematics/frozen.schematic"), location);
		sessionMap.put(session, player.getLocation());

		new BukkitRunnable() {
			@Override
			public void run() {
				session.undo(session);
				sessionMap.remove(session);
				Sounds.FREEZE2.play(player);

				for(Map.Entry<Location, Material> entry : blockMap.entrySet()) {
					entry.getKey().getBlock().setType(entry.getValue());
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 40);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		int seconds = getDuration(enchantLvl) / 20;
		return new PitLoreBuilder(
				"&7Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana, " +
						"freezing all nearby enemies for " + decimalFormat.format(seconds) + " second" +
						(seconds == 1 ? "" : "s")
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 30 * (4 - enchantLvl);
	}

	public static int getDuration(int enchantLvl) {
		return 20 * 3;
	}
}
