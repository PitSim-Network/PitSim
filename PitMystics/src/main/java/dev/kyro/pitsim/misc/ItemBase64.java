package dev.kyro.pitsim.misc;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ItemBase64 {

	public static String itemTo64(ItemStack stack) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(stack);

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to save item stack.", e);
		}
	}

	public static ItemStack itemFrom64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			try {
				return (ItemStack) dataInput.readObject();
			} finally {
				dataInput.close();
			}
		}
		catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
}
