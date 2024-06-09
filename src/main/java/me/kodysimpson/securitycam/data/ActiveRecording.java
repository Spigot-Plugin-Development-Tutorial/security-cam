package me.kodysimpson.securitycam.data;

import lombok.Data;
import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.tasks.CameraEntityTrackerTask;
import me.kodysimpson.securitycam.tasks.EquipmentTrackTask;
import me.kodysimpson.securitycam.tasks.TickTracker;
import me.kodysimpson.securitycam.tasks.TrackLocationTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

//A recording that is being recorded right now!!!!
@Data
public class ActiveRecording {

    private Recording recording;
    private Camera camera;
    private final Queue<UUID> recordableEntities;
    private BukkitTask trackLocationTask;
    private BukkitTask scanEntitiesTask;
    private BukkitTask trackEquipmentTask;
    private Queue<Recordable> recordablesBuffer = new ConcurrentLinkedQueue<>();
    private volatile boolean isFlushing = false;

    public ActiveRecording(Player owner, Camera camera) {
        this.recording = new Recording(owner, camera);
        this.camera = camera;
        this.recording.setOriginalBlocks(this.camera.getMaterialsInRegion());

        this.recordableEntities = new ConcurrentLinkedQueue<>();
        this.scanEntitiesTask = Bukkit.getScheduler().runTaskTimer(SecurityCam.getSecurityCam(), new CameraEntityTrackerTask(this), 0L, 20L);
        this.trackLocationTask = Bukkit.getScheduler().runTaskTimer(SecurityCam.getSecurityCam(), new TrackLocationTask(this), 0L, 1L);
        this.trackEquipmentTask = Bukkit.getScheduler().runTaskTimer(SecurityCam.getSecurityCam(), new EquipmentTrackTask(this), 0L, 20L);
    }

    public void stopRecording(){
        this.recording.setEndTime(System.currentTimeMillis());
        this.recording.setEndTick(TickTracker.getCurrentTick());
        this.scanEntitiesTask.cancel();
        this.trackLocationTask.cancel();
        System.out.println("tasks cancelled");
    }

    public boolean isEntityBeingTracked(UUID bukkitEntityId){
        synchronized (recordableEntities) {
            return recordableEntities.stream().anyMatch(recordableEntity -> recordableEntity.equals(bukkitEntityId));
        }
    }

    public boolean addEntityIfNotAlreadyBeingTracked(UUID bukkitEntityId){
        synchronized (recordableEntities) {
            if (recordableEntities.stream().noneMatch(re -> re.equals(bukkitEntityId))){
                recordableEntities.add(bukkitEntityId);
                return true;
            }
            return false;
        }
    }

    public void removeEntityFromTracking(UUID bukkitEntityId) {
        recordableEntities.removeIf(recordableEntity -> recordableEntity.equals(bukkitEntityId));
    }

    public synchronized boolean tryStartFlushing(){
        if (!isFlushing){
            isFlushing = true;
            return true;
        }
        return false;
    }

    public void endFlushing(){
        isFlushing = false;
    }

}
