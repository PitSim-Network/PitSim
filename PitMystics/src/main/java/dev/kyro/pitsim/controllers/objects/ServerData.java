package dev.kyro.pitsim.controllers.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerData {

	public static Map<Integer, ServerData> servers = new HashMap<>();

	public int index;

	private final List<String> playerStrings;

	private boolean isRunning;
	private int playerCount;

	public ServerData(int index, List<String> stringData, List<Integer> intData, List<Boolean> booleanData) {

		this.index = index;

		playerCount = intData.get(index);
		isRunning = booleanData.get(index);

		System.out.println(stringData);

		for(int i = 0; i < index; i++) {
			int otherIndex = intData.get(i);

			if(otherIndex > 0) {
				stringData.subList(0, otherIndex - 1).clear();
			}
		}

		System.out.println(stringData);

		for(int i = index; i < intData.size(); i++) {
			int otherIndex = intData.get(i);

			if(otherIndex > 0) {
				stringData.subList(otherIndex, stringData.size() - 1).clear();
			}
		}

		System.out.println(stringData);

		playerStrings = stringData.subList(0, playerCount);

		servers.put(index, this);

		System.out.println("e");
		for(ServerData value : servers.values()) {
			System.out.println(value.playerStrings);
		}
	}


	public ServerData(int index, int playerCount, boolean isRunning, List<String> playerStrings) {
		this.index = index;
		this.playerCount = playerCount;
		this.isRunning = isRunning;
		this.playerStrings = playerStrings;

		servers.put(index, this);
	}

	public List<String> getPlayerStrings() {
		return playerStrings;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public static ServerData getServerData(int index) {
		return servers.get(index);
	}

	public static int getServerCount() {
		return servers.size();
	}

}
