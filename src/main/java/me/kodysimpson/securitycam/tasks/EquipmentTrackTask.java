package me.kodysimpson.securitycam.tasks;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.ActiveRecording;
import me.kodysimpson.securitycam.data.Recording;
import me.kodysimpson.securitycam.data.recordables.SetEquipmentRecordable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class EquipmentTrackTask implements Runnable{

    private final ActiveRecording recording;

    public EquipmentTrackTask(ActiveRecording recording) {
        this.recording = recording;
    }

    @Override
    public void run() {

        var recordableEntities = recording.getRecordableEntities();
        synchronized (recordableEntities) {
            recordableEntities.forEach(recordableEntity -> {
                Entity entity = Bukkit.getEntity(recordableEntity);
                if (entity != null){
                    if (entity instanceof LivingEntity livingEntity && livingEntity.getEquipment() != null){
                        SetEquipmentRecordable recordable = new SetEquipmentRecordable(livingEntity.getUniqueId(),
                                livingEntity.getEquipment().getItemInMainHand(),
                                livingEntity.getEquipment().getItemInOffHand(),
                                livingEntity.getEquipment().getHelmet(),
                                livingEntity.getEquipment().getChestplate(),
                                livingEntity.getEquipment().getLeggings(),
                                livingEntity.getEquipment().getBoots());
                        SecurityCam.getSecurityCam().getRecordingService().addRecordable(recording, recordable);
                    }
                }
            });
        }

    }
}
