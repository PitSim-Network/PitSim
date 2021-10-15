package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class HelmetAbilityPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
    GoldenHelmet goldenHelmet = GoldenHelmet.getHelmet(player.getItemInHand(), player);
    public HelmetGUI helmetGUI;
    public HelmetAbilityPanel(AGUI gui) {
        super(gui);
        helmetGUI = (HelmetGUI) gui;

    }

    @Override
    public String getName() {
        return "Choose an Ability";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if(event.getClickedInventory().getHolder() == this) {

            ItemStack helm = getHelm();

            if(slot == 9) {
                goldenHelmet.setAbility(null);
            }

            for(HelmetAbility helmetAbility : HelmetAbility.helmetAbilities) {
                if(slot != helmetAbility.slot) continue;

                GoldenHelmet goldenHelmet = GoldenHelmet.getHelmet(helm, player);

                assert goldenHelmet != null;
                if(goldenHelmet.ability.refName.equals(helmetAbility.refName)) {
                    AOutput.error(player, "&aYou already have that ability selected!");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1F, 1F);
                    return;
                }

                goldenHelmet.setAbility(helmetAbility);

            }
            openPreviousGUI();
        }
        updateInventory();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        for(HelmetAbility helmetAbility : HelmetAbility.helmetAbilities) {
            AItemStackBuilder builder = new AItemStackBuilder(helmetAbility.getDisplayItem());
            ALoreBuilder loreBuilder = new ALoreBuilder();

            GoldenHelmet goldenHelmet = GoldenHelmet.getHelmet(getHelm(), player);
            assert goldenHelmet != null;
            if(!goldenHelmet.ability.refName.equals(helmetAbility.refName)) {
                builder.setName("&e" + helmetAbility.refName);
                loreBuilder.addLore("", "&eCLick to select!");
            } else  {
                builder.setName("&a" + helmetAbility.refName);
                loreBuilder.addLore("", "&aCLick to select!");
                builder.addEnchantGlint(true);
            }
            builder.setLore(loreBuilder);
            getInventory().setItem(helmetAbility.slot, builder.getItemStack());
        }

        AItemStackBuilder builder = new AItemStackBuilder(Material.BARRIER);
        builder.setName("&cNone");
        ALoreBuilder loreBuilder = new ALoreBuilder("", "&cClick to remove ability!");
        builder.setLore(loreBuilder);
        getInventory().setItem(9, builder.getItemStack());

    }


    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    public ItemStack getHelm() {
        int helmSlot = GoldenHelmet.INSTANCE.getInventorySlot(player);
        if(helmSlot == -1) return null;

        ItemStack helm = null;
        if(helmSlot == -2) helm = player.getInventory().getHelmet();
        else helm = player.getInventory().getItem(helmSlot);
        return helm;
    }

}


