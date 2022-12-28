//package dev.kyro.pitsim.commands;
//
//import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
//import dev.kyro.arcticapi.misc.AOutput;
//import dev.kyro.pitsim.controllers.StereoManager;
//import dev.kyro.pitsim.enchants.Stereo;
//import org.bukkit.Bukkit;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.Iterator;
//import java.util.Map;
//
//public class ToggleStereoCommand implements CommandExecutor {
//
//    @Override
//    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//
//        if(!(sender instanceof Player)) return false;
//        Player player = (Player) sender;
//
//        if(!StereoManager.toggledPlayers.contains(player)) {
//            AOutput.send(player, "&cStereo toggled off!");
//            StereoManager.toggledPlayers.add(player);
//
//
//            Iterator<Map.Entry<Player, EntitySongPlayer>> it = StereoManager.playerMusic.entrySet().iterator();
//            while(it.hasNext()) {
//                Map.Entry<Player, EntitySongPlayer> pair = it.next();
//                EntitySongPlayer esp = (EntitySongPlayer) pair.getValue();
//
//                esp.removePlayer(player);
//
//                Bukkit.broadcastMessage(esp.getEntity().toString());
//                Bukkit.broadcastMessage(player.toString());
//
//                if((esp.getEntity() == player)) {
//                    Bukkit.broadcastMessage("Toggled self");
//                    if(StereoManager.playerMusic.containsKey(player)) esp.destroy();
//                    Bukkit.broadcastMessage("Destroyed");
//                    StereoManager.playerMusic.remove(player);
//                }
//
//                it.remove();
//            }
//
//
//        } else {
//            AOutput.send(player, "&aStereo toggled on!");
//            StereoManager.toggledPlayers.remove(player);
//
//            Iterator<Map.Entry<Player, EntitySongPlayer>> it = StereoManager.playerMusic.entrySet().iterator();
//            while(it.hasNext()) {
//                Map.Entry<Player, EntitySongPlayer> pair = it.next();
//                EntitySongPlayer esp = (EntitySongPlayer) pair.getValue();
//
//                esp.addPlayer(player);
//
//                it.remove();
//            }
//
//            return false;
//        }
//        return false;
//    }
//}