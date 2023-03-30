package dev.kyro.pitsim.aserverstatistics;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.overworld.Regularity;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsManager implements Listener {
	public static final long CHUNK_CAPTURE_DURATION = 1000 * 60 * 60;

	private static StatisticDataChunk dataChunk = new StatisticDataChunk();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				getDataChunk();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L * 60);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerRealPlayer() || attackEvent.isFakeHit() || Regularity.isRegHit(attackEvent.getDefender())) return;

		List<StatisticCategory> applicableCategories = new ArrayList<>();

		PitMob defenderMob = DarkzoneManager.getPitMob(attackEvent.getDefender());
		PitBoss defenderBoss = BossManager.getPitBoss(attackEvent.getDefender());
		if(attackEvent.isDefenderPlayer() && attackEvent.getAttacker().getWorld() == MapManager.currentMap.world) {
			if(MapManager.currentMap.getMid().distance(attackEvent.getAttacker().getLocation()) < 10) {
				applicableCategories.add(StatisticCategory.OVERWORLD_STREAKING);
			} else {
				applicableCategories.add(StatisticCategory.OVERWORLD_PVP);
			}
		}

		if(PitSim.status.isDarkzone()) {
			if(attackEvent.isDefenderRealPlayer()) {
				applicableCategories.add(StatisticCategory.DARKZONE_VS_PLAYER);
			} else if(defenderMob != null) {
				applicableCategories.add(StatisticCategory.DARKZONE_VS_MOB);
			} else if(defenderBoss != null) {
				applicableCategories.add(StatisticCategory.DARKZONE_VS_BOSS);
			}
		}

		for(StatisticCategory category : applicableCategories) logAttack(category, attackEvent.getAttackerEnchantMap());
	}

	public static void logAttack(StatisticCategory category, Map<PitEnchant, Integer> enchantMap) {
		for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {
			PitEnchant pitEnchant = entry.getKey();
			StatisticDataChunk.Record record = getRecord(pitEnchant, category);
			record.logAttack(enchantMap);
		}
	}

	public static void resetDataChunk() {
		dataChunk = new StatisticDataChunk();
	}

	public static StatisticDataChunk getDataChunk() {
		if(dataChunk.hasExpired()) {
			dataChunk.send();
			dataChunk = new StatisticDataChunk();
		}
		return dataChunk;
	}

	public static StatisticDataChunk.Record getRecord(PitEnchant pitEnchant, StatisticCategory category) {
		StatisticDataChunk dataChunk = getDataChunk();
		for(StatisticDataChunk.Record record : dataChunk.records) {
			if(record.getPitEnchant() != pitEnchant || record.getCategory() != category) continue;
			return record;
		}
		throw new RuntimeException();
	}
}
