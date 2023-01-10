package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerIPPlaceholder implements APAPIPlaceholder {
	public static Map<UUID, ServerIP> customIPMap = new HashMap<>();

	static {
		customIPMap.put(UUID.fromString("3ede0ff9-f0b0-43de-8d8e-4f1e3027a3b5"), ServerIP.BOMP);
		customIPMap.put(UUID.fromString("6fcf9f87-05f4-4895-a803-6e1289da5557"), ServerIP.TOMCAT);
		customIPMap.put(UUID.fromString("ee660496-3cf1-458a-94fb-e11764c18663"), ServerIP.NOTFUTURE);
	}

	@Override
	public String getIdentifier() {
		return "server_ip";
	}

	@Override
	public String getValue(Player player) {
		if(!customIPMap.containsKey(player.getUniqueId())) return ServerIP.DEFAULT.getFullIP();
		return customIPMap.get(player.getUniqueId()).getFullIP();
	}

	public enum ServerIP {
		DEFAULT("mc"),
		BOMP("bomp"),
		TOMCAT("tomcat"),
		NOTFUTURE("future"),
		;

		private String subDomain;

		ServerIP(String subDomain) {
			this.subDomain = subDomain;
		}

		public String getFullIP() {
			return subDomain + ".pitsim.net";
		}
	}
}