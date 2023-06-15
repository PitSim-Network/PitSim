package net.pitsim.spigot.commands;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.SkinManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LoadSkinCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		if(args.length < 1) {
			AOutput.error(player, "&c&lERROR!&7 Usage: /" + label + " <skinName>");
			return false;
		}

		loadTexture(player, args[0]);

		return false;
	}

	public static void loadTexture(Player player, String skinName) {
		AOutput.send(player, "Attempting to load texture information for: " + skinName);

		SkinManager.loadAndSkinNPC(skinName, new BukkitRunnable() {
			@Override
			public void run() {
				NPC tempNPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, skinName);
				tempNPC.spawn(player.getLocation());
				SkinTrait skinTrait = CitizensAPI.getTraitFactory().getTrait(SkinTrait.class);
				tempNPC.addTrait(skinTrait);
				skinTrait.setSkinName(skinName);

				System.out.println("new MinecraftSkin(\"" + skinName + "\",\n\t\t\"" + skinTrait.getTexture() +
						"\",\n\t\t\"" + skinTrait.getSignature() + "\"\n);");
				AOutput.send(player, "&9&lSKIN!&7 Printed out skin information to console");

				new BukkitRunnable() {
					@Override
					public void run() {
						tempNPC.destroy();
					}
				}.runTaskLater(PitSim.INSTANCE, 20);
			}
		});
	}
}
