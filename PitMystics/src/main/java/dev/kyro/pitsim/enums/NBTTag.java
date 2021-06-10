package dev.kyro.pitsim.enums;

public enum NBTTag {

	ITEM_UUID("pr-uuid"),
	PIT_ENCHANTS("pr-enchants"),
	PIT_ENCHANT_ORDER("pr-enchant-order"),
	ITEM_JEWEL_ENCHANT("pr-jewel-enchant"),
	ITEM_TOKENS("pr-token-num"),
	ITEM_RTOKENS("pr-rare-token-num"),
	ITEM_ENCHANTS("pr-enchant-num"),
	PLAYER_KILLS("pr-player-kills"),
	BOT_KILLS("pr-bot-kills"),
	JEWEL_KILLS("pr-jewel-kills"),
	IS_JEWEL("pr-isjewel"),
	IS_GEMMED("pr-isgemmed"),
	UNDROPPABLE("pr-undroppable"),
	DROP_CONFIRM("pr-dropconfirm");

	private final String ref;

	NBTTag(String ref) {

		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}
}
