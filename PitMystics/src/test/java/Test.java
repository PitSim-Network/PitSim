public class Test {
	public static void main(String[] args) {

		int totalPlayers = 17;
		int totalServers = 4;
		for(int i = 1 + (totalPlayers + 3) / 10; i < totalServers; i++) {
			System.out.println("Shutting down server: " + (i + 1));
		}
	}
}
