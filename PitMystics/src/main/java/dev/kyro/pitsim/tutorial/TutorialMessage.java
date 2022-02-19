package dev.kyro.pitsim.tutorial;

import java.util.ArrayList;
import java.util.List;

public enum TutorialMessage {
	WELCOME1("&eHey there, Welcome to PitSim!", "Hey there, Welcome to PitSim!"),
	WELCOME2("&eBefore you start playing, let me show you a little more about how PitSim works.", "Before you start playing, let me show you a little more about how PitSim works."),
	WELCOME3("&eIf you're sure that you already know what you're doing, you may skip this tutorial with &f/tutorial skip&e.", "doing, you may skip this tutorial with"),
	WELCOME4("&eHowever, I highly recommend taking the tutorial, as you might not know how to play otherwise.", "However, I highly recommend taking the tutorial, as you might not know how to play otherwise."),
	WELCOME5("&eWith that being said, let's start the tutorial!", "With that being said, let"),
	VAMPIRE1("&eBefore you can learn about PitSim's unique combat mechanics, let's start out with some basics.", "Before you can learn about PitSim"),
	VAMPIRE2("&fPerks &eare abilities that give the player various buffs under certain circumstances.", "are abilities that give the player various buffs under certain circumstances."),
	VAMPIRE3("&eThe one &fPerk &ethat is essential to nearly every situation is &cVampire&e.", "that is essential to nearly every situation is"),
	VAMPIRE4("&eHead over to the &f\"Upgrades and Killstreaks\" &evillager and equip it.", "villager and equip it."),
	PERKS1("&eGreat!", "Great!"),
	PERKS2("&eNow browse though the other perks and fill you remaining 3 Perk Slots.", "Now browse though the other perks and fill you remaining 3 Perk Slots."),
	KILLSTREAK1("&eNicely Done!", "Nicely Done!"),
	KILLSTREAK2("&eNext we will be looking at &fKillstreaks&e.", "Next we will be looking at"),
	KILLSTREAK3("&fKillstreaks &eare similar to &fPerks&e, but are activated on different kill intervals.", ", but are activated on different kill intervals."),
	KILLSTREAK4("&eGo ahead and choose a &fKillstreak &eto fill your first killstreak slot.", "to fill your first killstreak slot."),
	MEGASTREAK1("&eThe final types of upgrades available in this menu are &fMegastreaks&e.", "The final types of upgrades available in this menu are"),
	MEGASTREAK2("&fMegastreaks &eare a type of &fKillstreak &ethat only activate once per streak.", "that only activate once per streak."),
	MEGASTREAK3("&eThey completely change your kill rewards and last until you die.", "They completely change your kill rewards and last until you die."),
	MEGASTREAK4("&eSelect &cOverdrive &efrom the Upgrades Villager. You will unlock more &fMegastreaks &elater.", "from the Upgrades Villager. You will unlock more"),
	MYSTIC1("&eNow that we have gone over upgrades, we can talk about PitSim's unique combat system!", "Now that we have gone over upgrades, we can talk about PitSim"),
	MYSTIC2("&eOur combat system revolves around &dMystic Items&e.", "Our combat system revolves around"),
	MYSTIC3("&dMystic Items &ecome in the form of &cSwords&e, &bBows&e, and &aPants&e, and can have up to 3 unique &dEnchants&e.", ", and can have up to 3 unique"),
	MYSTIC4("&dMystic Items &eare created in the &dMystic Well&e. I will now show you how to create them.", ". I will now show you how to create them."),
	MYSTIC5("&eThis is the home page of the &dMystic Well&e.", "This is the home page of the"),
	MYSTIC6("&eHere is where you choose which type of Mystic Item you would like to enchant.", "Here is where you choose which type of Mystic Item you would like to enchant."),
	MYSTIC7("&eYou can choose  &cSwords&e, &bBows&e, or the various colors of &aPants &ein the &aPhilosopher's Cactus &emenu.", ", or the various colors of"),
	MYSTIC8("&eOnce you have chosen your item to enchant, select an &dEnchant Slot &efrom the top of the page.", "Once you have chosen your item to enchant, select an"),
	ENCHANT1("&eThat will bring you to this menu.", "That will bring you to this menu."),
	ENCHANT2("&eThis is where you can choose from the many unique &dEnchants &ethat PitSim offers.", "This is where you can choose from the many unique"),
	ENCHANT3("&eEvery &dEnchant &eis made to counter another.", "is made to counter another."),
	ENCHANT4("&eThis means that having a carefully selected loadout gives the upper hand in battle.", "This means that having a carefully selected loadout gives the upper hand in battle."),
	TIER1("&eOnce you have selected an &dEnchant&e, you must choose which &cTier &eof it you would like.", ", you must choose which"),
	TIER2("&eRemember that &dMystic Items &ecan have up to &f3 &dEnchants&e.", "can have up to"),
	TIER3("&eHowever, there can only be &f8 &etotal &cEnchant Tiers &ecombined between them.", "However, there can only be"),
	TIER4("&eOnce you have chosen your &cEnchant Tier&e, fill the remaining &dEnchant Slots &eon your item.", "Once you have chosen your"),
	BILL1("&eNow it's your turn!", "your turn!"),
	BILL2("&eUse the &dMystic Well &eto create me a &cSword &ewith &dRARE! &9Billionaire II &eand &9Lifesteal III&e.", "to create me a"),
	BILL3("&eRemember, if you're confused, don't be afraid to scroll up and read again!", "be afraid to scroll up and read again!"),
	RGM1("&eAmazing! You got it!", "Amazing! You got it!"),
	RGM2("&eNow, make me &aPants &ewith &dRARE! &9Retro-Gravity Microcosm III &eand &9Critically Funky III&e.", "Now, make me"),
	RGM3("&eRemember, to enchant &fPants&e, click on the &aPhilosopher's Cactus &efirst.", "Remember, to enchant"),
	MEGA1("&eHey, you're getting the hang of this!", "re getting the hang of this!"),
	MEGA2("&eFinally, make me a &bBow &ewith &dRARE! &9Mega Longbow &eand &9Sprint Drain III&e.", "Finally, make me a"),
	MEGA3("&eIf you're having trouble with this one, &dRARE! &9Mega Longbow &ecan have any Enchant Tier.", "having trouble with this one,"),
	ARMOR1("&eGreat job!, you now have one of each type of &dMystic Item&e!", "Great job!, you now have one of each type of"),
	ARMOR2("&eNow you will get the chance to use them, but first put on the &fArmor &eI gave you.", "Now you will get the chance to use them, but first put on the"),
	VIEWNON1("&eAlright! lets proceed.", "Alright! lets proceed."),
	VIEWNON2("&eThis is a &fDummy Player &ethat you will be using to test out the abilities of your new items.", "that you will be using to test out the abilities of your new items."),
	REACH_MEGA1("&eGo ahead and attack it with your sword.", "Go ahead and attack it with your sword."),
	REACH_MEGA2("&eKeep killing the &fDummy &euntil you get to a 50 killstreak and activate your &cMegastreak&e.", "until you get to a 50 killstreak and activate your"),
	PRESTIGE1("&ePerfect!", "Perfect!"),
	PRESTIGE2("&eNow that you know how streaking works, it's time to tell you about &fPrestige&e.", "Now that you know how streaking works,"),
	PRESTIGE3("&eOnce you have reached the specified requirements, you can &fPrestige&e.", "Once you have reached the specified requirements, you can"),
	PRESTIGE4("&fPrestiging &eresets your &blevel &eand &6gold&e, but rewards &fRenown&e.", ", but rewards"),
	PRESTIGE5("&fRenown &ecan be spent on high-tier permanent upgrades.", "can be spent on high-tier permanent upgrades."),
	PRESTIGE6("&eI will now set your &blevel &eto &b&l120 &eso you can prestige, but only this once.", "so you can prestige, but only this once."),
	PRESTIGE7("&eAre you ready?", "Are you ready?"),
	PRESTIGE8("&eAlright, now go to the &f\"Prestige and Renown\" &evillager and prestige!", "villager and prestige!"),
	TENACTITY1("&eCongratulations, now use your &fRenown &eto purchase &cTenacity I&e from the Renown shop.", "Congratulations, now use your"),
	FINAL1("&eThis concludes the PitSim tutorial!", "This concludes the PitSim tutorial!"),
	FINAL2("&eIf you're still confused, join our community Discord at &f&ndiscord.gg/pitsim", "still confused, join our community Discord at"),
	FINAL3("&eWith that being said, have fun on your PitSim adventure!", "With that being said, have fun on your PitSim adventure!"),

	SPACER("&8&m------------------------------------", "------------------------------------");


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
