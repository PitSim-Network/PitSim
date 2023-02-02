package dev.kyro.pitsim.controllers;

//import com.theokanning.openai.OpenAiService;
//import com.theokanning.openai.completion.CompletionRequest;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

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
//		int maxTokens = Misc.weightedRandom(weightedMap);
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
//}
