package dev.kyro.pitsim.adarkzone.progression;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public abstract class MinorProgressionUnlock extends ProgressionUnlock {

	public MinorProgressionUnlock() {
		if(getLevels().size() != 6) throw new RuntimeException();
	}

	public abstract List<LevelData> getLevels();

	public LevelData getLevel(int level) {
		return getLevels().get(level);
	}

	@Override
	public ItemStack getDisplayStack() {
//		ItemStack itemStack = new AItemStackBuilder(ge)
		return null;
	}

	public static class LevelData {
		public List<Double> values;

		public LevelData(Double... values) {
			this.values = Arrays.asList(values);
		}
	}
}
