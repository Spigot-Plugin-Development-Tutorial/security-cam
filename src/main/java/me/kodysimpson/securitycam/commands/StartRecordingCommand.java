package me.kodysimpson.securitycam.commands;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.ActiveRecording;
import me.kodysimpson.securitycam.data.Recording;
import me.kodysimpson.securitycam.menus.recording.RecordCamerasMenu;
import me.kodysimpson.securitycam.services.RecordingService;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartRecordingCommand implements CommandExecutor {

    private final RecordingService recordingService;

    public StartRecordingCommand() {
        this.recordingService = SecurityCam.getSecurityCam().getRecordingService();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player){

            if (!player.hasPermission("securitycam.record")){
                player.sendMessage(ColorTranslator.translateColorCodes("&cYou do not have permission to use this command!"));
                return true;
            }

            ActiveRecording activeRecording = recordingService.getPlayerActiveRecording(player);
            if (activeRecording != null){
                player.sendMessage("You are already recording a camera. You cannot record two at once.");
                return true;
            }

            //open the menu so they can select a cam to record
            try {
                MenuManager.openMenu(RecordCamerasMenu.class, player);
            } catch (MenuManagerException | MenuManagerNotSetupException e) {
                player.sendMessage("An error occurred while trying to open the menu!");
                e.printStackTrace();
            }

        }else{
            sender.sendMessage("You must be a player to use this command!");
        }

        return true;
    }
}
