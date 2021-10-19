package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.*;

public class NonAnticheat implements Listener {
	private static final Map<UUID, AnticheatData> anticheatMap = new HashMap<>();
	private static final Map<UUID, Long> recentAlertMap = new HashMap<>();

	public static AnticheatData getPlayerLogs(Player player) {
		anticheatMap.putIfAbsent(player.getUniqueId(), new AnticheatData(player));
		return anticheatMap.get(player.getUniqueId());
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(NonManager.getNon(attackEvent.attacker) != null || NonManager.getNon(attackEvent.defender) == null) return;
		if(attackEvent.pet != null || attackEvent.arrow != null) return;
		Location playerLoc = attackEvent.attacker.getLocation();
		Location nonLoc = attackEvent.defender.getLocation();

		double distance = playerLoc.distance(nonLoc);
		if(distance < 2.5 || distance > 10) return;

		Vector playerNonVector = nonLoc.toVector().subtract(playerLoc.toVector()).normalize();
		Vector playerDir = attackEvent.attacker.getLocation().getDirection();

		double angle = Math.acos(playerNonVector.dot(playerDir));
		angle = Math.toDegrees(angle);

		anticheatMap.putIfAbsent(attackEvent.attacker.getUniqueId(), new AnticheatData(attackEvent.attacker));
		AnticheatData anticheatData = anticheatMap.get(attackEvent.attacker.getUniqueId());
		anticheatData.addData(new AnticheatData.HitData(angle, distance));
	}

	public static class AnticheatData {
		public UUID uuid;
		public String name;
		private final List<HitData> hitDataList = new ArrayList<>();

		public AnticheatData(Player player) {
			this.uuid = player.getUniqueId();
			this.name = player.getName();
		}

		public boolean hasLogs() {
			clean();
			return !hitDataList.isEmpty();
		}

		public int getRecentHits() {
			clean();
			return hitDataList.size();
		}

		public double getAbnormalDistancePercent() {
			clean();
			double abnormal = 0;
			for(HitData hitData : hitDataList) if(hitData.distance > 4) abnormal++;
			return (abnormal / hitDataList.size()) * 100;
		}

		public double getAbnormalAnglePercent() {
			clean();
			double abnormal = 0;
			for(HitData hitData : hitDataList) if(hitData.angle > 40) abnormal++;
			return (abnormal / hitDataList.size()) * 100;
		}

		private void clean() {
			ArrayList<HitData> toRemove = new ArrayList<>();
			for(HitData hitData : hitDataList) {
				if(hitData.time + 1000 * 60 < System.currentTimeMillis()) toRemove.add(hitData);
			}
			hitDataList.removeAll(toRemove);
		}

		public void addData(HitData hitData) {
			hitDataList.add(hitData);

			double abnormalAngle = getAbnormalAnglePercent();
			double abnormalDistance = getAbnormalDistancePercent();
			if(getRecentHits() < 10) return;
			if(abnormalAngle < AConfig.getInt("anticheat.abnormal-angle") && abnormalDistance < AConfig.getInt("anticheat.abnormal-distance")) return;
			if(recentAlertMap.containsKey(uuid) && recentAlertMap.get(uuid) + AConfig.getInt("anticheat.alert-interval") * 1000L >= System.currentTimeMillis()) return;
			recentAlertMap.put(uuid, System.currentTimeMillis());
			DecimalFormat format = new DecimalFormat("0.0");
			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(!onlinePlayer.isOp()) continue;
				if(!AConfig.getStringList("whitelisted-ips").contains(onlinePlayer.getAddress().getAddress().toString())) return;
				AOutput.send(onlinePlayer, "&c&lANTICHEAT&6 " + name + " &fmay be cheating &7(p: " + ((CraftPlayer) player).getHandle().ping + "ms, d: "+
						format.format(abnormalDistance) + ", a: " + format.format(abnormalAngle) + ")");
			}
		}

		public static class HitData {
			public long time;
			public double angle;
			public double distance;

			public HitData(double angle, double distance) {
				this.time = new Date().getTime();
				this.angle = angle;
				this.distance = distance;
			}
		}
	}
}
