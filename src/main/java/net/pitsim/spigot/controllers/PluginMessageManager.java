package net.pitsim.spigot.controllers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.kyro.arcticapi.data.AConfig;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.events.MessageEvent;
import septogeddon.pluginquery.PluginQuery;
import septogeddon.pluginquery.api.QueryConnection;
import septogeddon.pluginquery.api.QueryMessageListener;
import septogeddon.pluginquery.api.QueryMessenger;

import java.io.*;

public class PluginMessageManager implements QueryMessageListener {
	public static void sendMessage(PluginMessage message) {

		if(PitSim.getStatus() == PitSim.ServerStatus.STANDALONE) return;
//
//        String id = PitSim.INSTANCE.getConfig().getString("server-ID");
//        if(id == null) return;

		QueryMessenger messenger = PluginQuery.getMessenger();

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward"); // So BungeeCord knows to forward it
		out.writeUTF("ALL");
		out.writeUTF("PitSim");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF(message.messageID.toString());
			msgout.writeUTF(message.responseID.toString());
			msgout.writeUTF(AConfig.getString("server"));

			msgout.writeInt(message.getStrings().size());
			msgout.writeInt(message.getIntegers().size());
			msgout.writeInt(message.getLongs().size());
			msgout.writeInt(message.getBooleans().size());

			for(String string : message.getStrings()) {
				msgout.writeUTF(string);
			}

			for(int integer : message.getIntegers()) {
				msgout.writeInt(integer);
			}

			for(long longValue : message.getLongs()) {
				msgout.writeLong(longValue);
			}

			for(Boolean bool : message.getBooleans()) {
				msgout.writeBoolean(bool);
			}

		} catch(IOException exception) {
			exception.printStackTrace();
		}

		out.writeInt(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		if(!messenger.broadcastQuery("BungeeCord", out.toByteArray())) {
			// it will return false if there is no active connections
			throw new IllegalStateException("no active connections");
		}

//        Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
//        assert p != null;
//        p.sendPluginMessage(PitSim.INSTANCE, "BungeeCord", out.toByteArray());
	}

	@Override
	public void onQueryReceived(QueryConnection connection, String channel, byte[] message) {
		try {
			if(!channel.equals("BungeeCord")) {
				return;
			}
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String type = in.readUTF();
			String server = in.readUTF();
			String subChannel = in.readUTF();

			if(!subChannel.equals("PitSim")) return;

			int len = in.readInt();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			DataInputStream subDIS = new DataInputStream(new ByteArrayInputStream(msgbytes));

			PluginMessage pluginMessage = new PluginMessage(subDIS);
			PitSim.INSTANCE.getServer().getPluginManager().callEvent(new MessageEvent(pluginMessage, subChannel));

		} catch(Exception ignored) {}
	}
}
