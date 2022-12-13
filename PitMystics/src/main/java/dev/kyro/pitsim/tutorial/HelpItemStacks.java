package dev.kyro.pitsim.tutorial;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class HelpItemStacks {

	public static ItemStack BASE_ITEMSTACK;

	static {
		BASE_ITEMSTACK = getCustomHead("badc048a7ce78f7dad72a07da27d85c0916881e5522eeed1e3daf217a38c1a");
	}


	public static ItemStack getPerksItemStack() {
		return new AItemStackBuilder(BASE_ITEMSTACK.clone())
				.setName("&ePerks, Killstreaks, and Megastreaks")
				.setLore(new ALoreBuilder(
						"&8&m----------------------------",
						"&ePerks &7are abilities that passively", "&7help you in combat",
						"&eKillstreaks &7are abilities that", "&7activate when streaking",
						"&eMegastreaks &7shape the style of", "&7your streak",
						"&8&m----------------------------"
				))
				.getItemStack();
	}

	public static ItemStack getKitsItemStack() {
		return new AItemStackBuilder(BASE_ITEMSTACK.clone())
				.setName("&dMystic Item Kits")
				.setLore(new ALoreBuilder(
						"&8&m----------------------------",
						"&dMystic Items &7are the core of",
						"&6&lPit&e&lSim&7 combat. &7Here you can",
						"&7get kits specialized &7for &cPvP&7 and",
						"&bStreaking&7. You can experiment with",
						"&7other &eEnchant Combinations &7in the",
						"&dMystic Well",
						"&8&m----------------------------"
				))
				.getItemStack();
	}

	public static ItemStack getPrestigeItemStack() {
		return new AItemStackBuilder(BASE_ITEMSTACK.clone())
				.setName("&ePrestige and Renown")
				.setLore(new ALoreBuilder(
						"&8&m----------------------------",
						"&7Upon reaching level &f[&b&l120&f]&7,",
						"&7you can &ePrestige. &7This will reset",
						"&7your level to &f[&71&f]&7, &7and remove",
						"&7your &6gold. &7In exchange, you gain &eRenown&7,",
						"&7which can be used to purchase powerful",
						"&7upgrades from the &eRenown Shop",
						"&8&m----------------------------"
				))
				.getItemStack();
	}

	public static ItemStack getKeeperItemStack() {
		return new AItemStackBuilder(BASE_ITEMSTACK.clone())
				.setName("&ePrestige and Renown")
				.setLore(new ALoreBuilder(
						"&8&m----------------------------",
						"&7Do you feel like there are too many",
						"&7players in the middle? &7Here you can go",
						"&7to other PitSim &2Lobbies &7if they're open",
						"&8&m----------------------------"
				))
				.getItemStack();
	}




	public static ItemStack getCustomHead(String url) {

		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		Field field = null;

		assert skullMeta != null;

		if (url.length() < 16) {

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

		} catch (Exception e) {
			e.printStackTrace();
		}

		skull.setItemMeta(skullMeta);

		return skull; //Finally, you have the custom head!

	}

}
