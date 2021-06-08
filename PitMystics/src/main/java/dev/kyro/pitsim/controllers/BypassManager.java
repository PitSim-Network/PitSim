package dev.kyro.pitsim.controllers;

import me.liwk.karhu.api.data.SubCategory;
import me.liwk.karhu.api.event.KarhuEvent;
import me.liwk.karhu.api.event.impl.KarhuAlertEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;


public class BypassManager implements Listener {

    public static List<Player> bypassPlayers = new ArrayList<>();

    @EventHandler
    public void onAlert(KarhuEvent karhuEvent) {
        if(karhuEvent instanceof KarhuAlertEvent) {
            KarhuAlertEvent event = (KarhuAlertEvent) karhuEvent;

            SubCategory subcategory = event.getCheck().getSubCategory();

            for(Player player : bypassPlayers) {
                if(event.getPlayer() == player && subcategory.equals(SubCategory.SPEED)) {
                    karhuEvent.cancel();
                }

            }

        }
    }



}
