package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.controllers.PluginMessageManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PluginMessage {

    private final List<String> strings = new ArrayList<>();
    private final List<Integer> integers = new ArrayList<>();
    private final List<Boolean> booleans = new ArrayList<>();

    public UUID messageID;
    public UUID responseID;
    public PluginMessage requestMessage;
    public String originServer;

    public PluginMessage(DataInputStream data) throws IOException {

        messageID = UUID.fromString(data.readUTF());
        responseID = UUID.fromString(data.readUTF());

        originServer = data.readUTF();

        int stringCount = data.readInt();
        int integerCount = data.readInt();
        int booleanCount = data.readInt();

        for(int i = 0; i < stringCount; i++) {
            strings.add(data.readUTF());
        }

        for(int i = 0; i < integerCount; i++) {
            integers.add((int) data.readLong());
        }

        for(int i = 0; i < booleanCount; i++) {
            booleans.add(data.readBoolean());
        }

    }

    public PluginMessage() {
        messageID  = UUID.randomUUID();
        responseID = UUID.randomUUID();
    }


    public PluginMessage writeString(String string) {
        strings.add(string);
        return this;
    }

    public PluginMessage writeInt(int integer) {
        integers.add(integer);
        return this;
    }

    public PluginMessage writeBoolean(boolean bool) {
        booleans.add(bool);
        return this;
    }

    public void send() {
        PluginMessageManager.sendMessage(this);
    }

    public List<String> getStrings() {
        return strings;
    }

    public List<Integer> getIntegers() {
        return integers;
    }

    public List<Boolean> getBooleans() {
        return booleans;
    }

    public void respond(PluginMessage message) {
        message.responseID = messageID;
        message.send();
    }

    public boolean isResponseTo(PluginMessage message) {
        return responseID.equals(message.messageID);
    }
}
