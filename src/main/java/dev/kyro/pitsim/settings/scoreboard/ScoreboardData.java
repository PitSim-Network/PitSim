package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.pitsim.controllers.ScoreboardManager;

import java.util.*;

public class ScoreboardData {
	private final List<String> priorityList = new ArrayList<>();
	private final Map<String, Boolean> statusMap = new HashMap<>();

	public boolean hasCustomScoreboardEnabled() {
		for(Map.Entry<String, Boolean> entry : getStatusMap().entrySet()) if(entry.getValue()) return true;
		return false;
	}

	public List<String> getPriorityList() {
		addNewFields();
		return priorityList;
	}

	public Map<String, Boolean> getStatusMap() {
		addNewFields();
		return statusMap;
	}

	public void addNewFields() {
		for(ScoreboardOption scoreboardOption : ScoreboardManager.scoreboardOptions) {
			if(!priorityList.contains(scoreboardOption.refName)) {
				priorityList.add(scoreboardOption.refName);
				statusMap.put(scoreboardOption.refName, false);
			}
		}
	}
}
