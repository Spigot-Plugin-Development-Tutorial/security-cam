package me.kodysimpson.securitycam.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.tasks.CameraEntityTrackerTask;
import me.kodysimpson.securitycam.tasks.TickTracker;
import me.kodysimpson.securitycam.tasks.TrackLocationTask;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

//Data from a recording done in the past stored persistently
@Data
@NoArgsConstructor
public class Recording {

    private ObjectId id;
    private ObjectId cameraId;
    private UUID owner;
    private long startTime;
    private long endTime;
    private long startTick;
    private long endTick;
    private List<Material> originalBlocks;

    public Recording(Player owner, Camera camera) {
        this.owner = owner.getUniqueId();
        this.cameraId = camera.getId();
        this.startTime = System.currentTimeMillis();
        this.startTick = TickTracker.getCurrentTick();
    }

    public long getTickDuration(){
        return endTick - startTick;
    }

}
