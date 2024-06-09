package me.kodysimpson.securitycam.services;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.kodysimpson.securitycam.data.ActiveRecording;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Recording;
import me.kodysimpson.securitycam.data.Replay;
import me.kodysimpson.securitycam.data.recordables.DespawnEntityRecordable;
import me.kodysimpson.securitycam.data.recordables.SpawnEntityRecordable;
import me.kodysimpson.securitycam.database.dao.CameraDao;
import me.kodysimpson.securitycam.database.dao.RecordableDao;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplayService {

    private final RecordableDao recordableDao;
    private final CameraDao cameraDao;
    private final Set<Replay> replays = new HashSet<Replay>();

    public ReplayService(RecordableDao recordableDao, CameraDao cameraDao) {
        this.recordableDao = recordableDao;
        this.cameraDao = cameraDao;
    }

    public Replay getReplayForPlayer(Player player){
        return replays.stream()
                .filter(replay -> replay.getViewer().equals(player))
                .findFirst().orElse(null);
    }

    public void replayRecording(Recording recording, Player player){

        if (getReplayForPlayer(player) != null){
            player.sendMessage("You are already watching a replay!");
            return;
        }

        Replay replay = new Replay(recording, player);
        placeOriginalBlocks(replay.getRecording(), replay.getViewer());

        loadRecordables(replay, 0);

        replays.add(replay);
    }

    public void loadRecordables(Replay replay, long startTick){
        //make sure we are not already loading more data
        if (!replay.tryLoadData()) return;

        System.out.println("Trying to get more data for: " + startTick);

        try {
            long endTick = startTick + 100;
            if (endTick > replay.getRecording().getTickDuration()){
                endTick = replay.getRecording().getTickDuration();
            }

            System.out.println("End tick: " + endTick);

            Recording recording = replay.getRecording();
            List<Recordable> recordables = recordableDao.findByRecordingIdAndTickBetween(recording.getId(), startTick, endTick);
            List<List<Recordable>> groupedRecordables = new ArrayList<>();

            //Take the list of recordables which are sorted ascending by tick
            //and group them by tick. Make a new list for each tick and add the recordables.
            //If there are no recordables for a tick, add an empty list.
            long currentTick = 0;
            for (long tick = startTick; tick <= endTick; tick++) {
                List<Recordable> tickRecordables = new ArrayList<>();
                while (currentTick < recordables.size() && recordables.get((int) currentTick).getTick() == tick) {
                    tickRecordables.add(recordables.get((int) currentTick));
                    currentTick++;
                }
                groupedRecordables.add(tickRecordables);
            }

            groupedRecordables.forEach(replay.getRecordableQueue()::add);
        }finally {
            replay.doneLoadingData();
        }
    }

    public void restartReplay(Replay replay){
        placeOriginalBlocks(replay.getRecording(), replay.getViewer());

        replay.getSpawnedEntities().forEach((uuid, entityId) -> {
            WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(entityId);
            PacketEvents.getAPI().getPlayerManager().getUser(replay.getViewer()).sendPacket(destroyEntities);
        });
        replay.getSpawnedEntities().clear();
        replay.restartReplay();
        loadRecordables(replay, 0);
    }

    public void stopReplay(Replay replay){
        //stop the replay task
        replay.endReplay();

        //despawn all the active entities
        replay.getSpawnedEntities().forEach((uuid, entityId) -> {
            WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(entityId);
            PacketEvents.getAPI().getPlayerManager().getUser(replay.getViewer()).sendPacket(destroyEntities);
        });
        replay.getSpawnedEntities().clear();

        placeActualBlocks(replay.getRecording(), replay.getViewer());

        replays.remove(replay);
    }

    public void stopAllReplays(){
        replays.forEach(this::stopReplay);
        this.replays.clear();
    }

    private void placeOriginalBlocks(Recording recording, Player viewer){

        List<Material> originalBlocks = recording.getOriginalBlocks();
        var camera = cameraDao.findById(recording.getCameraId());
        var corner1 = camera.getCorner1();
        var corner2 = camera.getCorner2();

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }

        int startX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int endX = Math.max(corner1.getBlockX(), corner2.getBlockX());

        int startY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int endY = Math.max(corner1.getBlockY(), corner2.getBlockY());

        int startZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int endZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        int index = 0;
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {

                    Material material = originalBlocks.get(index);
                    //use a packet to show the block changing to this material

                    Vector3i position = new Vector3i(x, y, z);
                    var stateType = StateTypes.getByName("minecraft:" + material.name().toLowerCase());
                    var wrappedBlockState = WrappedBlockState.getDefaultState(stateType);
                    WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange(position, wrappedBlockState.getGlobalId());
                    PacketEvents.getAPI().getPlayerManager().getUser(viewer).sendPacket(blockChangePacket);

                    index++;
                }
            }
        }

    }

    private void placeActualBlocks(Recording recording, Player viewer){

        var camera = cameraDao.findById(recording.getCameraId());
        var corner1 = camera.getCorner1();
        var corner2 = camera.getCorner2();
        World world = corner1.getWorld();

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }

        int startX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int endX = Math.max(corner1.getBlockX(), corner2.getBlockX());

        int startY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int endY = Math.max(corner1.getBlockY(), corner2.getBlockY());

        int startZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int endZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    var actualBlockState = SpigotConversionUtil.fromBukkitBlockData(block.getBlockData());
                    Vector3i position = new Vector3i(x, y, z);
                    WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange(position, actualBlockState.getGlobalId());
                    PacketEvents.getAPI().getPlayerManager().getUser(viewer).sendPacket(blockChangePacket);
                }
            }
        }

    }

}
