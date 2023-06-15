package net.pitsim.pitsim.enums;

public enum NBTTag {
	CUSTOM_ITEM("pr-item"),
	ITEM_UUID("pr-uuid"),
	DROPPED_ITEM_UUID("pr-random-uuid"),

	MYSTIC_ENCHANTS("pr-enchants"),
	MYSTIC_ENCHANT_ORDER("pr-enchant-order"),
	ITEM_TOKENS("pr-token-num"),
	ITEM_RTOKENS("pr-rare-token-num"),
	ITEM_ENCHANT_NUM("pr-enchant-num"),
	ITEM_JEWEL_KILLS("pr-jewel-kills"),
	ITEM_JEWEL_ENCHANT("pr-jewel-enchant"),
	ITEM_LAST_SERVER("pr-last-server"),
	IS_TUTORIAL_ITEM("pr-istutorial"),
	PREVIOUS_TUTORIAL_ITEM("pr-previous-tutorial"),

	IS_LOCKED("pr-islocked"),
	IS_JEWEL("pr-isjewel"),
	IS_GEMMED("pr-isgemmed"),
	SAVED_PANTS_COLOR("pr-saved-color"), // for when item is broken (but is kept up to date all the time)
	ORIGINAL_PANTS_COLOR("pr-original-color"), // for undying pants
	CURRENT_LIVES("pr-current-lives"),
	MAX_LIVES("pr-max-lives"),
	TAINTED_TIER("pr-tainted-tier"),

	IS_VENOM("pr-isvenom"),
	IS_PREMADE("pr-ispremade"),
	IS_GOD("pr-isgod"),

	CANNOT_PICKUP("pr-cannot-pickup"),
	SOUL_PICKUP_AMOUNT("pr-soul-pickup-amount"),
	GOLD_PICKUP_AMOUNT("pr-gold-pickup-amount"),
	COOKIE_GIVER("pr-cookie-giver"),
	COOKIE_RECEIVER("pr-cookie-receiver"),
	CAKE_USAGES("pr-cake-usages"),

	GHELMET_GOLD("pr-ghelm-gold"),
	GHELMET_ABILITY("pr-ghelm-ability"),

	POTION_IDENTIFIER("pr-potion-identifier"),
	POTION_POTENCY("pr-potion-potency"),
	POTION_DURATION("pr-potion-duration"),
	IS_SPLASH_POTION("pr-is-splash-potion"),

	GHELMET_UUID("pr-ghelm-uuid"),
	IS_FEATHER("pr-isfeather"),
	IS_CORRUPTED_FEATHER("pr-iscfeather"),
	IS_TOKEN("pr-istoken"),
	IS_VILE("pr-isvile"),
	IS_SHARD("pr-isshard"),
	IS_GEM("pr-isgem"),
	IS_GHELMET("pr-isghelm"),
	IS_YUMMY_BREAD("pr-is-yummy-bread"),
	IS_VERY_YUMMY_BREAD("pr-is-very-yummy-bread2"),
	ZOMBIE_FLESH("pr-zombie-flesh"),
	SKELETON_BONE("pr-skeleton-bone"),
	CREEPER_POWDER("pr-creeper-powder"),
	SPIDER_EYE("pr-spider-eye"),
	CAVESPIDER_EYE("pr-cavespider-eye"),
	MAGMACUBE_CREAM("pr-magmacube-cream"),
	PIGMAN_PORK("pr-pigman-pork"),
	WITHER_SKELETON_SKULL("pr-wither-skull"),
	GOLEM_INGOT("pr-golem-ingot"),
	ENDERMAN_PEARL("pr-enderman-pearl"),
	INVENTORY_INDEX("pr-inventory-index"),
	IS_AUCTION_REWARD("pr-is-auction-reward"),
	;

	private final String ref;

	NBTTag(String ref) {
		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}
}
