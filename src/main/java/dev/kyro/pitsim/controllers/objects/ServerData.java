package dev.kyro.pitsim.controllers.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerData {

	public static Map<Integer, ServerData> servers = new HashMap<>();

	public int index;

	private final List<String> playerStrings;

	private boolean isRunning;
	private int playerCount;
	private List<String> stringData;

	public ServerData(int index, List<String> initialStringData, List<Integer> intData, List<Boolean> booleanData) {

		this.index = index;

		playerCount = intData.get(index);
		isRunning = booleanData.get(index);
		stringData = new ArrayList<>(initialStringData);

		List<List<String>> indexData = new ArrayList<>();

		for(Integer intDatum : intData) {
			if(intDatum == 0) {
				indexData.add(new ArrayList<>());
				continue;
			}


			List<String> data = stringData.subList(0, intDatum);
			indexData.add(new ArrayList<>(data));
			stringData.subList(0, intDatum).clear();
		}
		playerStrings = indexData.get(index);

		servers.put(index, this);
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
