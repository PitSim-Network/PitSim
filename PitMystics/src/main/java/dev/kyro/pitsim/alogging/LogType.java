package dev.kyro.pitsim.alogging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

// Enum needs to be mirrored in the proxy plugin
@SuppressWarnings("unused")
public enum LogType {
	TEST,

	//	Exploit prevention
	GEM_ITEM(LogFile.EXPLOIT_PREVENTION, LogFile.MAJOR),
	COMPLETE_JEWEL(LogFile.EXPLOIT_PREVENTION, LogFile.MAJOR),
	REPAIR_ITEM(LogFile.EXPLOIT_PREVENTION, LogFile.MAJOR),
	BOOSTER_USE(LogFile.EXPLOIT_PREVENTION, LogFile.MAJOR),

	//	Misc
	PLAYER_KILL(LogFile.COMBAT, LogFile.MAJOR),
	PLAYER_CHAT(LogFile.CHAT),
	ITEM_BROKEN(LogFile.COMBAT, LogFile.MAJOR),
	LIFE_LOST(LogFile.COMBAT),

	//	Guilds
	GUILD_CREATE(LogFile.GUILDS, LogFile.MAJOR),
	GUILD_CHAT(LogFile.GUILDS, LogFile.CHAT),

	//	Server TODO: actually implement
	SERVER_START,
	SERVER_STOP,
	;

	public final List<LogFile> logFiles;

	LogType(LogFile... logFiles) {
		this.logFiles = new ArrayList<>(Arrays.asList(logFiles));
		this.logFiles.add(LogFile.ALL);
		this.logFiles.add(LogFile.SERVER);
	}

	public enum LogFile {
		ALL("/main.log"),
		SERVER(null),
		MAJOR("/major.log"),
		EXPLOIT_PREVENTION("/exploit-prevention.log"),
		CHAT("/chat.log"),
		COMBAT("/combat.log"),
		GUILDS("/guilds.log");

		private final String fileName;

		LogFile(String fileName) {
			this.fileName = fileName;
		}

		public String getRelativePath(String serverName, Date date) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			String path = "/logs/" + dateFormat.format(date);
			if(this == SERVER) {
				path += "/servers/" + serverName + ".log";
			} else {
				path += fileName;
			}
			return path;
		}
	}
}
