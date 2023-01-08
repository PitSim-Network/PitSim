package dev.kyro.pitsim.inventories.stats;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.LeaderboardManager;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PlayerStats;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Objects;

public class LeaderboardPanel extends AGUIPanel {
	public StatGUI statGUI;

	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public PlayerStats stats;

	public LeaderboardPanel(AGUI gui) {
		super(gui);
		this.statGUI = (StatGUI) gui;
		this.stats = pitPlayer.stats;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);

		for(Leaderboard leaderboard : LeaderboardManager.leaderboards) {
			getInventory().setItem(leaderboard.slot, leaderboard.getDisplayStack(player.getUniqueId()));
		}

		updateInventory();
	}

	@Override
	public String getName() {
		return "Personal Statistics";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		for(Leaderboard leaderboard : LeaderboardManager.leaderboards) {
			if(leaderboard.slot == event.getSlot()) {

				if(Objects.equals(leaderboard.refName, pitPlayer.savedLeaderboardRef)) {
					AOutput.error(player, "&cYou already have that leaderboard selected!");
					Sounds.NO.play(player);
					return;
				}

				pitPlayer.savedLeaderboardRef = leaderboard.refName;

				Sounds.RENOWN_SHOP_PURCHASE.play(player);
				AOutput.send(player, "&e&lLEADERBOARD! &7Now displaying " + leaderboard.displayName);
				player.closeInventory();
				return;
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
