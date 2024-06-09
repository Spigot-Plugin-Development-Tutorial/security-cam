package me.kodysimpson.securitycam.menus.replay;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.Camera;
import me.kodysimpson.securitycam.menus.PMUData;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import me.kodysimpson.simpapi.menu.PaginatedMenu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class ReplayCamerasMenu extends PaginatedMenu {

    public ReplayCamerasMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public List<ItemStack> dataToItems() {
        return SecurityCam.getSecurityCam().getCameraService().getCamerasForPlayer(playerMenuUtility.getOwner()).stream().map(camera -> {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(camera.getOwner());
            ItemStack item = makeItem(Material.ENDER_EYE, camera.getName(), " ",
                    "&eOwner: &f" + owner.getName(),
                    "&b* &#843897Right click to teleport");
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(SecurityCam.getSecurityCam(), "camera_id"), PersistentDataType.STRING, camera.getId().toString());
            item.setItemMeta(itemMeta);
            return item;
        }).toList();
    }

    @Override
    public @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems() {
        return null;
    }

    @Override
    public String getMenuName() {
        return "Select a Camera";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {

        switch (e.getCurrentItem().getType()){
            case BARRIER -> player.closeInventory();
            case ENDER_EYE -> {
                String cameraId = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SecurityCam.getSecurityCam(), "camera_id"), PersistentDataType.STRING);
                Camera camera = SecurityCam.getSecurityCam().getCameraService().findById(cameraId);

                if (camera == null){
                    player.sendMessage("Unexpected error, unable to grab camera. Try again later.");
                    return;
                }

                if (e.isLeftClick()){
                    playerMenuUtility.setData(PMUData.CAMERA, camera);
                    MenuManager.openMenu(RecordingsMenu.class, player);
                }else if (e.isRightClick()){
                    SecurityCam.getSecurityCam().getCameraService().teleportToCamera(camera, player);
                    player.closeInventory();
                }
            }
        }

    }
}
