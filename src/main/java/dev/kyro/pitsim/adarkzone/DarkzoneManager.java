package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.adarkzone.sublevels.ZombieSubLevel;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class DarkzoneManager implements Listener {
	public static List<SubLevel> subLevels = new ArrayList<>();

	static {
		registerSubLevel(new ZombieSubLevel());
	}

	public static void registerSubLevel(SubLevel subLevel) {

	}

	public static SubLevel getSubLevel(Class<? extends SubLevel> clazz) {
		for(SubLevel subLevel : subLevels) if(subLevel.getClass() == clazz) return subLevel;
		return null;
	}
}
