package dev.kyro.pitsim.ahelp;

import com.google.cloud.dialogflow.cx.v3.*;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HelpManager implements Listener {
	public static final String PROJECT_ID = "pitsim-network";
	public static final String AGENT_ID = "deed93e6-7edc-42e7-ac0f-9a472df87c56";
	public static final String LOCATION = "us-east1";
	private static final Map<Player, HelperAgent> helpClientMap = new HashMap<>();

	private static SessionsSettings sessionsSettings;
	private static IntentsSettings intentsSettings;

	public HelpManager() {
		setupSettings();
		setupEnv();
		updateIntents();
	}

	private static void setupSettings() {
		try {
			sessionsSettings = SessionsSettings.newBuilder()
					.setEndpoint(LOCATION + "-dialogflow.googleapis.com:443")
					.build();
		} catch(IOException exception) {
			throw new RuntimeException(exception);
		}
		try {
			intentsSettings = IntentsSettings.newBuilder()
					.setEndpoint(LOCATION + "-dialogflow.googleapis.com:443")
					.build();
		} catch(IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private static void setupEnv() {
		Map<String, String> newEnvFields = new HashMap<>();
		newEnvFields.put("GOOGLE_APPLICATION_CREDENTIALS", PitSim.INSTANCE.getDataFolder() + "/google-key.json");
		try {
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
			env.putAll(newEnvFields);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
					.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.putAll(newEnvFields);
		} catch (NoSuchFieldException | ClassNotFoundException exception) {
			try {
				Class[] classes = Collections.class.getDeclaredClasses();
				Map<String, String> env = System.getenv();
				for (Class cl : classes) {
					if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
						Field field = cl.getDeclaredField("m");
						field.setAccessible(true);
						Object obj = field.get(env);
						Map<String, String> map = (Map<String, String>) obj;
						map.clear();
						map.putAll(newEnvFields);
					}
				}
			} catch(Exception exception1) {
				exception.printStackTrace();
			}
		} catch(IllegalAccessException exception) {
			exception.printStackTrace();
		}
	}

	private static void updateIntents() {
		try(IntentsClient client = IntentsClient.create(intentsSettings)) {
			AgentName parent = AgentName.of(PROJECT_ID, LOCATION, AGENT_ID);
			for(Intent intent : client.listIntents(parent).iterateAll()) {
				System.out.println("intent: " + intent.getDisplayName());
				System.out.println("intent: " + intent.getName());
			}
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}

	public static HelperAgent getAgent(Player player) {
//		if(player == null) throw new RuntimeException();
		if(helpClientMap.containsKey(player)) {
			HelperAgent helperAgent = helpClientMap.get(player);
			if(helperAgent.isAlive()) return helperAgent;
		}
		HelperAgent helperAgent = new HelperAgent(player);
		helpClientMap.put(player, helperAgent);
		return helperAgent;
	}

	public static class HelperAgent {
		private final Player player;
		private final UUID sessionID = UUID.randomUUID();
		private final long startTime = System.currentTimeMillis();

		public HelperAgent(Player player) {
			this.player = player;
		}

		public void detectIntent(String text) {
			try(SessionsClient client = SessionsClient.create(sessionsSettings)) {
				SessionName session = SessionName.ofProjectLocationAgentSessionName(PROJECT_ID, LOCATION, AGENT_ID, sessionID.toString());
				TextInput.Builder textInput = TextInput.newBuilder().setText(text);
				QueryInput queryInput = QueryInput.newBuilder().setText(textInput).setLanguageCode("en-US").build();

				DetectIntentRequest request = DetectIntentRequest.newBuilder()
						.setSession(session.toString())
						.setQueryInput(queryInput)
						.build();

				DetectIntentResponse response = client.detectIntent(request);
				QueryResult queryResult = response.getQueryResult();

				AOutput.send(player, "&7====================");
				AOutput.send(player, "&7text: " + queryResult.getText());
				AOutput.send(player, "&7intent: " + queryResult.getIntent().getDisplayName());
				AOutput.send(player, "&7confidence: " + queryResult.getIntentDetectionConfidence());
				for(ResponseMessage responseMessage : queryResult.getResponseMessagesList()) {
					AOutput.send(player, "&7response: " + responseMessage.getText());
				}
				AOutput.send(player, "&7====================");
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void remove() {
			helpClientMap.remove(player);
		}

		public boolean isAlive() {
			return System.currentTimeMillis() - startTime < 1000 * 60 * 19;
		}

		public Player getPlayer() {
			return player;
		}

		public UUID getSessionID() {
			return sessionID;
		}

		public long getStartTime() {
			return startTime;
		}
	}
}
