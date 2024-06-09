package me.kodysimpson.securitycam.tasks;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.ActiveRecording;
import me.kodysimpson.securitycam.data.recordables.SpawnEntityRecordable;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class CameraEntityTrackerTask implements Runnable {

    private final ActiveRecording activeRecording;

    public CameraEntityTrackerTask(ActiveRecording recording) {
        this.activeRecording = recording;
    }

    @Override
    public void run() {

        Location corner1 = activeRecording.getCamera().getCorner1();
        Location corner2 = activeRecording.getCamera().getCorner2();

        var loadedChunks = corner1.getWorld().getLoadedChunks();
        List<Chunk> chunksInRegion = new ArrayList<>();
        // Calculate chunk coordinates outside of the loop
        int chunkX1 = corner1.getBlockX() >> 4;
        int chunkZ1 = corner1.getBlockZ() >> 4;
        int chunkX2 = corner2.getBlockX() >> 4;
        int chunkZ2 = corner2.getBlockZ() >> 4;

        // Ensure the minimum and maximum are correctly ordered
        int minX = Math.min(chunkX1, chunkX2);
        int maxX = Math.max(chunkX1, chunkX2);
        int minZ = Math.min(chunkZ1, chunkZ2);
        int maxZ = Math.max(chunkZ1, chunkZ2);

        for (var chunk : loadedChunks) {
            if (chunk.getX() >= minX && chunk.getX() <= maxX &&
                    chunk.getZ() >= minZ && chunk.getZ() <= maxZ) {
                chunksInRegion.add(chunk);
            }
        }

        //get all the entities in the chunks and see if we are tracking them
        for (var chunk : chunksInRegion){
            var entities = chunk.getEntities();
            for (var entity : entities){
                //check to see if the entity is in the region
                if (!activeRecording.getCamera().isInRegion(entity.getLocation())){
                    continue;
                }

                if (activeRecording.addEntityIfNotAlreadyBeingTracked(entity.getUniqueId())){
                    System.out.println("ADDED ENTITY TO TRACKING: " + entity.getType());
                    SpawnEntityRecordable spawnEntityRecordable = new SpawnEntityRecordable(entity);
                    SecurityCam.getSecurityCam().getRecordingService().addRecordable(activeRecording, spawnEntityRecordable);
                }
            }
        }

    }
}
