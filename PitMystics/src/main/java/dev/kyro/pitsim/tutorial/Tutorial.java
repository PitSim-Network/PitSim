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
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Tutorial {
	private final UUID uuid;
	private final PitPlayer pitPlayer;
	private BossBar bossBar;
	private BukkitTask particleRunnable;

	public boolean isInObjective = false;

	public boolean hasStartedTutorial = false;
	public List<TutorialObjective> completedObjectives = new ArrayList<>();

	public String refName;
	public boolean ordered;

	public abstract void sendStartMessages();

	public abstract void sendEndMessages();

	public abstract boolean canBeActive();

	public abstract String getBossBarDisplay(int completedObjectives);

	public Tutorial(PitPlayer pitPlayer, FileConfiguration playerData, String refName, boolean ordered) {
		this.uuid = pitPlayer.player.getUniqueId();
		this.pitPlayer = pitPlayer;
		this.refName = refName;
		this.ordered = ordered;

		this.hasStartedTutorial = playerData.getBoolean("tutorial." + refName + ".has-started");
		for(String string : playerData.getStringList("tutorial." + refName + ".completed-objectives")) {
			TutorialObjective objective = TutorialObjective.getByRefName(string);
			if(objective == null) continue;
			completedObjectives.add(objective);
		}

		if(!isActive()) return;

		if(!hasStartedTutorial) {
			isInObjective = true;
			hasStartedTutorial = true;

			sendStartMessages();

			new BukkitRunnable() {
				@Override
				public void run() {
					isInObjective = false;
					updateBossBar();
					startRunnable();
					Sounds.TUTORIAL_MESSAGE.play(pitPlayer.player);
				}
			}.runTaskLater(PitSim.INSTANCE, 20 * 4);
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					updateBossBar();
					startRunnable();
				}
			}.runTaskLater(PitSim.INSTANCE, 20);
		}
	}

	public void save() {
		APlayer aPlayer = APlayerData.getPlayerData(uuid);
		FileConfiguration playerData = aPlayer.playerData;

		List<String> rawData = new ArrayList<>();
		for(TutorialObjective completedObjective : completedObjectives) {
			rawData.add(completedObjective.refName);
		}
		playerData.set("tutorial." + refName + ".has-started", hasStartedTutorial);
		playerData.set("tutorial." + refName + ".completed-objectives", rawData);

		aPlayer.save();
	}

	public void completeObjective(TutorialObjective objective, long delay) {
		if(isCompleted(objective)) return;

		if(ordered && objective != getNextObjective()) return;

		isInObjective = true;

		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				completedObjectives.add(objective);
				updateBossBar();
				AOutput.send(pitPlayer.player, "&a&lTUTORIAL!&7 Completed objective: " + objective.display);
				Sounds.LEVEL_UP.play(pitPlayer.player);

				if(completedObjectives.size() == TutorialObjective.getSize(Tutorial.this.getClass())) {
					if(particleRunnable != null) particleRunnable.cancel();

					new BukkitRunnable() {
						@Override
						public void run() {
							AOutput.send(pitPlayer.player, "&a&lTUTORIAL COMPLETED!");
							Sounds.LEVEL_UP.play(pitPlayer.player);
						}
					}.runTaskLater(PitSim.INSTANCE, 30);

					sendEndMessages();

					new BukkitRunnable() {
						@Override
						public void run() {
							Audience audience = PitSim.adventure.player(uuid);
							audience.hideBossBar(bossBar);
						}
					}.runTaskLater(PitSim.INSTANCE, 60);
				}
				isInObjective = false;
			}
		};

		if(delay == 0) runnable.run();
		else runnable.runTaskLater(PitSim.INSTANCE, delay);
	}

	public TutorialObjective getNextObjective() {
		if(!ordered) return null;

		return TutorialObjective.getObjective(completedObjectives.size(), getClass());
	}

	public boolean isCompleted(TutorialObjective objective) {
		return completedObjectives.contains(objective);
	}

	public void updateBossBar() {
		Audience audience = PitSim.adventure.player(uuid);
		audience.hideBossBar(bossBar);

		Component name = Component.text(getBossBarDisplay(completedObjectives.size()));
		float progress = ((float) completedObjectives.size()) / (float) TutorialObjective.getSize(getClass());

		bossBar = BossBar.bossBar(name, progress, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);

		audience.showBossBar(bossBar);
	}

	public UUID getUUID() {
		return uuid;
	}

	public boolean isActive() {
		return canBeActive() && completedObjectives.size() < TutorialObjective.getSize(getClass());
	}

	public void sendMessage(String text, long ticks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Sounds.TUTORIAL_MESSAGE.play(pitPlayer.player);
				AOutput.send(pitPlayer.player, text);
			}
		}.runTaskLater(PitSim.INSTANCE, ticks);
	}

	private void startRunnable() {
		particleRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(!pitPlayer.player.isOnline()) {
					cancel();
					return;
				}
				List<TutorialObjective> tutorialObjectives = TutorialObjective.getObjectives(Tutorial.this.getClass());
				tutorialObjectives.removeAll(completedObjectives);
				for(TutorialObjective objective : tutorialObjectives) {
					if(objective.particleDisplayHeight < 2 && Math.random() < 0.4) continue;
					Location location = objective.getParticleLocation(pitPlayer.player.getWorld());
					double random = 1.4;
					location.add(Math.random() * random - random / 2.0, Math.random() * objective.particleDisplayHeight,
							Math.random() * random - random / 2.0);
					pitPlayer.player.playEffect(location, Effect.HAPPY_VILLAGER, 1);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
	}
}
