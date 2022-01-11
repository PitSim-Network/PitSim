package dev.kyro.pitsim.tutorial;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class MessageManager implements Listener {


	public static void sendTutorialMessage(Player player, TutorialMessage message) {

		AOutput.send(player, message.message);
	}



	static {
			ProtocolLibrary.getProtocolManager().addPacketListener(
					new PacketAdapter(PitSim.INSTANCE, PacketType.Play.Server.CHAT) {
						@Override
						public void onPacketSending(PacketEvent event) {

							if(TutorialMessage.getMessages().contains(event.getPacket().getStrings().read(0))) return;

							if(TutorialManager.tutorials.containsKey(event.getPlayer())) event.setCancelled(true);
						}
					});
	}

}
