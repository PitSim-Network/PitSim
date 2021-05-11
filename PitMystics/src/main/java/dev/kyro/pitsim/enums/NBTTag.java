package dev.kyro.pitsim.enums;

public enum NBTTag {

	ITEM_UUID("pr-uuid"),
	PIT_ENCHANTS("pr-enchants"),
	ITEM_TOKENS("pr-token-num"),
	ITEM_RTOKENS("pr-rare-token-num"),
	ITEM_ENCHANTS("pr-enchant-num");

	private final String ref;

	NBTTag(String ref) {

		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}
}
