package net.pitsim.pitsim.ahelp;

import java.util.List;

public interface Summarizable {
	String getIdentifier();
	String getSummary();
	List<String> getTrainingPhrases();
}
