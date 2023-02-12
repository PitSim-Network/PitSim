package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class PitLoreBuilder extends ALoreBuilder {
	public static List<Character> specialFormatting = new ArrayList<>();

	static {
		specialFormatting.add('k');
		specialFormatting.add('l');
		specialFormatting.add('m');
		specialFormatting.add('n');
		specialFormatting.add('o');
	}

	private final int lineWidth;

	public PitLoreBuilder(String singleLine) {
		this(singleLine, 32);
	}

	public PitLoreBuilder(String longLine, int lineWidth) {
		this.lineWidth = lineWidth;
		addLongLine(longLine);
	}

	public void addLongLine(String longLine) {
		longLine = ChatColor.translateAlternateColorCodes('&', longLine);
		String currentString = "";
		String lastChatColor = "";
		for(String word : longLine.split(" ")) {
			word = word.replaceAll("\\[]", " ");
			if(ChatColor.stripColor(currentString).length() + 1 + ChatColor.stripColor(word).length() > lineWidth) {
				getLore().add(currentString);
				currentString = lastChatColor;
			}

			char[] characters = word.toCharArray();
			for(int i = 0; i < characters.length; i++) {
				char character = characters[i];
				if(character != '\u00A7' || i == characters.length - 1) continue;

				char nextChar = characters[i + 1];
				if(specialFormatting.contains(nextChar)) {
					lastChatColor += "\u00A7" + nextChar;
					continue;
				}

				lastChatColor = "\u00A7" + nextChar;
			}

			if(!ChatColor.stripColor(currentString).isEmpty()) currentString += " ";
			currentString += word;
		}
		if(!ChatColor.stripColor(currentString).isEmpty()) getLore().add(currentString);
	}
}
