package me.kodysimpson.securitycam.listeners;

import me.kodysimpson.securitycam.data.Replay;
import me.kodysimpson.securitycam.services.ReplayService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ReplayListener implements Listener {

    private final ReplayService replayService;

    public ReplayListener(ReplayService replayService) {
        this.replayService = replayService;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){

            Player player = event.getPlayer();
            Replay replay = replayService.getReplayForPlayer(player);
            if (replay == null) return;

            switch (event.getItem().getType()){
                case REPEATER -> {
                    replayService.restartReplay(replay);
                }
                case GREEN_BANNER -> {
                    replay.playReplay();
                }
                case YELLOW_BANNER -> {
                    replay.pauseReplay();
                }
                case LIME_BANNER -> {
                    replayService.stopReplay(replay);
                }
            }

            event.setCancelled(true);
        }

    }

}
