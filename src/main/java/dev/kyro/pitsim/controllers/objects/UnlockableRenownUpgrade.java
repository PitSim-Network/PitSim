package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;

public abstract class UnlockableRenownUpgrade extends RenownUpgrade {

	public UnlockableRenownUpgrade(String name, String refName, int prestigeReq) {
		super(name, refName, prestigeReq);
	}

	public UnlockableRenownUpgrade(String name, String refName, int prestigeReq, Class<? extends AGUIPanel> subPanel) {
		super(name, refName, prestigeReq, subPanel);
	}

	public abstract String getEffect();
	public abstract int getUnlockCost();

	@Override
	public PitLoreBuilder getBaseDescription(Player player) {
		PitLoreBuilder loreBuilder = new PitLoreBuilder();
		loreBuilder.addLongLine(getEffect());
		return loreBuilder;
	}
}
