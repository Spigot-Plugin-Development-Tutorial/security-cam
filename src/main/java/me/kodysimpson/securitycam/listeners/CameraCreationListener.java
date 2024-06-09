package me.kodysimpson.securitycam.listeners;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.Camera;
import me.kodysimpson.securitycam.services.CameraService;
import me.kodysimpson.securitycam.utils.RegionUtils;
import me.kodysimpson.simpapi.region.Region;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CameraCreationListener implements Listener {

    private final CameraService cameraService;

    public CameraCreationListener(){
        this.cameraService = SecurityCam.getSecurityCam().getCameraService();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player player){

            Camera cameraBeingCreated = cameraService.getCameraBeingCreated(player);
            if (cameraBeingCreated == null) return;

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){

        Player player = event.getPlayer();

        Camera cameraBeingCreated = cameraService.getCameraBeingCreated(player);
        if (cameraBeingCreated == null) return;

        event.setCancelled(true);

        switch (event.getItem().getType()){
            case BARRIER -> {
                RegionUtils.killSelectorsWithTag(cameraBeingCreated.getName());
                cameraService.cancelCreatingCamera(player);
            }
            case BLAZE_POWDER -> {

                //make sure that they set both corners
                if (cameraBeingCreated.getCorner1() == null){
                    player.sendMessage("You must set corner 1 first!");
                    return;
                }

                if (cameraBeingCreated.getCorner2() == null){
                    player.sendMessage("You must set corner 2 first!");
                    return;
                }

                RegionUtils.killSelectorsWithTag(cameraBeingCreated.getName());
                cameraService.finishCreatingCamera(player);
            }
            case RED_WOOL, GREEN_WOOL -> {

                Location location = null;
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
                    location = event.getClickedBlock().getLocation();
                }else if (event.getAction() == Action.RIGHT_CLICK_AIR){
                    location = player.getLocation();
                }

                if (event.getItem().getType() == Material.RED_WOOL){
                    player.sendMessage("You have set corner 1.");
                    cameraBeingCreated.setCorner1(location);
                }else{
                    player.sendMessage("You have set corner 2.");
                    cameraBeingCreated.setCorner2(location);
                }

                if (cameraBeingCreated.getCorner1() != null && cameraBeingCreated.getCorner2() != null){
                    Region region = new Region();
                    region.setCorner1(cameraBeingCreated.getCorner1());
                    region.setCorner2(cameraBeingCreated.getCorner2());
                    RegionUtils.killSelectorsWithTag(cameraBeingCreated.getName());
                    RegionUtils.drawSelector(region, location.getWorld(), cameraBeingCreated.getName(), ChatColor.GREEN);
                    player.sendMessage("You have set both corners! Right click the blaze powder to finish.");
                }else if (cameraBeingCreated.getCorner1() != null && cameraBeingCreated.getCorner2() == null){
                    Region region = new Region();
                    region.setCorner1(cameraBeingCreated.getCorner1());
                    region.setCorner2(cameraBeingCreated.getCorner1());
                    RegionUtils.killSelectorsWithTag(cameraBeingCreated.getName());
                    RegionUtils.drawSelector(region, location.getWorld(), cameraBeingCreated.getName(), ChatColor.YELLOW);
                }else if (cameraBeingCreated.getCorner1() == null && cameraBeingCreated.getCorner2() != null){
                    Region region = new Region();
                    region.setCorner1(cameraBeingCreated.getCorner2());
                    region.setCorner2(cameraBeingCreated.getCorner2());
                    RegionUtils.killSelectorsWithTag(cameraBeingCreated.getName());
                    RegionUtils.drawSelector(region, location.getWorld(), cameraBeingCreated.getName(), ChatColor.YELLOW);
                }

            }
        }

    }

}
