package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.objects.PluginMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MessageEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private PluginMessage message;
	private String subChannel;

	public MessageEvent(PluginMessage message, String subChannel) {
		this.message = message;
		this.subChannel = subChannel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	public PluginMessage getMessage() {
		return message;
	}

	public String getSubChannel() {
		return subChannel;
	}

}
