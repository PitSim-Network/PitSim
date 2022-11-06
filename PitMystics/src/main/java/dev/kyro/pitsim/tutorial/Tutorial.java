package dev.kyro.pitsim.tutorial;

import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tutorial {
	private final UUID uuid;
	private final PitPlayer pitPlayer;
	private BossBar bossBar;

	public boolean isInObjective = false;

	public List<TutorialObjective> completedObjectives = new ArrayList<>();

	public Tutorial(PitPlayer pitPlayer, FileConfiguration playerData) {
		this.uuid = pitPlayer.player.getUniqueId();
		this.pitPlayer = pitPlayer;

		for(String string : playerData.getStringList("tutorial.completed-objectives")) {
			TutorialObjective objective = TutorialObjective.getByRefName(string);
			if(objective == null) continue;
			completedObjectives.add(objective);
		}

//		if(!isActive()) return;

		updateBossBar();


	}

	public void save() {
		APlayer aPlayer = APlayerData.getPlayerData(uuid);
		FileConfiguration playerData = aPlayer.playerData;

		List<String> rawData = new ArrayList<>();
		for(TutorialObjective completedObjective : completedObjectives) {
			rawData.add(completedObjective.refName);
		}
		System.out.println(rawData);
		playerData.set("tutorial.completed-objectives", rawData);

		aPlayer.save();
	}

	public void completeObjective(TutorialObjective objective, long delay) {
		if(isCompleted(objective)) return;
		isInObjective = true;

		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				completedObjectives.add(objective);
				updateBossBar();
				AOutput.send(pitPlayer.player, "&a&lTUTORIAL!&7 Completed objective: " + objective.display);
				Sounds.LEVEL_UP.play(pitPlayer.player);

				if(completedObjectives.size() == TutorialObjective.values().length) {
					Audience audience = PitSim.adventure.player(uuid);
					audience.hideBossBar(bossBar);
				}
				isInObjective = false;
			}
		};

		if(delay == 0) runnable.run();
		else runnable.runTaskLater(PitSim.INSTANCE, delay);
	}

	public boolean isCompleted(TutorialObjective objective) {
		return completedObjectives.contains(objective);
	}

	public void updateBossBar() {
		Audience audience = PitSim.adventure.player(uuid);
		TutorialObjective objective = getNextObjective();

		Component name = Component.text(ChatColor.translateAlternateColorCodes('&', "&a&lOBJECTIVE: &7Interact with NPCs &7("
				+ completedObjectives.size() + "/" +  TutorialObjective.values().length + ")"));
		float progress = ((float) completedObjectives.size()) / (float) TutorialObjective.values().length;

		bossBar = BossBar.bossBar(name, progress, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);

		audience.showBossBar(bossBar);
	}

	public TutorialObjective getNextObjective() {
		for(TutorialObjective value : TutorialObjective.values()) {
			if(!completedObjectives.contains(value)) return value;
		}
		return null;
	}

	public UUID getUUID() {
		return uuid;
	}

	public boolean isActive() {
		return pitPlayer.prestige <= 1 && completedObjectives.size() < TutorialObjective.values().length;
	}

	public void sendMessage(String text, long ticks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				AOutput.send(pitPlayer.player, text);
			}
		}.runTaskLater(PitSim.INSTANCE, ticks);
	}
}
