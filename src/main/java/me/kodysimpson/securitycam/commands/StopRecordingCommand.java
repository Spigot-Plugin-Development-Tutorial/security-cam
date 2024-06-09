package me.kodysimpson.securitycam.commands;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.services.RecordingService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StopRecordingCommand implements CommandExecutor {

    private final RecordingService recordingService;

    public StopRecordingCommand() {
        this.recordingService = SecurityCam.getSecurityCam().getRecordingService();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player){

            if (!player.hasPermission("securitycam.record")){
                player.sendMessage("You do not have permission to use this command!");
                return true;
            }

            recordingService.stopRecording(player);
        }else{
            sender.sendMessage("You must be a player to use this command!");
        }

        return true;
    }
}
