package me.kodysimpson.securitycam.menus.replay;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.Camera;
import me.kodysimpson.securitycam.data.Recording;
import me.kodysimpson.securitycam.menus.PMUData;
import me.kodysimpson.securitycam.services.RecordingService;
import me.kodysimpson.securitycam.services.ReplayService;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.PaginatedMenu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RecordingsMenu extends PaginatedMenu {

    private final RecordingService recordingService;
    private final ReplayService replayService;
    private final Camera camera;

    public RecordingsMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        this.recordingService = SecurityCam.getSecurityCam().getRecordingService();
        this.replayService = SecurityCam.getSecurityCam().getReplayService();
        this.camera = playerMenuUtility.getData(PMUData.CAMERA, Camera.class);
    }

    @Override
    public List<ItemStack> dataToItems() {
        return this.recordingService.getRecordingsForCamera(camera).stream().map(recording -> {

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
            String startDate = formatter.format(new Date(recording.getStartTime()));
            String endDate = formatter.format(new Date(recording.getEndTime()));

            //calculate the duration as a string of HH:MM:SS
            var duration = (recording.getEndTime() - recording.getStartTime()) / 1000;
            var hours = duration / 3600;
            var minutes = (duration % 3600) / 60;
            var seconds = duration % 60;
            var durationString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            ItemStack item = makeItem(Material.PAPER, recording.getId().toString(),
                    "&aStart&f: " + startDate,
                    "&aEnd&f: " + endDate,
                    "&aDuration&f: " + durationString, "",
                    "&eClick to replay");

            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.getPersistentDataContainer().set(
                    new NamespacedKey(SecurityCam.getSecurityCam(), "recordingId"),
                    PersistentDataType.STRING, recording.getId().toString());
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
        return "Recordings from: " + camera.getName();
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

        if (event.getCurrentItem().getType() == Material.PAPER){

            String recordingId = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SecurityCam.getSecurityCam(), "recordingId"), PersistentDataType.STRING);

            Recording recording = recordingService.findById(recordingId);

            if (recording == null){
                player.sendMessage("An error ocurred while trying to get the recording. Try again later.");
                return;
            }

            replayService.replayRecording(recording, player);
            player.closeInventory();
        }

    }
}
