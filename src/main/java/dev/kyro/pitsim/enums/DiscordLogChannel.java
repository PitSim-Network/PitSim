package dev.kyro.pitsim.enums;

// Enum needs to be mirrored in the pitsim plugin
public enum DiscordLogChannel {
	GOD_MENU_LOG_CHANNEL(1091234311291805837L),
	;

	private final Long channelID;

	DiscordLogChannel(Long channelID) {
		this.channelID = channelID;
	}

	public Long getChannelID() {
		return channelID;
	}
}
