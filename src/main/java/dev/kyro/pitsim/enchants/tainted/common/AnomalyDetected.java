package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class AnomalyDetected extends PitEnchant {
	public static AnomalyDetected INSTANCE;

	public AnomalyDetected() {
		super("Anomaly Detected!", false, ApplyType.SCYTHES,
				"anamolydetected", "anamoly", "anomalydetected", "anomaly");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder(
				"&7A basic tainted enchant"
		).getLore();
	}
}
