package dev.kyro.pitsim.enums;

public enum DisplayItemType {
	SELECT_PANEL(true),
	MAIN_PERK_PANEL(true),
	VIEW_PANEL(false);

	private final boolean appendStatus;

	DisplayItemType(boolean appendStatus) {
		this.appendStatus = appendStatus;
	}

	public boolean shouldAppendStatus() {
		return appendStatus;
	}
}