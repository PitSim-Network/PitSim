package net.pitsim.pitsim.controllers.objects;

import java.util.*;

public class ServerData {

	public static Map<Integer, ServerData> overworldServers = new HashMap<>();
	public static Map<Integer, ServerData> darkzoneServers = new HashMap<>();

	public int index;

	private final Map<String, String> playerStrings = new HashMap<>();
	private boolean darkzone;

	private boolean isRunning;
	private int playerCount;
	private List<String> stringData;

	public ServerData(int index, boolean darkzone, List<String> initialStringData, List<Integer> intData, List<Boolean> booleanData) {

		this.index = index;
		this.darkzone = darkzone;

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
		List<String> data = indexData.get(index);
		for(String datum : data) {
			String[] split = datum.split(":");
			playerStrings.put(split[0], split[1]);
		}

		if(!darkzone) overworldServers.put(index, this);
		else darkzoneServers.put(index, this);
	}

	public Map<String, String> getPlayers() {
		return playerStrings;
	}

	public List<String> getPlayerStrings() {
		return new ArrayList<>(playerStrings.values());
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public static List<ServerData> getAllServerData() {
		List<ServerData> completeList = new ArrayList<>(overworldServers.values());
		completeList.addAll(darkzoneServers.values());
		return completeList;
	}

	public static ServerData getOverworldServerData(int index) {
		return overworldServers.get(index);
	}

	public static ServerData getDarkzoneServerData(int index) {
		return darkzoneServers.get(index);
	}

	public static int getAllServerCount() {
		return overworldServers.size() + darkzoneServers.size();
	}

	public static int getOverworldServerCount() {
		return overworldServers.size();
	}

	public static int getDarkzoneServerCount() {
		return darkzoneServers.size();
	}

	public boolean isDarkzone() {
		return darkzone;
	}

}
