package dev.kyro.pitsim.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class HeadLib {

	public static ItemStack getCustomHead(String url) {

		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		Field field = null;

		assert skullMeta != null;

		if(url.length() < 16) {

			skullMeta.setOwner(url);

			skull.setItemMeta(skullMeta);
			return skull;
		}

		StringBuilder s_url = new StringBuilder();
		s_url.append("https://textures.minecraft.net/texture/").append(url); // We get the texture link.

		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null); // We create a GameProfile

		// We get the bytes from the texture in Base64 encoded that comes from the Minecraft-URL.
		byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", s_url.toString()).getBytes());

		// We set the texture property in the GameProfile.
		gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

		try {

			field = skullMeta.getClass().getDeclaredField("profile"); // We get the field profile.

			field.setAccessible(true); // We set as accessible to modify.
			field.set(skullMeta, gameProfile); // We set in the skullMeta the modified GameProfile that we created.

		} catch(Exception e) {
			e.printStackTrace();
		}

		skull.setItemMeta(skullMeta);

		return skull; //Finally, you have the custom head!

	}

	public static String getDownArrowHead() {
		return "7437346d8bda78d525d19f540a95e4e79daeda795cbc5a13256236312cf";
	}

	public static String getUpArrowHead() {
		return "3040fe836a6c2fbd2c7a9c8ec6be5174fddf1ac20f55e366156fa5f712e10";
	}

	public static String getPlusHead() {
		return "3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716";
	}

	public static String getServerHead(int index) {

		switch(index) {
			case 0:
				return "90fbdf260e2cf2025bfbc304fa7b81593016d0729b00db16f09ed01f9bd3e699";
			case 1:
				return "21d859e8b14f62646859cf3804a64f1060d6879341b4c3385b46a0ec40faf73c";
			case 2:
				return "b3d93e8b5fb0b5d50abd4ef88532f46874b9924f668ddb01091468e4e61b9c83";
			case 3:
				return "ea79daa9208a056500c83cd2090faec91eae5405792e4f5472697b0e7dac323a";
			case 4:
				return "21bd898ccb1e5af71d64411a159c0d469af6391062e802bfe8199565e8d7be68";
			case 5:
				return "33cd934f11f0766f5410eba9e7b5f0ceb66f6b317e845cb6a501f37258556a43";
			case 6:
				return "795544a4080f6a4af19e0801da492838fb04bc534fc84600e04c8815e131e29d";
			case 7:
				return "f43f8b105ec3b48963e981aff825c5ab94dffee5d84a869ad072afa8fd214e7e";
			case 8:
				return "cef83d0279a2e51e7e532d202df5ec7db83f96d3fbb245da1b628c1f0b1eecbd";
			case 9:
				return "7e4eab5c8c82e0a91828f0a5dbd43d26076bc4b57ce1d135eca2d7bd0b1de30";
		}
		return "7e4eab5c8c82e0a91828f0a5dbd43d26076bc4b57ce1d135eca2d7bd0b1de30";
	}

	public static String getDarkzoneHead(int index) {


		switch(index) {
			case 0:
				return "dcdf22e25ab547b376f083cbf8462c4268cef5555a9689b0e7d2dffe8672b2";
			case 1:
				return "78a42df06fc916de110f61bd76eddbf58ed4249fce5ee51c219ec75a37b414";
			case 2:
				return "1ef134f0efa88351b837f7c087afe1b3fb36435ab7d746fa37c0ef155e4f29";
			case 3:
				return "1965e9c57c14c95c84e622e5306e1cf23bc5f1e47ac791f3d357b5ae8cded24";
		}
		return "1965e9c57c14c95c84e622e5306e1cf23bc5f1e47ac791f3d357b5ae8cded24";
	}
}
