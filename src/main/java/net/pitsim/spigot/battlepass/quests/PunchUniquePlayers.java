package net.pitsim.spigot.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.battlepass.PassData;
import net.pitsim.spigot.battlepass.PassManager;
import net.pitsim.spigot.battlepass.PassQuest;
import net.pitsim.spigot.controllers.PlayerManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Formatter;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class PunchUniquePlayers extends PassQuest {
	public static PunchUniquePlayers INSTANCE;

	public PunchUniquePlayers() {
		super("&f&lAwkward Greeting", "punchplayers", QuestType.WEEKLY);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) ||
				attackEvent.getAttacker() == attackEvent.getDefender() || attackEvent.getArrow() != null || attackEvent.getPet() != null ||
				!canProgressQuest(attackEvent.getAttackerPitPlayer())) return;

		if(!Misc.isAirOrNull(attackEvent.getAttackerPlayer().getItemInHand())) return;
		PassData passData = attackEvent.getAttackerPitPlayer().getPassData(PassManager.currentPass.startDate);
		if(passData.uniquePlayersPunched.contains(attackEvent.getDefenderPlayer().getUniqueId().toString())) return;
		passData.uniquePlayersPunched.add(attackEvent.getDefenderPlayer().getUniqueId().toString());
		progressQuest(attackEvent.getAttackerPitPlayer(), 1);
		Sounds.PUNCH_UNIQUE_PLAYER.play(attackEvent.getAttackerPlayer());
		String playerName = PlaceholderAPI.setPlaceholders(attackEvent.getDefenderPlayer(),
				ChatColor.translateAlternateColorCodes('&', "%luckperms_prefix%" + attackEvent.getDefenderPlayer().getName()));
		AOutput.send(attackEvent.getAttackerPlayer(), "&f&lGREETING!&7 You greeted " + playerName);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.BRICK)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Say hi to &f" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7unique players",
						"&7by punching them :P",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20,
										progress / questLevel.getRequirement(pitPlayer)) + "&8]",
						"&7Reward: &3" + questLevel.rewardPoints + " &7Quest Points"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public QuestLevel getDailyState() {
		return null;
	}

	@Override
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(20, 100));
		questLevels.add(new QuestLevel(30, 150));
		questLevels.add(new QuestLevel(40, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
