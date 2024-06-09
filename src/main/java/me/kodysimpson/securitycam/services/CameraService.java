package me.kodysimpson.securitycam.services;

import me.kodysimpson.securitycam.data.Camera;
import me.kodysimpson.securitycam.database.dao.CameraDao;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.items.ItemUtils;
import org.bson.types.ObjectId;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CameraService {

    private final CameraDao cameraDao;
    private final HashMap<Player, Camera> camerasBeingCreated = new HashMap<>();

    public CameraService(CameraDao cameraDao) {
        this.cameraDao = cameraDao;
    }

    public Camera findById(String id){
        return cameraDao.findById(new ObjectId(id));
    }

    public Camera getCameraBeingCreated(Player player){
        return camerasBeingCreated.get(player);
    }

    public List<Camera> getCamerasForPlayer(Player player){
        if (player.hasPermission("securitycam.admin")){
            return cameraDao.findAll();
        }else{
            return cameraDao.findAllByOwnerId(player.getUniqueId());
        }
    }

    public void startCreatingCamera(Player player, String name){

        if (camerasBeingCreated.containsKey(player)){
            player.sendMessage("You are already creating a camera!");
            return;
        }

        Camera camera = new Camera(name, player.getUniqueId());
        camerasBeingCreated.put(player, camera);

        //Starting the process of setting up a new camera
        player.getInventory().clear();

        player.sendMessage("You are now creating a new camera named \"" + name + "\"! Go ahead and place the corner blocks using items in your hotbar.");
        ItemStack cancel = ItemUtils.makeItem(Material.BARRIER, ColorTranslator.translateColorCodes("&c&lCancel"));
        ItemStack finish = ItemUtils.makeItem(Material.BLAZE_POWDER, ColorTranslator.translateColorCodes("&e&lFinish"));
        ItemStack corner1 = ItemUtils.makeItem(Material.RED_WOOL, ColorTranslator.translateColorCodes("&a&lCorner 1"));
        ItemStack corner2 = ItemUtils.makeItem(Material.GREEN_WOOL, ColorTranslator.translateColorCodes("&b&lCorner 2"));

        player.getInventory().setItem(0, cancel);
        player.getInventory().setItem(3, corner1);
        player.getInventory().setItem(5, corner2);
        player.getInventory().setItem(8, finish);
    }

    public void finishCreatingCamera(Player player){

        if (!camerasBeingCreated.containsKey(player)){
            player.sendMessage("You are not currently creating a camera!");
            return;
        }

        Camera camera = camerasBeingCreated.get(player);
        cameraDao.insert(camera);
        camerasBeingCreated.remove(player);

        player.getInventory().clear();
        player.sendMessage("The camera named \"" + camera.getName() + "\" has been created.");
    }

    public void cancelCreatingCamera(Player player){
        if (!camerasBeingCreated.containsKey(player)){
            player.sendMessage("You are not currently creating a camera!");
            return;
        }

        camerasBeingCreated.remove(player);
        player.getInventory().clear();
        player.sendMessage("You have cancelled the camera creation process.");
    }

}
