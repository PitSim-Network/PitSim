package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MarketAsyncTask {

	public static Map<UUID, MarketAsyncTask> taskMap = new HashMap<>();

	private final MarketTask task;
	private final Runnable success;
	private final Runnable failure;
	private final MarketListing listing;
	private final Player player;
	private final int parameter;
	private final BukkitTask timeout;

	public MarketAsyncTask(MarketTask task, MarketListing listing, Player executor, int parameter, Runnable success, Runnable failure) {
		this.task = task;
		this.success = success;
		this.failure = failure;
		this.listing = listing;
		this.player = executor;
		this.parameter = parameter;

		timeout = new BukkitRunnable() {
			@Override
			public void run() {
				failure.run();
			}
		}.runTaskLater(PitSim.INSTANCE, 20);


		if(taskMap.containsKey(executor.getUniqueId())) {
			respond(false);
		}

		taskMap.put(executor.getUniqueId(), this);

		PluginMessage message = new PluginMessage().writeString(task.proxyName);
		message.writeString(executor.getUniqueId().toString()).writeString(listing.marketUUID.toString());
		if(task == MarketTask.BIN_ITEM || task == MarketTask.PLACE_BID) {
			message.writeInt(parameter);
		}
		message.send();
	}

	public MarketTask getTask() {
		return task;
	}

	public Runnable getSuccess() {
		return success;
	}

	public Runnable getFailure() {
		return failure;
	}

	public MarketListing getListing() {
		return listing;
	}

	public Player getPlayer() {
		return player;
	}

	public int getParameter() {
		return parameter;
	}

	public void respond(boolean success) {
		timeout.cancel();
		taskMap.remove(player.getUniqueId());
		if(success) {
			this.success.run();
		} else {
			this.failure.run();
		}
	}

	public enum MarketTask {
		REMOVE_LISTING("REMOVE LISTING"),
		PLACE_BID("PLACE MARKET BID"),
		BIN_ITEM("LISTING BIN"),
		CLAIM_ITEM("CLAIM LISTING ITEM"),
		CLAIM_SOULS("CLAIM LISTING SOULS");

		public final String proxyName;
		MarketTask(String proxyName) {
			this.proxyName = proxyName;
		}
	}

	public static MarketAsyncTask getTask(UUID playerUUID) {
		return taskMap.get(playerUUID);
	}

	public static Runnable getDefaultFail(Player player) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				AOutput.error(player, "&cThere was an issue with your request. Please try again in a moment.");
				Sounds.NO.play(player);
				player.closeInventory();
			}
		};
	}
}
