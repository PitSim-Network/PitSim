package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.ScoreboardManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.settings.SettingsGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardOptionsPanel extends AGUIPanel {
	public static ItemStack backItem;

	public SettingsGUI settingsGUI;
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

	public static List<Integer> scoreboardItemSlots = new ArrayList<>();
	public Map<Integer, ScoreboardOption> scoreboardMap = new HashMap<>();

	static {
		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&cBack")
				.setLore(new ALoreBuilder(
						"&7Click to go to the previous screen"
				))
				.getItemStack();

		for(int i = 9; i < 54; i++) {
			if(i % 9 == 0 || (i + 1) % 9 == 0) continue;
			scoreboardItemSlots.add(i);
			if(scoreboardItemSlots.size() == ScoreboardManager.scoreboardOptions.size()) break;
		}
	}

	public ScoreboardOptionsPanel(AGUI gui) {
		super(gui);
		this.settingsGUI = (SettingsGUI) gui;

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);
		getInventory().setItem(getRows() * 9 - 5, backItem);

		setInventory();
	}

	@Override
	public String getName() {
		return ChatColor.DARK_GREEN + "Custom Scoreboard Line";
	}

	@Override
	public int getRows() {
		return (ScoreboardManager.scoreboardOptions.size() - 1) / 7 + 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(scoreboardMap.containsKey(slot)) {
			ScoreboardOption scoreboardOption = scoreboardMap.get(slot);
			int position = scoreboardOption.getCurrentPosition(pitPlayer);

			List<String> priorityList = pitPlayer.scoreboardData.getPriorityList();
			if(event.getClick() == ClickType.LEFT) {
				if(position == 0) {
					Sounds.NO.play(player);
					return;
				}

				priorityList.remove(scoreboardOption.getRefName());
				priorityList.add(position - 1, scoreboardOption.getRefName());
				Sounds.SUCCESS.play(player);
			} else if(event.getClick() == ClickType.RIGHT) {
				if(position == ScoreboardManager.scoreboardOptions.size() - 1) {
					Sounds.NO.play(player);
					return;
				}

				priorityList.remove(scoreboardOption.getRefName());
				priorityList.add(position + 1, scoreboardOption.getRefName());
				Sounds.SUCCESS.play(player);
			} else if(event.getClick() == ClickType.MIDDLE || event.getClick() == ClickType.SHIFT_LEFT) {
				pitPlayer.scoreboardData.getStatusMap().put(scoreboardOption.getRefName(),
						!pitPlayer.scoreboardData.getStatusMap().get(scoreboardOption.getRefName()));
				Sounds.SUCCESS.play(player);
			}

			setInventory();
		} else if(slot == getRows() * 9 - 5) {
			openPreviousGUI();
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

	public void setInventory() {
		scoreboardMap.clear();
		int count = 0;
		for(String refName : pitPlayer.scoreboardData.getPriorityList()) {
			for(ScoreboardOption scoreboardOption : ScoreboardManager.scoreboardOptions) {
				if(!scoreboardOption.getRefName().equals(refName)) continue;
				ItemStack displayStack = scoreboardOption.getDisplayStack(count, pitPlayer.scoreboardData.getStatusMap().get(refName));
				getInventory().setItem(scoreboardItemSlots.get(count), displayStack);
				scoreboardMap.put(scoreboardItemSlots.get(count), scoreboardOption);
				break;
			}
			count++;
		}
		updateInventory();
	}
}
