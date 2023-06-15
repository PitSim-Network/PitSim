package net.pitsim.spigot.help;

import java.util.List;

public interface Summarizable {
	String getIdentifier();
	String getSummary();
	List<String> getTrainingPhrases();
}
