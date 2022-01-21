package dev.kyro.pitsim.tutorial;

public enum Task {
    VIEW_MAP(1),
    EQUIP_VAMPIRE(2),
    EQUIP_PERKS(3),
    EQUIP_KILLSTREAK(4),
    EQUIP_MEGASTREAK(5),
    VIEW_MYSTIC_WELL(6),
    VIEW_ENCHANTS(7),
    VIEW_ENCHANT_TIERS(8),
    ENCHANT_BILL_LS(9),
    ENCHANT_RGM(10),
    ENCHANT_MEGA_DRAIN(11),
    EQUIP_ARMOR(12),
    VIEW_NON(13),
    ACTIVATE_MEGASTREAK(14),
    PRESTIGE(15),
    BUY_TENACITY(16),
    FINISH_TUTORIAL(17);

    public int order;

    Task(int order) {
        this.order = order;
    }
}
