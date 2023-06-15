package net.pitsim.spigot.ahelp;

public class HelpPage {
	private final HelpPageIdentifier identifier;
	private String fullName;
	private String entryFulfillment;

	public HelpPage(HelpPageIdentifier identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier.getIdentifier();
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEntryFulfillment() {
		return entryFulfillment;
	}

	public HelpPage setEntryFulfillment(String entryFulfillment) {
		this.entryFulfillment = entryFulfillment;
		return this;
	}
}
