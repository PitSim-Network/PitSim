package dev.kyro.pitsim.battlepass.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class PassGUI extends AGUI {
	public PitPlayer pitPlayer;

	public PassPanel passPanel;
	public QuestPanel questPanel;

	public PassGUI(Player player) {
		super(player);
		this.pitPlayer = PitPlayer.getPitPlayer(player);

		passPanel = new PassPanel(this);
		questPanel = new QuestPanel(this);
		setHomePanel(passPanel);
	}
}
