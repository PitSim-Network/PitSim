package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.misc.AOutput;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MarketAsyncTask {

	private final MarketTask task;
	private final Runnable success;
	private final Runnable failure;
	private final MarketListing listing;
	private final Player player;

	public MarketAsyncTask(MarketTask task, MarketListing listing, Player executor, Runnable success, Runnable failure) {
		this.task = task;
		this.success = success;
		this.failure = failure;
		this.listing = listing;
		this.player = executor;
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

	public static Runnable getDefaultFail(Player player) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				AOutput.error(player, "&cThere was an issue with your request. Please try again in a moment.");
				player.closeInventory();
			}
		};
	}
}
