package me.kodysimpson.securitycam.commands;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.services.CameraService;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateCameraCommand implements CommandExecutor {

    private final CameraService cameraService;

    public CreateCameraCommand() {
        this.cameraService = SecurityCam.getSecurityCam().getCameraService();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player){

            if (!player.hasPermission("securitycam.createcamera")){
                player.sendMessage(ColorTranslator.translateColorCodes("&cYou do not have permission to use this command!"));
                return true;
            }

            //get the name of the camera
            if (args.length == 0){
                player.sendMessage("Please provide a name for the camera!");
                return true;
            }

            String cameraName = args[0];
            cameraService.startCreatingCamera(player, cameraName);

        }else{
            System.out.println("You are not a player. go away.");
        }

        return true;
    }
}
