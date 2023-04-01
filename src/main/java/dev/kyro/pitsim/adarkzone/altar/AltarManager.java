package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneLeveling;
import dev.kyro.pitsim.adarkzone.altar.pedestals.*;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AltarManager implements Listener {
	public static final Location TEXT_LOCATION = new Location(MapManager.getDarkzone(), 221.5, 96, -83.5);
	public static final Location CONFIRM_LOCATION = new Location(MapManager.getDarkzone(), 221.5, 90.5, -83.5);
	public static final Location ALTAR_CENTER = new Location(MapManager.getDarkzone(), 221.5, 93, -83.5);
	public static final int EFFECT_CHUNK_RADIUS = 5;
	public static final int EFFECT_RADIUS = EFFECT_CHUNK_RADIUS + 3;

	public static final List<AltarAnimation> animations = new ArrayList<>();
	public static ArmorStand[] textStands = new ArmorStand[7];

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(onlinePlayer.getWorld() != MapManager.getDarkzone()) continue;
					if(onlinePlayer.getLocation().distance(TEXT_LOCATION) > 30) {
						AltarPedestal.disableAll(onlinePlayer);
						continue;
					}

					setDefaultText(onlinePlayer);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
	}


	public static void init() {
		for(int i = 0; i < 7; i++) {
			ArmorStand stand = (ArmorStand) MapManager.getDarkzone().spawnEntity(TEXT_LOCATION.clone().add(0, -0.3 * i, 0), EntityType.ARMOR_STAND);
			stand.setMarker(true);
			stand.setVisible(false);
			stand.setCustomNameVisible(true);
			stand.setArms(true);
			stand.setCustomName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "TAINTED ALTAR");
			stand.setGravity(false);

			textStands[i] = stand;
		}

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

	public static void setText(Player player, String[] text) {
		if(text.length != 7) return;

		for(int i = 0; i < 7; i++) {
			if(text[i] == null) text[i] = "";

			DataWatcher dw = ((CraftEntity)textStands[i]).getHandle().getDataWatcher();
			dw.watch(2, (Object)ChatColor.translateAlternateColorCodes('&', text[i]));
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textStands[i]), dw, false);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
		}
	}

	public static void setDefaultText(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);

		int altarLevel = DarkzoneLeveling.getLevel(pitPlayer.darkzoneData.altarXP);
		int difference = info.darkzoneLevelIncrease - altarLevel;
		String altarPercent = DarkzoneLeveling.getReductionPercent(pitPlayer);

		String color = difference > 0 ? "&c-" : "&a+";
		String status = difference > 0 ? "&7Taking &f" + altarPercent + "% &7of &bXP &7and &6Gold" : "&aStronger than the Darkzone!";

		DecimalFormat decimalFormat = new DecimalFormat("#,##0");
		setText(player, new String[] {
				"&5Darkzone Level: " + decimalFormat.format(info.darkzoneLevelIncrease),
				"&8&m----------------------",
				"&4&lAltar Level",
				"&4" + decimalFormat.format(altarLevel) + " " + AUtil.createProgressBar("|", ChatColor.RED, ChatColor.GRAY, 30,
						DarkzoneLeveling.getXPProgressToNextLevel(pitPlayer.darkzoneData.altarXP) /
								DarkzoneLeveling.getXPForLevel(altarLevel + 1)) + " &4" + decimalFormat.format(altarLevel + 1),
				"&8&m----------------------",
				"&7Level Difference: " + color + decimalFormat.format(Math.abs(difference)),
				status
		});
	}

	public static void cleanUp() {
		for(ArmorStand textStand : textStands) {
			textStand.remove();
		}
		AltarPedestal.cleanUp();
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
	public void onQuit(PlayerQuitEvent event) {
		AltarPedestal.disableAll(event.getPlayer());

		AltarAnimation altarAnimation = getAnimation(event.getPlayer());
		if(altarAnimation == null) return;
		altarAnimation.onQuit();
	}

	public static void activateAltar(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.taintedSouls -= AltarPedestal.getTotalCost(player);
		pitPlayer.stats.soulsSacrificed += AltarPedestal.getTotalCost(player);

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
		for(ArmorStand textStand : textStands) {
			PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textStand));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
		}
	}

	public static void enableText(Player player) {
		for(ArmorStand textStand : textStands) {
			PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(((CraftEntity) textStand).getHandle(), 78);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawnPacket);

			DataWatcher dw = ((CraftEntity)textStand).getHandle().getDataWatcher();
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textStand), dw, true);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
		}
	}

	public static double getReduction(PitPlayer pitPlayer) {
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);

		int altarLevel = DarkzoneLeveling.getLevel(pitPlayer.darkzoneData.altarXP);
		int difference = prestigeInfo.darkzoneLevelIncrease - altarLevel;
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
