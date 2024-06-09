package me.kodysimpson.securitycam.data;

import lombok.Data;
import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.tasks.ReplayTask;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import me.kodysimpson.simpapi.items.ItemUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class Replay {

    private Player viewer;
    private Recording recording;

    private BossBar bossBar;
    private ReplayTask replayTask;
    private HashMap<UUID, Integer> spawnedEntities = new HashMap<>();

    private Queue<List<Recordable>> recordableQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean loadingData = false;

    public Replay(Recording recording, Player viewer){
        this.recording = recording;
        this.viewer = viewer;

        viewer.getInventory().clear();

        //TODO: give them some items to control the playback
        ItemStack restart = ItemUtils.makeItem(Material.REPEATER, ColorTranslator.translateColorCodes("&c&lRestart"));
        ItemStack play = ItemUtils.makeItem(Material.GREEN_BANNER,ColorTranslator.translateColorCodes("&a&lPlay"));
        ItemStack pause = ItemUtils.makeItem(Material.YELLOW_BANNER,ColorTranslator.translateColorCodes("&e&lPause"));
        ItemStack done = ItemUtils.makeItem(Material.LIME_BANNER,ColorTranslator.translateColorCodes("&b&lDone"));

        viewer.getInventory().setItem(0, restart);
        viewer.getInventory().setItem(3, play);
        viewer.getInventory().setItem(4, pause);
        viewer.getInventory().setItem(8, done);

        viewer.sendMessage("The recording is being loaded. Use the controls in your hotbar to control the replay.");

        this.bossBar = BossBar.bossBar(
                Component.text("Replay Progress"),
                0,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );
        SecurityCam.getSecurityCam().getAdventure().player(viewer).showBossBar(bossBar);
        this.replayTask = new ReplayTask(this);
        replayTask.runTaskTimerAsynchronously(SecurityCam.getSecurityCam(), 0L, 1L);
    }

    public void restartReplay(){
        viewer.sendMessage("Restarted replay. Press play to start.");
        replayTask.restart();
    }

    public void playReplay(){
        viewer.sendMessage("Playing replay.");
        replayTask.play();
    }

    public void pauseReplay(){
        viewer.sendMessage("Paused replay.");
        replayTask.pause();
    }

    public void endReplay(){
        SecurityCam.getSecurityCam().getAdventure().player(viewer).hideBossBar(bossBar);
        this.bossBar = null;
        viewer.sendMessage("Replay ended.");
        if (replayTask != null){
            replayTask.cancel();
        }
        viewer.getInventory().clear();
    }

    public synchronized boolean tryLoadData(){
        if (loadingData){
            return false;
        }
        loadingData = true;
        return true;
    }

    public void doneLoadingData(){
        loadingData = false;
    }

}
