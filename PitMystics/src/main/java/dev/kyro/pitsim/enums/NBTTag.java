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
	DROP_CONFIRM("pr-dropconfirm"),
	ORIGINAL_COLOR("pr-original-color"),
	CURRENT_LIVES("pr-current-lives"),
	MAX_LIVES("pr-max-lives"),
	IS_FEATHER("pr-isfeather"),
	IS_TOKEN("pr-istoken"),
	IS_VILE("pr-isvile"),
	IS_SHARD("pr-isshard"),
	IS_GEM("pr-isgem"),
	IS_GHELMET("pr-isghelm"),
	GHELMET_UUID("pr-ghelm-uuid"),
	GHELMET_GOLD("pr-ghelm-gold"),
	GHELMET_ABILITY("pr-ghelm-ability"),
	IS_YUMMY_BREAD("pr-is-yummy-bread"),
	IS_VERY_YUMMY_BREAD("pr-is-very-yummy-bread"),

	IS_VENOM("pr-isvenom");

	private final String ref;

	NBTTag(String ref) {

		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}
}
