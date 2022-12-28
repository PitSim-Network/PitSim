package dev.kyro.pitsim.inventories.stats;

import dev.kyro.arcticapi.gui.AGUI;
import org.bukkit.entity.Player;

public class StatGUI extends AGUI {
	public StatMenuPanel menuPanel;
	public StatPanel statPanel;
	public LeaderboardPanel leaderboardPanel;

	public StatGUI(Player player) {
		super(player);

		this.menuPanel = new StatMenuPanel(this);
		this.statPanel = new StatPanel(this);
		this.leaderboardPanel = new LeaderboardPanel(this);
		setHomePanel(menuPanel);
	}
}