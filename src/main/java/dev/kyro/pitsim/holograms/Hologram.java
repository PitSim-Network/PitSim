package dev.kyro.pitsim.holograms;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Hologram {

	public static final double MARGIN_DISTANCE = 0.3;
	public static final double VIEW_PROXIMITY = 50;

	protected final List<TextLine> textLines;
	//Location is the top text line
	protected Location spawnLocation;
	protected ViewMode viewMode;
	protected RefreshMode refreshMode;

	private final List<UUID> activeViewers;
	private final List<Player> permittedViewers;

	public Hologram(Location spawnLocation) {
		this(spawnLocation, ViewMode.ALL, RefreshMode.AUTOMATIC_MEDIUM);
	}
	public Hologram(Location spawnLocation, ViewMode viewMode, RefreshMode refreshMode) {
		this.spawnLocation = spawnLocation;
		this.viewMode = viewMode;
		this.refreshMode = refreshMode;

		this.textLines = new ArrayList<>();

		activeViewers = new ArrayList<>();
		permittedViewers = new ArrayList<>();

		HologramManager.registerHologram(this);

		new BukkitRunnable() {
			int iteration = 0;
			@Override
			public void run() {
				List<UUID> toRemove = new ArrayList<>();

				for(Player player : getPermittedViewers()) {
					if(player.getWorld() != spawnLocation.getWorld()) continue;
					double distance = player.getLocation().distance(spawnLocation);
					if(!activeViewers.contains(player.getUniqueId()) && distance <= VIEW_PROXIMITY) {
						addViewer(player);
					}
				}

				for(UUID activeViewer : new ArrayList<>(activeViewers)) {

					Player player = Bukkit.getPlayer(activeViewer);
					if(player == null) {
						toRemove.add(activeViewer);
						continue;
					}

					if(player.getWorld() != spawnLocation.getWorld() || !permittedViewers.contains(player)) {
						removeViewer(player);
						continue;
					}

					double distance = player.getLocation().distance(spawnLocation);
					if(distance > VIEW_PROXIMITY) removeViewer(player);
				}

				toRemove.forEach(activeViewers::remove);

				for(TextLine textLine : Hologram.this.textLines) {
					if(iteration % refreshMode.iterations == 0) updateLine(textLine);
				}
				iteration++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 5);
	}

	public abstract List<String> getStrings(Player player);

	protected Location getSpawnLocation(TextLine textLine) {
		int index = textLines.indexOf(textLine);

		if(index == -1) throw new RuntimeException("Text line not found in hologram");

		return spawnLocation.clone().subtract(0, MARGIN_DISTANCE * index, 0);
	}

	private void addViewer(Player player) {
		activeViewers.add(player.getUniqueId());

		if(textLines.isEmpty()) getStrings(player).forEach(text -> textLines.add(new TextLine(this)));

		textLines.forEach(textLine -> textLine.displayLine(this, player));
	}

	private void removeViewer(Player player) {
		activeViewers.remove(player.getUniqueId());
		textLines.forEach(textLine -> textLine.removeLine(player));
	}

	public void updateLine(int index) {
		TextLine textLine = textLines.get(index);
		updateLine(textLine);
	}

	public void updateHologram() {
		textLines.forEach(this::updateLine);
	}

	public void updateLine(TextLine textLine) {
		if(textLine == null) throw new RuntimeException("Text line not found in hologram");

		for(UUID activeViewer : activeViewers) {
			Player player = Bukkit.getPlayer(activeViewer);
			if(player == null) continue;

			textLine.updateLine(this, player);
		}
	}

	public void updateHologram(Player player) {
		textLines.forEach(textLine -> textLine.updateLine(this, player));
	}

	public List<Player> getPermittedViewers() {
		return permittedViewers;
	}

	public void addPermittedViewer(Player player) {
		if(permittedViewers.contains(player)) return;
		permittedViewers.add(player);

		if(player.getWorld() != spawnLocation.getWorld()) return;
		double distance = player.getLocation().distance(spawnLocation);
		if(!activeViewers.contains(player.getUniqueId()) && distance <= VIEW_PROXIMITY) {
			addViewer(player);
		}
	}

	public void removePermittedViewer(Player player) {
		permittedViewers.remove(player);

		if(activeViewers.contains(player.getUniqueId())) removeViewer(player);
	}

	public void setPermittedViewers(List<Player> permittedViewers) {
		this.permittedViewers.clear();
		this.permittedViewers.addAll(permittedViewers);
	}
}
