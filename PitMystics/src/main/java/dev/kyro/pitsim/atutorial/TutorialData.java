package dev.kyro.pitsim.atutorial;

import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.UUID;

public class TutorialData {
	private final UUID uuid;

	public List<String> completedObjectives;

	public TutorialData(PitPlayer pitPlayer, FileConfiguration playerData) {
		this.uuid = pitPlayer.player.getUniqueId();

		this.completedObjectives = playerData.getStringList("tutorial.completed-objectives");
	}

	public void save() {
		APlayer aPlayer = APlayerData.getPlayerData(uuid);
		FileConfiguration playerData = aPlayer.playerData;

		playerData.set("tutorial.completed-objectives", completedObjectives);

		aPlayer.save();
	}
}
