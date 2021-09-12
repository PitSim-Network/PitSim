//package dev.kyro.pitsim.controllers;
//
//import me.liwk.karhu.api.data.SubCategory;
//import me.liwk.karhu.api.event.KarhuEvent;
//import me.liwk.karhu.api.event.KarhuListener;
//import me.liwk.karhu.api.event.impl.KarhuAlertEvent;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//
//    public static List<Player> bypassPCTS = new ArrayList<>();
//    public static List<Player> bypassPullbow = new ArrayList<>();
//    public static List<Player> bypassExplosive = new ArrayList<>();
//    public static List<Player> bypassPunch = new ArrayList<>();
//    public static List<Player> oofBypass = new ArrayList<>();
//    public static List<Player> bypassAll = new ArrayList<>();
//
//
//
//
//
//            SubCategory subcategory = event.getCheck().getSubCategory();
//
//            for(Player player : bypassPCTS) {
//                if(event.getPlayer() == player && subcategory.equals(SubCategory.SPEED)) {
//                    karhuEvent.cancel();
//                }
//
//            }
//
//            for(Player player : bypassPullbow) {
//                if(event.getPlayer() == player && subcategory.equals(SubCategory.SPEED)) {
//                    karhuEvent.cancel();
//                }
//
//            }
//
//            for(Player player : bypassExplosive) {
//                if(event.getPlayer() == player && subcategory.equals(SubCategory.SPEED)) {
//                    karhuEvent.cancel();
//                }
//
//            }
//
//            for(Player player : bypassAll) {
//                if(event.getPlayer() == player) {
//                    karhuEvent.cancel();
//                }
//
//            }
//
//        }
//    }
//
//
//
//}
