package dev.kyro.pitsim.settings.scoreboard;

import com.google.cloud.firestore.annotation.Exclude;
import dev.kyro.pitsim.controllers.ScoreboardManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;

import java.util.*;

public class ScoreboardData {
	@Exclude
	public PitPlayer pitPlayer;
	@Exclude
	public UUID uuid;

	private final List<String> priorityList = new ArrayList<>();
	private final Map<String, Boolean> statusMap = new HashMap<>();

	public void init(PitPlayer pitPlayer) {
		this.pitPlayer = pitPlayer;
		this.uuid = pitPlayer.player.getUniqueId();
		updateFields();
	}

	public boolean hasCustomScoreboardEnabled() {
		for(Map.Entry<String, Boolean> entry : getStatusMap().entrySet()) if(entry.getValue()) return true;
		return false;
	}

	public List<String> getPriorityList() {
		updateFields();
		return priorityList;
	}

	public Map<String, Boolean> getStatusMap() {
		updateFields();
		return statusMap;
	}

	public void updateFields() {
		for(String refName : priorityList) {
			ScoreboardOption scoreboardOption = ScoreboardManager.getScoreboardOption(refName);
			if(scoreboardOption != null) continue;
			priorityList.remove(refName);
			statusMap.remove(refName);
		}
		for(ScoreboardOption scoreboardOption : ScoreboardManager.scoreboardOptions) {
			if(!priorityList.contains(scoreboardOption.getRefName())) {
				priorityList.add(scoreboardOption.getRefName());
				statusMap.put(scoreboardOption.getRefName(), false);
			}
		}
	}
}
