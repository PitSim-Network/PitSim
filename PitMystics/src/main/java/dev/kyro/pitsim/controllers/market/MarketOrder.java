package dev.kyro.pitsim.controllers.market;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public class MarketOrder {

	public double pure;
	public int philo;
	public int feathers;
	public double water;

	public MarketOrder(double pure, int philo, int feathers, double water) {
		this.pure = pure;
		this.philo = philo;
		this.feathers = feathers;
		this.water = water;
	}

	public MarketOrder(ConfigurationSection save) {

		this.pure = save.getDouble("pure");
		this.philo = save.getInt("philo");
		this.feathers = save.getInt("feathers");
		this.water = save.getDouble("water");
	}

	public double getTotalWorth(Currency currency) {

		double pureCost = pure;
		pureCost += philo / 10D;
		pureCost += feathers * 2D;
		pureCost += water / 3.5D;

		switch(currency) {
			case PURE:
				return pureCost;
			case PHILO:
				return pureCost * 10;
			case FEATHER:
				return pureCost / 2;
			case WATER:
				return pureCost * 3.5;
		}
		return -1;
	}

	public double getSpecificWorth(Currency currency) {

		switch(currency) {
			case PURE:
				return pure;
			case PHILO:
				return philo;
			case FEATHER:
				return feathers;
			case WATER:
				return water;
		}
		return -1;
	}

	public Currency getHighestWorth() {

		Currency highestWorthCurrency = null;
		double highestWorth = -1;
		for(Currency value : Currency.values()) {

			double specificWorth = getSpecificWorth(value);
			if(specificWorth <= highestWorth) continue;

			highestWorthCurrency = value;
			highestWorth = specificWorth;
		}

		return highestWorthCurrency;
	}

	public ConfigurationSection createSave() {
		ConfigurationSection save = new MemoryConfiguration();
		save.set("pure", pure);
		save.set("philo", philo);
		save.set("feathers", feathers);
		save.set("water", water);
		return save;
	}
}
