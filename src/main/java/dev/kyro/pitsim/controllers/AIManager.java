package dev.kyro.pitsim.controllers;

//import com.theokanning.openai.OpenAiService;
//import com.theokanning.openai.completion.CompletionRequest;
//public class AIManager implements Listener {
//
//	public static String getAnswer(String question) {
//		String prompt = "Respond in a polite way\nQuestion: " + question + "\nAnswer: ";
//
//		Map<Integer, Double> weightedMap = new HashMap<>();
//		weightedMap.put(15, 10.0);
//		weightedMap.put(20, 5.0);
//		weightedMap.put(25, 5.0);
//		weightedMap.put(100, 2.0);
//		int maxTokens = weightedRandom(weightedMap);
//
//		OpenAiService service = new OpenAiService("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");
//		CompletionRequest.CompletionRequestBuilder builder = CompletionRequest.builder()
//				.prompt(prompt)
//				.model("davinci:ft-personal-2023-01-17-08-11-03")
////				.model("davinci:ft-personal-2023-01-17-09-14-35")
////				.model("davinci:ft-personal-2023-01-17-09-25-12")
//				.temperature(1.0)
//				.maxTokens(maxTokens);
//		return service.createCompletion(builder.build()).getChoices().stream().findFirst().get().getText();
//	}
//
//	public static <T> T weightedRandom(Map<T, Double> weightedMap) {
//		// Normalize the weights
//		double sum = 0.0;
//		for(double weight : weightedMap.values()) sum += weight;
//		Map<T, Double> normalizedWeights = new HashMap<>();
//		for(Map.Entry<T, Double> entry : weightedMap.entrySet()) normalizedWeights.put(entry.getKey(), entry.getValue() / sum);
//
//		// Select a random number between 0 and 1
//		double rand = Math.random();
//
//		// Find the element corresponding to the random number
//		double total = 0.0;
//		for(Map.Entry<T, Double> entry : normalizedWeights.entrySet()) {
//			total += entry.getValue();
//			if(total >= rand) return entry.getKey();
//		}
//
//		return normalizedWeights.entrySet().iterator().next().getKey();
//	}
//}
