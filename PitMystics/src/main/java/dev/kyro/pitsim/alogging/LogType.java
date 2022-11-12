package dev.kyro.pitsim.alogging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Enum is mirrored in the proxy plugin
public enum LogType {
	TEST,
	PLAYER_KILL,

//	Guilds
	GUILD_CREATE,

//	Server
	SERVER_START,
	SERVER_STOP;

	private final List<String> logFiles;

	LogType(String... logFiles) {
		this.logFiles = new ArrayList<>(Arrays.asList(logFiles));
	}
}
