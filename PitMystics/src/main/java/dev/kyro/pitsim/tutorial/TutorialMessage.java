package dev.kyro.pitsim.tutorial;

import java.util.ArrayList;
import java.util.List;

public enum TutorialMessage {
    DARK_BLUE("Dark Blue", "Blue"),
    DARK_GREEN("Dark Green", "Green"),
    WELCOME1("&eHey there, Welcome to PitSim!", ""),
    WELCOME2("&eBefore you start playing, let me show you a little more about how PitSim works.", "Before you start playing, let me show you a little more about how PitSim works."),
    WELCOME3("&eIf you're sure that you already know what you're doing, you may skip this tutorial with &f/tutorial skip&e.", "If you're sure that you already know what you're doing, you may skip this tutorial with"),
    WELCOME4("&eHowever, I highly recommend taking the tutorial, as you might not know how to play otherwise.", "However, I highly recommend taking the tutorial, as you might not know how to play otherwise."),
    WELCOME5("&eWith that being said, lets start the tutorial!", "With that being said, lets start the tutorial!"),
    VAMPIRE1("&eBefore you can learn about PitSim's unique combat mechanics, lets start out with some basics.", "Before you can learn about PitSim's unique combat mechanics, lets start out with some basics."),
    VAMPIRE2("&fPerks &eare abilities that gave the player various buffs under certain circumstances.", "are abilities that gave the player various buffs under certain circumstances."),
    VAMPIRE3("&eThe one &fPerk &ethat is essential to nearly every situation is &cVampire&e.", "that is essential to nearly every situation is"),
    VAMPIRE4("&eHead over to the &f\"Upgrades and Killstreaks\" &evillager and equip it.", "villager and equip it."),
    PERKS1("&eGreat!", "Great!"),
    PERKS2("&eNow browse though the other perks and fill you remaining 3 Perk Slots.", "Now browse though the other perks and fill you remaining 3 Perk Slots."),
    KILLSTREAK1("&eNicely Done!", "Nicely Done!"),
    KILLSTREAK2("&eNext we will be looking at &fKillstreaks&e.", "Next we will be looking at"),
    KILLSTREAK3("&fKillstreaks &eare similar to Perks, but are activated on different kill intervals.", "are similar to Perks, but are activated on different kill intervals."),
    KILLSTREAK4("&eGo ahead and choose a Killstreak to fill your first Killstreak slot.", "Go ahead and choose a Killstreak to fill your first Killstreak slot."),
    MEGASTREAK1("&eThe final types of upgrade available in this menu are &fMegastreaks&e.", "The final types of upgrade available in this menu are"),
    MEGASTREAK2("&fMegastreaks &eare a type of Killstreak that only activate once per streak.", "are a type of Killstreak that only activate once per streak."),
    MEGASTREAK3("&eThey completely change your kill rewards and last until you die.", "They completely change your kill rewards and last until you die."),
    MEGASTREAK4("&eSelect &cOverdrive &efrom the Upgrades Villager. You will unlock more Megastreaks later.", "from the Upgrades Villager. You will unlock more Megastreaks later."),
    MYSTIC1("&eNow that we have gone over upgrades, we can talk about PitSim's unique combat system!", "Now that we have gone over upgrades, we can talk about PitSim's unique combat system!"),
    MYSTIC2("&eOur combat system revolves around &fMystic Items&e.", "Our combat system revolves around &fMystic Items"),
    MYSTIC3("&fMystic Items &ecome in the form of Swords, Bows, and Pants, and can have up to 3 unique &fEnchants&e.", "come in the form of Swords, Bows, and Pants, and can have up to 3 unique"),
    MYSTIC4("&eMystic Items are created in the &dMystic Well&e. I will now show you how to create them.", ". I will now show you how to create them."),
    MYSTIC5("&eThis is the home page of the &dMystic Well&e.", "This is the home page of the"),
    MYSTIC6("&eHere is where you choose which type of Mystic Item you would like to enchant.", "Here is where you choose which type of Mystic Item you would like to enchant."),
    MYSTIC7("&eYou can choose Swords, Bows, or the various colors of pants in the &aPhilosopher's Cactus &emenu.", "You can choose Swords, Bows, or the various colors of pants in the"),
    MYSTIC8("&eOnce you have chosen your item to enchant, select an Enchant Slot from the top of the page.", "Once you have chosen your item to enchant, select an Enchant Slot from the top of the page."),
    ENCHANT1("&eThat will bring you to this menu.", "That will bring you to this menu."),
    ENCHANT2("&eThis is where you can choose from the many unique Enchants that PitSim offers.", "This is where you can choose from the many unique Enchants that PitSim offers."),
    ENCHANT3("&eEvery Enchant is made to counter another.", "Every Enchant is made to counter another."),
    ENCHANT4("&eThis means that having a carefully selected loadout gives the upper hand in battle.", "This means that having a carefully selected loadout gives the upper hand in battle."),
    TIER1("&eOnce you have selected an Enchant, you must choose which Tier of it you would like.", "Once you have selected an Enchant, you must choose which Tier of it you would like."),
    TIER2("&eRemember that Mystic Items can have up to 3 Enchants.", "Remember that Mystic Items can have up to 3 Enchants."),
    TIER3("&eHowever, there can only be 8 total Enchant Tiers combined between them.", "However, there can only be 8 total Enchant Tiers combined between them."),
    TIER4("&eOnce you have chosen your Enchant Tier, fill the remaining Enchant Slots on your item.", "Once you have chosen your Enchant Tier, fill the remaining Enchant Slots on your item."),
    BILL1("&eNow it's your turn!", "Now it's your turn!"),
    BILL2("&eUse the &dMystic Well &eto create me a &fSword &ewith &dRARE! &9Billionaire II &eand &9Lifesteal III&e.", "to create me a"),
    BILL3("&eRemember, if you're confused, don't be afraid to scroll up and read again!", "Remember, if you're confused, don't be afraid to scroll up and read again!"),
    RGM1("&eAmazing! You got it!", "Amazing! You got it!"),
    RGM2("&eNow, make me &fPants &ewith &dRARE! &9Retro-Gravity Microcosm III &eand &9Critically Funky III&e.", "Now, make me"),
    RGM3("&eRemember, to enchant &fPants&e, click on the &aPhilosopher's Cactus &efirst.", "Remember, to enchant"),
    MEGA1("&eHey, you're getting the hang of this!", "Hey, you're getting the hang of this!"),
    MEGA2("&eFinally, make me a &fBow &ewith &dRARE! &9Mega Longbow &eand &9Sprint Drain III&e.", "Finally, make me a"),
    MEGA3("&eIf you're having trouble with this one, &dRARE! &9Mega Longbow &ecan have any Enchant Tier", "If you're having trouble with this one,");



    public String message;
    public String identifier;

    TutorialMessage(String message, String identifier) {
        this.message = message;
        this.identifier = identifier;

    }

    public static List<String> messageStrings = new ArrayList<>();

    public static List<String> getIdentifiers() {
        for(TutorialMessage value : TutorialMessage.values()) {
            messageStrings.add(value.identifier);
        }
        return messageStrings;
    }
}
