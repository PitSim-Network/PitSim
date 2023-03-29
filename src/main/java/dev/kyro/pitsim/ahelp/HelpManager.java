package dev.kyro.pitsim.ahelp;

import com.google.cloud.dialogflow.cx.v3.*;
import com.google.protobuf.FieldMask;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DiscordManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class HelpManager implements Listener {
	public static List<HelpIntent> helpIntents = new ArrayList<>();
	public static List<HelpPage> helpPages = new ArrayList<>();

	public static final String PROJECT_ID = "pitsim-network";
	public static final String AGENT_ID = "deed93e6-7edc-42e7-ac0f-9a472df87c56";
	public static final String LOCATION_ID = "us-east1";
	public static final String FLOW_ID = "00000000-0000-0000-0000-000000000000";
	private static final Map<Player, HelperAgent> helpClientMap = new HashMap<>();
	public static final String TABLE_NAME = "HelpRequests";

	private static SessionsSettings sessionsSettings;
	private static PagesSettings pagesSettings;
	private static IntentsSettings intentsSettings;

	//	TODO: Replace with database code

	public HelpManager() {
		setupSettings();
		setupEnv();
	}

	public static void registerIntentsAndPages() {
//		Intents
		for(PitPerk pitPerk : PerkManager.pitPerks) registerIntent(pitPerk);
		for(Killstreak killstreak : PerkManager.killstreaks) registerIntent(killstreak);
		for(Megastreak megastreak : PerkManager.megastreaks) registerIntent(megastreak);
		for(RenownUpgrade upgrade : UpgradeManager.upgrades) registerIntent(upgrade);
		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) registerIntent(pitEnchant);

		helpIntents.add(new HelpIntent("NO_INTENT", HelpPageIdentifier.MAIN_PAGE)
				.setTrainingPhrases(
						"?",
						"huh?",
						"what?",
						"how are you?",
						"where are you?",
						"what are you doing?"
				));

		helpIntents.add(new HelpIntent("WHAT_IS_PIT", HelpPageIdentifier.MAIN_PAGE)
				.setReply("The Hypixel Pit is a glorious minigame run by our one true lord and saviour &cMinikloon")
				.setTrainingPhrases(
						"what is pit?",
						"what is the hypixel pit?"
				));

		helpIntents.add(new HelpIntent("WHO_IS_HARRY", HelpPageIdentifier.MAIN_PAGE)
				.setReply("Random british kid")
				.setTrainingPhrases(
						"who is harry?",
						"who is hairy?",
						"who is harrison?"
				));

		helpIntents.add(new HelpIntent("WHAT_IS_PITSANDBOX", HelpPageIdentifier.MAIN_PAGE)
				.setReply("A shitty server ran by a shittier owner")
				.setTrainingPhrases(
						"what is shit sandbox?",
						"what is pit sandbox?",
						"what is harry's network?",
						"what is hairy's network?"
				));

		helpIntents.add(new HelpIntent("WHEN_BETTERPIT_OPEN", HelpPageIdentifier.MAIN_PAGE)
				.setReply("When is the next blue moon?")
				.setTrainingPhrases(
						"when will betterpit open?",
						"when will betterpit go back up?",
						"when can i join betterpit again?"
				));

		helpIntents.add(new HelpIntent("WHO_IS_ANDREW_TATE", HelpPageIdentifier.MAIN_PAGE)
				.setReply("A MAN OF &4&l&oLIMITLESS POWER&7 JUST BELOW GOD. AN &4&l&oALPHA DOG&7 WHO &4&l&oRULES " +
						"OVER ALL MEN&7 AND IS MORE CAPABLE THEN YOU EVER WILL BE")
				.setTrainingPhrases(
						"who is andrew tate?",
						"who is tate?"
				));

		helpIntents.add(new HelpIntent("WHO_IS_KYRO", HelpPageIdentifier.MAIN_PAGE)
				.setReply("&7The best &6&lPit&e&lSim&7 &9Developer &7(Kyro#2820)")
				.setTrainingPhrases(
						"who is kyro?",
						"who is kyrokrypt?"
				));

		helpIntents.add(new HelpIntent("WHO_IS_WIJI", HelpPageIdentifier.MAIN_PAGE)
				.setReply("&7The second-best &6&lPit&e&lSim&7 &9Developer &7(wiji#0001)")
				.setTrainingPhrases(
						"who is wiji?"
				));

		helpIntents.add(new HelpIntent("WHO_IS_FINN", HelpPageIdentifier.MAIN_PAGE)
				.setReply("&7Single man, ladies hmu Finn#6575, btw im 6'2\", international DJ, worldwide traveler, inactive pitsim developer")
				.setTrainingPhrases(
						"who is finn?"
				));

//		Pages
		helpPages.add(new HelpPage(HelpPageIdentifier.MAIN_PAGE));
	}

	private static void registerIntent(Summarizable summarizable) {
		if(summarizable == null || summarizable.getSummary() == null) return;
		helpIntents.add(new HelpIntent(summarizable.getIdentifier(), HelpPageIdentifier.MAIN_PAGE)
				.setReply(summarizable.getSummary())
				.setTrainingPhrases(summarizable.getTrainingPhrases()));
	}

	public static void updateIntentsAndPages() {
		try(PagesClient pagesClient = PagesClient.create(pagesSettings);
			IntentsClient intentsClient = IntentsClient.create(intentsSettings)) {

			AgentName agentParent = AgentName.of(PROJECT_ID, LOCATION_ID, AGENT_ID);
			FlowName flowParent = FlowName.of(PROJECT_ID, LOCATION_ID, AGENT_ID, FLOW_ID);

			List<Page> pages = new ArrayList<>();
			pagesClient.listPages(flowParent).iterateAll().forEach(pages::add);
			List<Intent> intents = new ArrayList<>();
			intentsClient.listIntents(agentParent).iterateAll().forEach(intents::add);

//			if(true) {
//				for(Intent intent : intents) {
//					try {
//						intentsClient.deleteIntent(intent.getName());
//						AOutput.log("deleting intent: " + intent.getDisplayName());
//						sleep(1000);
//					} catch(Exception e) {
//						e.printStackTrace();
//					}
//				}
//				return;
//			}

			Map<HelpPage, Page> pageMap = new LinkedHashMap<>();
			Map<HelpIntent, Intent> intentMap = new LinkedHashMap<>();
			for(HelpPage helpPage : helpPages) {
				Page page = null;
				for(Page testPage : pages) {
					if(!testPage.getDisplayName().equals(helpPage.getIdentifier())) continue;
					page = testPage;
					break;
				}
				pageMap.put(helpPage, page);
//				if(page != null) {
//					DeletePageRequest deletePageRequest = DeletePageRequest.newBuilder()
//							.setName(page.getName())
//							.setForce(true)
//							.build();
//					pagesClient.deletePage(deletePageRequest);
//					pageMap.put(helpPage, null);
//				}
			}
			for(HelpIntent helpIntent : helpIntents) {
				Intent intent = null;
				for(Intent testIntent : intents) {
					if(!testIntent.getDisplayName().equals(helpIntent.getIdentifier())) continue;
					intent = testIntent;
					break;
				}
				intentMap.put(helpIntent, intent);
			}

			for(Map.Entry<HelpPage, Page> entry : pageMap.entrySet()) {
				HelpPage helpPage = entry.getKey();
				Page page = entry.getValue();
				if(page != null) {
						page = Page.newBuilder()
								.setName(page.getName())
								.setEntryFulfillment(createFulfillment(helpPage.getEntryFulfillment()))
								.build();
						FieldMask fieldMask = FieldMask.newBuilder().addPaths("entry_fulfillment").build();
						page = pagesClient.updatePage(page, fieldMask);
						pageMap.put(helpPage, page);
						AOutput.log("Updated page: " + page.getDisplayName());
					continue;
				}

				page = Page.newBuilder()
						.setDisplayName(helpPage.getIdentifier())
//							.setEntryFulfillment(createFulfillment(helpPage.getEntryFulfillment()))
						.build();
				Page createdPage = pagesClient.createPage(flowParent, page);

				helpPage.setFullName(createdPage.getName());
				pageMap.put(helpPage, createdPage);
				AOutput.log("Created page: " + entry.getKey().getIdentifier());
				sleep(1000);
			}

			for(Map.Entry<HelpIntent, Intent> entry : intentMap.entrySet()) {
				HelpIntent helpIntent = entry.getKey();
				Intent intent = entry.getValue();
				if(intent != null) {
					intent = Intent.newBuilder()
							.setName(intent.getName())
							.addAllTrainingPhrases(createTrainingPhrases(helpIntent.getTrainingPhrases()))
							.build();
					FieldMask fieldMask = FieldMask.newBuilder().addPaths("training_phrases").build();
					intent = intentsClient.updateIntent(intent, fieldMask);
					intentMap.put(helpIntent, intent);
					AOutput.log("Updated intent: " + intent.getDisplayName());
					sleep(1000);
					continue;
				}

				intent = Intent.newBuilder()
						.setDisplayName(helpIntent.getIdentifier())
						.addAllTrainingPhrases(createTrainingPhrases(helpIntent.getTrainingPhrases()))
						.build();
				intent = intentsClient.createIntent(agentParent, intent);
				intentMap.put(helpIntent, intent);
				AOutput.log("Created intent: " + helpIntent.getIdentifier());
				sleep(1000);
			}

			for(Map.Entry<HelpPage, Page> entry : pageMap.entrySet()) {
				HelpPage helpPage = entry.getKey();
				Page page = entry.getValue();

				List<TransitionRoute> transitionRoutes = new ArrayList<>();
				loop:
				for(Map.Entry<HelpIntent, Intent> intentEntry : intentMap.entrySet()) {
					HelpIntent helpIntent = intentEntry.getKey();
					Intent intent = intentEntry.getValue();
					if(!helpIntent.getParentPage().getIdentifier().equals(helpPage.getIdentifier())) continue;

					String transitionName = intent.getDisplayName() + "_TO_";
					if(helpIntent.getChildPage() != null) {
						transitionName += helpIntent.getChildPage().getIdentifier();
					} else {
						transitionName += "END_SESSION";
					}

					for(TransitionRoute transitionRoute : page.getTransitionRoutesList()) {
						if(!transitionRoute.getName().equals(transitionName)) continue;
						continue loop;
					}

					TransitionRoute.Builder builder = TransitionRoute.newBuilder()
							.setName(transitionName)
							.setIntent(intent.getName());
					if(helpIntent.getChildPage() != null) {
						builder.setTargetPage(getPage(pageMap, helpIntent.getChildPage()).getName());
					} else {
						builder.setTriggerFulfillment(createFulfillment(null));
					}

					transitionRoutes.add(builder.build());
					AOutput.log("Added intent " + intent.getDisplayName() + " to page " + page.getDisplayName());
					sleep(1000);
				}

				if(transitionRoutes.isEmpty()) continue;

				page = page.toBuilder()
						.addAllTransitionRoutes(transitionRoutes)
						.build();

				UpdatePageRequest updatePageRequest = UpdatePageRequest.newBuilder()
						.setPage(page)
						.setUpdateMask(FieldMask.newBuilder().addPaths("transition_routes"))
						.build();

				page = pagesClient.updatePage(updatePageRequest);
				pageMap.put(helpPage, page);
				AOutput.log("Updated transition routes for page: " + page.getDisplayName());
				sleep(1000);
			}
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}

	private static void setupSettings() {
		try {
			sessionsSettings = SessionsSettings.newBuilder()
					.setEndpoint(LOCATION_ID + "-dialogflow.googleapis.com:443")
					.build();
			pagesSettings = PagesSettings.newBuilder()
					.setEndpoint(LOCATION_ID + "-dialogflow.googleapis.com:443")
					.build();
			intentsSettings = IntentsSettings.newBuilder()
					.setEndpoint(LOCATION_ID + "-dialogflow.googleapis.com:443")
					.build();
		} catch(IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
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

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException exception) {
			exception.printStackTrace();
		}
	}

	public static Fulfillment createFulfillment(String text) {
		if(text == null) return Fulfillment.newBuilder().build();
		return Fulfillment.newBuilder()
				.addMessages(ResponseMessage.newBuilder()
						.setText(ResponseMessage.Text.newBuilder()
								.addText(text)
								.build())
						.build())
				.build();
	}

	public static List<Intent.TrainingPhrase> createTrainingPhrases(List<String> phrases) {
		List<Intent.TrainingPhrase> trainingPhrases = new ArrayList<>();
		for(String phrase : phrases) {
			trainingPhrases.add(Intent.TrainingPhrase.newBuilder()
					.addParts(Intent.TrainingPhrase.Part.newBuilder()
							.setText(phrase)
							.build())
					.setRepeatCount(1)
					.build());
		}
		return trainingPhrases;
	}

	public static Page getPage(Map<HelpPage, Page> pageMap, HelpPageIdentifier identifier) {
		for(HelpPage page : pageMap.keySet()) {
			if(page.getIdentifier().equals(identifier.getIdentifier())) return pageMap.get(page);
		}
		return null;
	}

	public static StoredRequest getStoredRequest(String query) {
		Connection connection = getConnection();
		String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE query = ?";

		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, query);
			ResultSet resultSet = statement.executeQuery();
			if(resultSet.next()) {
				String intent = resultSet.getString("intent");
				return new StoredRequest(query, intent);
			}
		} catch(SQLException exception) {
			exception.printStackTrace();
		}

		try {
			connection.close();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public static void writeStoredRequest(StoredRequest request) {
		Connection connection = getConnection();
		String sqlQuery = "INSERT INTO " + TABLE_NAME + " (query, intent) VALUES (?, ?)";

		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, request.query);
			statement.setString(2, request.intent);
			statement.executeUpdate();
		} catch(SQLException exception) {
			exception.printStackTrace();
		}

		try {
			connection.close();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void clearStoredData() {
		Connection connection = getConnection();
		String sqlQuery = "DELETE FROM " + TABLE_NAME;

		try {
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.executeUpdate();
		} catch(SQLException exception) {
			exception.printStackTrace();
		}

		try {
			connection.close();
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static HelperAgent getAgent(Player player) {
		if(player == null) throw new RuntimeException();
		if(helpClientMap.containsKey(player)) {
			HelperAgent helperAgent = helpClientMap.get(player);
			if(helperAgent.isAlive()) return helperAgent;
		}
		HelperAgent helperAgent = new HelperAgent(player);
		helpClientMap.put(player, helperAgent);
		return helperAgent;
	}

	public static HelpIntent getHelpIntent(String intentName) {
		for(HelpIntent intent : helpIntents) if(intent.getIdentifier().equals(intentName)) return intent;
		return null;
	}

	public static class HelperAgent {
		private final Player player;
		private final UUID sessionID = UUID.randomUUID();
		private final long startTime = System.currentTimeMillis();

		private HelpIntent lastIntent;
		private long lastIntentUpdate;

		private boolean isReady = false;

		public HelperAgent(Player player) {
			this.player = player;
		}

		public String detectIntent(String text) {
			if(!isReady) {
				isReady = true;
				detectIntent("init");
			}
			try(SessionsClient client = SessionsClient.create(sessionsSettings)) {
				SessionName session = SessionName.ofProjectLocationAgentSessionName(PROJECT_ID, LOCATION_ID, AGENT_ID, sessionID.toString());
				TextInput.Builder textInput = TextInput.newBuilder().setText(text);
				QueryInput queryInput = QueryInput.newBuilder().setText(textInput).setLanguageCode("en-US").build();

				DetectIntentRequest request = DetectIntentRequest.newBuilder()
						.setSession(session.toString())
						.setQueryInput(queryInput)
						.build();

				DetectIntentResponse response = client.detectIntent(request);
				QueryResult queryResult = response.getQueryResult();
				return queryResult.getIntent().getDisplayName();
			} catch(IOException exception) {
				throw new RuntimeException(exception);
			}
		}

		public void executeIntent(String intent) {
			if(intent.equals("DEFAULT_INIT")) return;
			HelpIntent helpIntent = getHelpIntent(intent);
			if(helpIntent == null) {
				AOutput.error(player, "&9&lAI!&7 Sorry, I'm not sure about that");
				return;
			}

			if(helpIntent.getChildPage() == null) remove();
			setLastIntent(helpIntent);

//				AOutput.send(player, "&7====================");
//				AOutput.send(player, "&7text: " + queryResult.getText());
//				AOutput.send(player, "&7intent: " + queryResult.getIntent().getDisplayName());
//				AOutput.send(player, "&7confidence: " + queryResult.getIntentDetectionConfidence());
//				for(ResponseMessage responseMessage : queryResult.getResponseMessagesList()) {
//					AOutput.send(player, "&7response: " + responseMessage.getText());
//				}
//				AOutput.send(player, "&7====================");

			String reply = helpIntent.getReply();
			if(reply == null) return;
			AOutput.send(player, helpIntent.getReply());
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

		public boolean isWaitingForResponse() {
			return lastIntent != null && lastIntent.getChildPage() != null &&
					System.currentTimeMillis() - lastIntentUpdate < 1000 * 20;
		}

		public void setLastIntent(HelpIntent lastIntent) {
			this.lastIntent = lastIntent;
			lastIntentUpdate = System.currentTimeMillis();
		}
	}

	public static class StoredRequest {
		private final String query;
		private final String intent;

		public StoredRequest(String query, String intent) {
			this.query = query;
			this.intent = intent;
		}

		public String getQuery() {
			return query;
		}

		public String getIntent() {
			return intent;
		}
	}

	public static Connection getConnection() {
		return DiscordManager.getConnection();
	}

	public static void createTable(Connection connection) throws SQLException {
		Statement stmt = connection.createStatement();

		String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
				"query VARCHAR(255) PRIMARY KEY, " +
				"intent VARCHAR(255) NOT NULL)";
		stmt.executeUpdate(createTableSQL);

		stmt.close();
		connection.close();
	}
}
