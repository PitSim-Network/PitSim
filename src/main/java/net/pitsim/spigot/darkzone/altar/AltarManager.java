package net.pitsim.spigot.darkzone.altar;

import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.darkzone.DarkzoneLeveling;
import net.pitsim.spigot.darkzone.altar.pedestals.*;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.PrestigeValues;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.PitQuitEvent;
import net.pitsim.spigot.holograms.Hologram;
import net.pitsim.spigot.holograms.RefreshMode;
import net.pitsim.spigot.holograms.ViewMode;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public  class AltarManager implements Listener {
	public static final Location TEXT_LOCATION = new Location(MapManager.getDarkzone(), 221.5, 96, -83.5);
	public static final Location CONFIRM_LOCATION = new Location(MapManager.getDarkzone(), 221.5, 90.5, -83.5);
	public static final Location ALTAR_CENTER = new Location(MapManager.getDarkzone(), 221.5, 93, -83.5);
	public static final int EFFECT_CHUNK_RADIUS = 5;
	public static final int EFFECT_RADIUS = EFFECT_CHUNK_RADIUS + 3;

	public static final List<AltarAnimation> animations = new ArrayList<>();
	public static Hologram hologram;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(onlinePlayer.getWorld() != MapManager.getDarkzone()) continue;
					if(onlinePlayer.getLocation().distance(TEXT_LOCATION) > 30) AltarPedestal.disableAll(onlinePlayer);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
	}


	public static void init() {

		hologram = new Hologram(TEXT_LOCATION, ViewMode.ALL, RefreshMode.MANUAL) {
			@Override
			public List<String> getStrings(Player player) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);

				int altarLevel = DarkzoneLeveling.getLevel(pitPlayer.darkzoneData.altarXP);
				int difference = info.getDarkzoneLevel() - altarLevel;
				String altarPercent = DarkzoneLeveling.getReductionPercent(pitPlayer);

				String color = difference > 0 ? "&c-" : "&a+";
				if(difference == 0) color = color.replaceAll("\\+", "");
				String status;
				if(difference > 0) {
					status = "&7Receiving &c" + altarPercent + "% &7of &bXP &7and &6Gold";
				} else if(difference < 0) {
					status = "&7Receiving &a" + altarPercent + "% &7of &bXP &7and &6Gold";
				} else {
					status = "&aEven with the Darkzone";
				}

				DecimalFormat decimalFormat = new DecimalFormat("#,##0");
				List<String> strings = new ArrayList<>();
				strings.add("&5Darkzone Level: " + decimalFormat.format(info.getDarkzoneLevel()));
				strings.add("&8&m----------------------");
				strings.add("&4&lAltar Level");
				strings.add("&4" + decimalFormat.format(altarLevel) + " " + AUtil.createProgressBar("|", ChatColor.RED, ChatColor.GRAY, 30,
				DarkzoneLeveling.getXPProgressToNextLevel(pitPlayer.darkzoneData.altarXP) /
						DarkzoneLeveling.getXPForLevel(altarLevel + 1)) + " &4" + decimalFormat.format(altarLevel + 1));
				strings.add("&8&m----------------------");
				strings.add("&7Level Difference: " + color + decimalFormat.format(Math.abs(difference)));
				strings.add(status);

				return strings;
			}
		};

		new KnowledgePedestal(new Location(MapManager.getDarkzone(), 224.5, 91.5, -85.5));
		new RenownPedestal(new Location(MapManager.getDarkzone(), 224.5, 91.5, -82.5));
		new HeresyPedestal(new Location(MapManager.getDarkzone(), 221.5, 91.5, -80.5));
		new WealthPedestal(new Location(MapManager.getDarkzone(), 218.5, 91.5, -82.5));
		new TurmoilPedestal(new Location(MapManager.getDarkzone(), 218.5, 91.5, -85.5));

		List<Chunk> chunks = new ArrayList<>();
		for(int x = -1 * EFFECT_CHUNK_RADIUS; x <= EFFECT_CHUNK_RADIUS; x++) {
			for(int z = -1 * EFFECT_CHUNK_RADIUS; z <= EFFECT_CHUNK_RADIUS; z++) {
				Chunk chunk = CONFIRM_LOCATION.clone().add(x, 0, z).getChunk();
				if(!chunks.contains(chunk)) chunks.add(chunk);
			}
		}

		BiomeChanger.chunkList.addAll(chunks);
		Heartbeat.init();
	}

	public static int getStandID(ArmorStand stand) {
		for(Entity entity : Bukkit.getWorld("darkzone").getNearbyEntities(CONFIRM_LOCATION, 10.0, 10.0, 10.0)) {
			if(!(entity instanceof ArmorStand) || !entity.isValid()) {
				continue;
			}
			if(entity.getUniqueId().equals(stand.getUniqueId())) {
				return entity.getEntityId();
			}
		}
		return 0;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		Block block = CONFIRM_LOCATION.clone().add(0, 2, 0).getBlock();
		if(!event.getClickedBlock().equals(block)) return;
		if(isInAnimation(player)) return;

		AltarGUI altarGUI = new AltarGUI(player);
		altarGUI.open();
	}

	@EventHandler
	public void onQuit(PitQuitEvent event) {
		AltarPedestal.disableAll(event.getPlayer());

		AltarAnimation altarAnimation = getAnimation(event.getPlayer());
		if(altarAnimation == null) return;
		altarAnimation.onQuit();
	}

	public static void activateAltar(Player player, int costOverride) {
		if(PitSim.isDev()) {
			int ticks = AltarRewards.getTurmoilTicks(player);
			double turmoilMultiplier = AltarPedestal.getPedestal(TurmoilPedestal.class).isActivated(player) ? ticks * 0.1 : 1;
			AltarRewards.rewardPlayer(player, turmoilMultiplier);
			return;
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.taintedSouls -= (costOverride > -1 ? costOverride : AltarPedestal.getTotalCost(player));
		pitPlayer.stats.soulsSacrificed += (costOverride >= -1 ? costOverride : AltarPedestal.getTotalCost(player));

		List<AltarPedestal> pedestals = new ArrayList<>();
		for(AltarPedestal pedestal : AltarPedestal.altarPedestals) {
			if(pedestal.isActivated(player)) {
				pedestals.add(pedestal);
			}
		}

		disableText(player);
		int ticks = AltarRewards.getTurmoilTicks(player);
		double turmoilMultiplier = AltarPedestal.getPedestal(TurmoilPedestal.class).isActivated(player) ? ticks * 0.1 : 1;

		BukkitRunnable callback = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.strikeLightningForPlayers(CONFIRM_LOCATION, player);
				AltarRewards.rewardPlayer(player, turmoilMultiplier);
				AltarPedestal.disableAll(player);
				enableText(player);
			}
		};

		animations.add(new AltarAnimation(player, AltarPedestal.getTotalCost(player), pedestals, ticks, callback));
	}

	public static void disableText(Player player) {
		hologram.removePermittedViewer(player);
	}

	public static void enableText(Player player) {
		hologram.addPermittedViewer(player);
	}

	public static double getReduction(PitPlayer pitPlayer) {
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);

		int altarLevel = DarkzoneLeveling.getLevel(pitPlayer.darkzoneData.altarXP);
		int difference = prestigeInfo.getDarkzoneLevel() - altarLevel;
		if(difference <= 0) return 0;
		return 100 - 100 * Math.pow(0.99, difference);
	}

	public static String getReductionFormatted(PitPlayer pitPlayer) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		return decimalFormat.format(getReduction(pitPlayer));
	}

	public static double getReductionMultiplier(PitPlayer pitPlayer) {
		return Misc.getReductionMultiplier(getReduction(pitPlayer));
	}

	public static AltarAnimation getAnimation(Player player) {
		for(AltarAnimation animation : animations) {
			if(animation.player.equals(player)) {
				return animation;
			}
		}
		return null;
	}

	public static boolean isInAnimation(Player player) {
		return getAnimation(player) != null;
	}
}
