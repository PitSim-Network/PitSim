package dev.kyro.pitsim.ahelp;

import com.google.cloud.dialogflow.cx.v3.*;
import com.google.protobuf.FieldMask;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class HelpManager implements Listener {
	public static List<HelpIntent> helpIntents = new ArrayList<>();
	public static List<HelpPage> helpPages = new ArrayList<>();

	public static final String PROJECT_ID = "pitsim-network";
	public static final String AGENT_ID = "deed93e6-7edc-42e7-ac0f-9a472df87c56";
	public static final String LOCATION_ID = "us-east1";
	public static final String FLOW_ID = "00000000-0000-0000-0000-000000000000";
	public static final String START_PAGE_ID = "START_PAGE";
	private static final Map<Player, HelperAgent> helpClientMap = new HashMap<>();

	private static SessionsSettings sessionsSettings;
	private static PagesSettings pagesSettings;
	private static IntentsSettings intentsSettings;

	public HelpManager() {
		setupSettings();
		setupEnv();
	}

	public static void registerIntentsAndPages() {
//		Intents
		for(Megastreak megastreak : PerkManager.megastreaks) registerIntent(megastreak);

		helpIntents.add(new HelpIntent("WHAT_IS_THE_DARKZONE", HelpPageIdentifier.MAIN_PAGE)
				.setReply("The darkzone is a place that makes you hate living more than anything else")
				.setTrainingPhrases(
						"what is the darkzone?",
						"what do you do in the darkzone?",
						"how do you unlock the darkzone?"
				));

		helpIntents.add(new HelpIntent("BEST_PERKS", HelpPageIdentifier.MAIN_PAGE)
				.setChildPage(HelpPageIdentifier.BEST_PERKS)
				.setReply("Are you asking about streaking or fighting perks?")
				.setTrainingPhrases(
						"what are the best perks?",
						"what perks are the best?"
				));
		helpIntents.add(new HelpIntent("BEST_STREAKING_PERKS", HelpPageIdentifier.BEST_PERKS)
				.setReply("You may as well jump off a bridge")
				.setTrainingPhrases(
						"streaking",
						"streaking perks"
				));
		helpIntents.add(new HelpIntent("BEST_FIGHTING_PERKS", HelpPageIdentifier.BEST_PERKS)
				.setReply("Why the fuck would I know")
				.setTrainingPhrases(
						"fighting",
						"fighting perks"
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

//		Pages
		helpPages.add(new HelpPage(HelpPageIdentifier.MAIN_PAGE));
		helpPages.add(new HelpPage(HelpPageIdentifier.BEST_PERKS)
				.setEntryFulfillment("Are you asking about streaking or fighting perks?"));
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

			Map<HelpPage, Page> pageMap = new LinkedHashMap<>();
			Map<HelpIntent, Intent> intentMap = new LinkedHashMap<>();
			for(HelpPage helpPage : helpPages) {
				Page page = null;
				for(Page testPage : pagesClient.listPages(flowParent).iterateAll()) {
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
				for(Intent testIntent : intentsClient.listIntents(agentParent).iterateAll()) {
					if(!testIntent.getDisplayName().equals(helpIntent.getIdentifier())) continue;
					intent = testIntent;
					break;
				}
				intentMap.put(helpIntent, intent);
			}

			try {
				for(Map.Entry<HelpPage, Page> entry : pageMap.entrySet()) {
					HelpPage helpPage = entry.getKey();
					Page page = entry.getValue();
					if(page != null) {
//						page = Page.newBuilder()
//								.setName(page.getName())
//								.setEntryFulfillment(createFulfillment(helpPage.getEntryFulfillment()))
//								.build();
//						FieldMask fieldMask = FieldMask.newBuilder().addPaths("entry_fulfillment").build();
//						page = pagesClient.updatePage(page, fieldMask);
//						pageMap.put(helpPage, page);
//						AOutput.log("Updated page: " + page.getDisplayName());
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
				}
			} catch(Exception exception) {
				exception.printStackTrace();
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
					continue;
				}

				intent = Intent.newBuilder()
						.setDisplayName(helpIntent.getIdentifier())
						.addAllTrainingPhrases(createTrainingPhrases(helpIntent.getTrainingPhrases()))
						.build();
				intent = intentsClient.createIntent(agentParent, intent);
				intentMap.put(helpIntent, intent);
				AOutput.log("Created intent: " + helpIntent.getIdentifier());
			}

			try {
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
				}
			} catch(Exception exception) {
				exception.printStackTrace();
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

		public void detectIntent(String text) {
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

				String intent = queryResult.getIntent().getDisplayName();
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
			} catch(IOException exception) {
				throw new RuntimeException(exception);
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

		public boolean isWaitingForResponse() {
			return lastIntent != null && lastIntent.getChildPage() != null &&
					System.currentTimeMillis() - lastIntentUpdate < 1000 * 20;
		}

		public void setLastIntent(HelpIntent lastIntent) {
			this.lastIntent = lastIntent;
			lastIntentUpdate = System.currentTimeMillis();
		}
	}
}
