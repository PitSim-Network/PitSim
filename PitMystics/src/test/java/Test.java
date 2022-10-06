public class Test {
	public static void main(String[] args) {

		int START_THRESHOLD = 10;
		int STOP_THRESHOLD = 6;

		int players = 7;
		int totalServers = 4;

		for(int i = 0; i < Math.min(players / 10 + 1, totalServers); i++) {
//			if server is on:
//				if server is shutting down: cancel
//				continue
//			end if
			System.out.println("Turning on server: " + (i + 1));
		}

		for(int i = 1 + (players + (START_THRESHOLD - STOP_THRESHOLD - 1)) / 10; i < totalServers; i++) {
//			if server is already shut down or is shutting down: continue
			System.out.println("Shutting down server: " + (i + 1));
		}
	}
}