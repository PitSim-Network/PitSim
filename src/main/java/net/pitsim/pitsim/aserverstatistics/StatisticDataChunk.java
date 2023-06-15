package net.pitsim.pitsim.aserverstatistics;

import net.pitsim.pitsim.controllers.EnchantManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PluginMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticDataChunk {
	private final long startTime = System.currentTimeMillis();
	public List<Record> records = new ArrayList<>();

	public StatisticDataChunk() {
		for(StatisticCategory category : StatisticCategory.values()) records.add(new Record(null, category));
		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) {
			for(StatisticCategory category : pitEnchant.statisticCategories) records.add(new Record(pitEnchant, category));
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public boolean hasExpired() {
		return System.currentTimeMillis() - startTime > StatisticsManager.CHUNK_CAPTURE_DURATION;
	}

	public void send() {
		PluginMessage pluginMessage = new PluginMessage()
				.writeString("SERVER_STATISTICS")
				.writeLong(startTime)
				.writeInt(getNonEmptyRecordCount());

		for(Record record : records) record.writeToMessage(pluginMessage);

		pluginMessage.send();
	}

	public int getNonEmptyRecordCount() {
		int count = 0;
		for(Record record : records) if(record.totalHits != 0) count++;
		return count;
	}

	public static class Record {
		private final PitEnchant pitEnchant;
		private final StatisticCategory category;
		private int totalHits;
		private final Map<PitEnchant, Integer> hitsWithEnchant = new LinkedHashMap<>();

		public Record(PitEnchant pitEnchant, StatisticCategory category) {
			this.pitEnchant = pitEnchant;
			this.category = category;

			for(PitEnchant enchant : EnchantManager.pitEnchants) {
				if(enchant.statisticCategories.isEmpty()) continue;
				hitsWithEnchant.put(enchant, 0);
			}
		}

		public void logAttack(Map<PitEnchant, Integer> enchantMap) {
			for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {
				PitEnchant testEnchant = entry.getKey();
				hitsWithEnchant.put(testEnchant, hitsWithEnchant.get(testEnchant) + 1);
			}
			totalHits++;
		}

		public void writeToMessage(PluginMessage pluginMessage) {
			if(totalHits == 0) return;
			pluginMessage
					.writeString(pitEnchant == null ? "" : pitEnchant.refNames.get(0))
					.writeString(category.name())
					.writeInt(totalHits);

			for(Map.Entry<PitEnchant, Integer> entry : hitsWithEnchant.entrySet())
				pluginMessage.writeInt(entry.getValue());
		}

		public PitEnchant getPitEnchant() {
			return pitEnchant;
		}

		public StatisticCategory getCategory() {
			return category;
		}
	}
}
