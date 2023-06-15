package net.pitsim.pitsim.ahelp;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class HelpIntent implements Summarizable {
	private final String identifier;
	private String reply;
	private List<String> trainingPhrases;
	private HelpPageIdentifier parentPage;
	private HelpPageIdentifier childPage;

	public HelpIntent(String identifier, HelpPageIdentifier parentPage) {
		this.identifier = identifier;
		this.parentPage = parentPage;
	}

	public String getReply() {
		return reply;
	}

	public HelpIntent setReply(String summary) {
		this.reply = ChatColor.translateAlternateColorCodes('&', "&9&lAI!&7 "+ summary);
		return this;
	}

	public HelpIntent setTrainingPhrases(List<String> trainingPhrases) {
		this.trainingPhrases = trainingPhrases;
		return this;
	}

	public HelpIntent setTrainingPhrases(String... trainingPhrases) {
		setTrainingPhrases(Arrays.asList(trainingPhrases));
		return this;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String getSummary() {
		return reply;
	}

	@Override
	public List<String> getTrainingPhrases() {
		return trainingPhrases;
	}

	public HelpPageIdentifier getParentPage() {
		return parentPage;
	}

	public HelpPageIdentifier getChildPage() {
		return childPage;
	}

	public HelpIntent setChildPage(HelpPageIdentifier childPage) {
		this.childPage = childPage;
		return this;
	}
}
