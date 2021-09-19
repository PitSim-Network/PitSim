package dev.kyro.pitsim.controllers;

import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StereoManager implements Listener {

    public static Map<Player, EntitySongPlayer> playerMusic = new HashMap<>();
    public static List<Player> toggledPlayers = new ArrayList<>();

    public static boolean hasStereo(Player player) {
        ItemStack pants = player.getInventory().getLeggings();
        if(pants == null) return false;
        if(pants.getType() == Material.AIR) return false;
        if(!pants.hasItemMeta()) return false;
        ItemMeta meta = player.getInventory().getLeggings().getItemMeta();
        if(!meta.hasLore()) return false;
        List<String> lore = player.getInventory().getLeggings().getItemMeta().getLore();

        for(String s : lore) {
            if(s.contains("Stereo")) {
                return true;
            }
        }

        return false;
    }
}