package me.kodysimpson.securitycam.commands;

import me.kodysimpson.securitycam.menus.replay.ReplayCamerasMenu;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player){

            if (!player.hasPermission("securitycam.replay")){
                player.sendMessage("You do not have permission to use this command!");
                return true;
            }

            //open the replay menu
            try {
                MenuManager.openMenu(ReplayCamerasMenu.class, player);
            } catch (MenuManagerException | MenuManagerNotSetupException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
