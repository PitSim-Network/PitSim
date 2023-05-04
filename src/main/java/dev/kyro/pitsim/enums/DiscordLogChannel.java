package dev.kyro.pitsim.enums;

// needs to be mirrored in the proxy plugin
public enum DiscordLogChannel {
	GOD_MENU_LOG_CHANNEL(1091234311291805837L),
	TUTORIAL_LOG_CHANNEL(1102710878433980456L),
	;

	private final Long channelID;

	DiscordLogChannel(Long channelID) {
		this.channelID = channelID;
	}

	public Long getChannelID() {
		return channelID;
	}
}
