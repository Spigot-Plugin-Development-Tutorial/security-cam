package me.kodysimpson.securitycam.tasks;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.ActiveRecording;
import me.kodysimpson.securitycam.data.Camera;
import me.kodysimpson.securitycam.data.Recording;
import me.kodysimpson.securitycam.data.recordables.DespawnEntityRecordable;
import me.kodysimpson.securitycam.data.recordables.LocationChangeRecordable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class TrackLocationTask implements Runnable{

    private final ActiveRecording activeRecording;
    private final Camera camera;

    public TrackLocationTask(ActiveRecording activeRecording){
        this.activeRecording = activeRecording;
        this.camera = activeRecording.getCamera();
    }

    @Override
    public void run() {

        var recordableEntities = activeRecording.getRecordableEntities();
        synchronized (recordableEntities){
            recordableEntities.forEach(recordableEntity -> {
                Entity entity = Bukkit.getEntity(recordableEntity);

                if (entity != null){

                    if (camera.isInRegion(entity.getLocation())){
                        var recordable = new LocationChangeRecordable(entity.getLocation(), entity.getUniqueId());
                        SecurityCam.getSecurityCam().getRecordingService().addRecordable(activeRecording, recordable);
                    }else{
                        System.out.println("REMOVING THE ENTITY FROM TRACKING");
                        activeRecording.removeEntityFromTracking(recordableEntity);
                        DespawnEntityRecordable despawnEntityRecordable = new DespawnEntityRecordable(recordableEntity);
                        SecurityCam.getSecurityCam().getRecordingService().addRecordable(activeRecording, despawnEntityRecordable);
                    }

                }else{
                    System.out.println("REMOVING THE DESPAWNED ENTITY FROM TRACKING");
                    activeRecording.removeEntityFromTracking(recordableEntity);
                    DespawnEntityRecordable despawnEntityRecordable = new DespawnEntityRecordable(recordableEntity);
                    SecurityCam.getSecurityCam().getRecordingService().addRecordable(activeRecording, despawnEntityRecordable);
                }
            });
        }

    }
}
