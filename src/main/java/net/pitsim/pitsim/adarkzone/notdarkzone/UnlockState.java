package net.pitsim.pitsim.adarkzone.notdarkzone;

import org.bukkit.ChatColor;

public enum UnlockState {
	LOCKED(ChatColor.RED, 14),
	NEXT_TO_UNLOCK(ChatColor.YELLOW, 4),
	UNLOCKED(ChatColor.GREEN, 5),
	;

	public ChatColor chatColor;
	public int data;

	UnlockState(ChatColor chatColor, int data) {
		this.chatColor = chatColor;
		this.data = data;
	}
}
