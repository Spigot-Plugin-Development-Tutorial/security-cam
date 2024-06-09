package me.kodysimpson.securitycam.services;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.ActiveRecording;
import me.kodysimpson.securitycam.data.Camera;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Recording;
import me.kodysimpson.securitycam.database.dao.RecordableDao;
import me.kodysimpson.securitycam.database.dao.RecordingDao;
import me.kodysimpson.securitycam.tasks.TickTracker;
import me.kodysimpson.simpapi.colors.ColorTranslator;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

//2 Tasks:
//1: A way to identify entities that are in the camera region
// and keep track of them
//2: A way to get all the entities that we are currently tracking
// and record their updated locations

public class RecordingService {

    //These are the recordigns CURRENTLY being recorded
    private final RecordingDao recordingDao;
    private final RecordableDao recordableDao;
    private final List<ActiveRecording> activeRecordings = new ArrayList<>();
    private final int MAX_RECORDABLES = 1000;

    public RecordingService(RecordingDao recordingDao, RecordableDao recordableDao) {
        this.recordingDao = recordingDao;
        this.recordableDao = recordableDao;
    }

    public ActiveRecording getPlayerActiveRecording(Player player){
        return activeRecordings.stream()
                .filter(recording -> recording.getRecording().getOwner().equals(player.getUniqueId()))
                .findFirst().orElse(null);
    }

    public ActiveRecording getEntityInRecording(Entity entity){
        return activeRecordings.stream()
                .filter(recording -> recording.isEntityBeingTracked(entity.getUniqueId()))
                .findFirst().orElse(null);
    }

    public void startRecording(Player player, Camera camera){
        ActiveRecording activeRecording = new ActiveRecording(player, camera);
        activeRecordings.add(activeRecording);

        recordingDao.insert(activeRecording.getRecording());

        player.sendMessage(ColorTranslator.translateColorCodes("&fYou have started recording camera: &a" + camera.getName()));
        player.sendMessage(ColorTranslator.translateColorCodes("&fYou can &cstop recording by typing &c/stoprecording"));
    }

    public void stopRecording(Player player){
        ActiveRecording activeRecording = getPlayerActiveRecording(player);
        if (activeRecording == null) {
            player.sendMessage("You are not recording!");
            return;
        }

        activeRecordings.remove(activeRecording);
        activeRecording.stopRecording();
        flushRecordables(activeRecording);
        recordingDao.update(activeRecording.getRecording());
        player.sendMessage("Recording stopped and saved. You can replay it with /replay.");
    }

    public void addRecordable(ActiveRecording activeRecording, Recordable recordable){
        Recording recording = activeRecording.getRecording();
        long currentTick = TickTracker.getCurrentTick();

        //normalize using startTick
        currentTick = currentTick - recording.getStartTick();

        recordable.setTick(currentTick);
        recordable.setRecordingId(activeRecording.getRecording().getId());
        activeRecording.getRecordablesBuffer().add(recordable);

        // see if the buffer should be flushed
        if (activeRecording.getRecordablesBuffer().size() >= MAX_RECORDABLES && activeRecording.tryStartFlushing()){
            flushRecordables(activeRecording);
        }
    }

    public void flushRecordables(ActiveRecording activeRecording){
        Bukkit.getScheduler().runTaskAsynchronously(SecurityCam.getSecurityCam(), () -> {
            //copy the buffer into a new list and clear after
            Queue<Recordable> bufferCopy;
            synchronized (this){
                bufferCopy = new LinkedList<>(activeRecording.getRecordablesBuffer());
                activeRecording.getRecordablesBuffer().clear();
            }
            List<Recordable> listToFlush = new ArrayList<>(bufferCopy);
            recordableDao.insertMany(listToFlush);
            activeRecording.endFlushing();
        });
    }

    public List<Recording> getRecordingsForCamera(Camera camera){
        return this.recordingDao.getCameraRecordings(camera.getId());
    }

    public Recording findById(String id){
        return this.recordingDao.findById(new ObjectId(id));
    }

}
