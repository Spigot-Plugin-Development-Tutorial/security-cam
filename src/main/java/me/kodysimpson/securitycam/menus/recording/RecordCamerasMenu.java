package me.kodysimpson.securitycam.menus.recording;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.Camera;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.items.ItemUtils;
import me.kodysimpson.simpapi.menu.PaginatedMenu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class RecordCamerasMenu extends PaginatedMenu {

    private final NamespacedKey cameraIdKey;

    public RecordCamerasMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        cameraIdKey = new NamespacedKey(SecurityCam.getSecurityCam(), "camera_id");
    }

    @Override
    public List<ItemStack> dataToItems() {
        return SecurityCam.getSecurityCam().getCameraService().getCamerasForPlayer(player)
                .stream()
                .map(camera -> {
                    ItemStack item = ItemUtils.makeItem(Material.ENDER_EYE, camera.getName()," ",
                            "&b* &#843897Left click to record",
                            "&b* &#843897Right click to teleport");
                    ItemMeta meta = item.getItemMeta();
                    meta.getPersistentDataContainer().set(cameraIdKey, PersistentDataType.STRING, camera.getId().toString());
                    item.setItemMeta(meta);
                    return item;
                }).toList();
    }

    @Override
    public @Nullable HashMap<Integer, ItemStack> getCustomMenuBorderItems() {
        return null;
    }

    @Override
    public String getMenuName() {
        return "Choose a Camera to Record";
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
    public void handleMenu(InventoryClickEvent event) throws MenuManagerNotSetupException, MenuManagerException {

        switch (event.getCurrentItem().getType()){
            case BARRIER -> player.closeInventory();
            case ENDER_EYE -> {

                String cameraId = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(cameraIdKey, PersistentDataType.STRING);
                Camera camera = SecurityCam.getSecurityCam().getCameraService().findById(cameraId);

                if (camera == null){
                    player.sendMessage("Unexpected error, unable to grab camera. Try again later.");
                    return;
                }

                if (event.isLeftClick()){
                    SecurityCam.getSecurityCam().getRecordingService().startRecording(player, camera);
                    player.closeInventory();
                }else if (event.isRightClick()){
                    //TODO: tp them to the cam
                }

            }
        }

    }
}
