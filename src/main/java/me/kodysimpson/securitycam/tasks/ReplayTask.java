package me.kodysimpson.securitycam.tasks;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Replay;
import me.kodysimpson.securitycam.services.ReplayService;
import org.bukkit.scheduler.BukkitRunnable;

public class ReplayTask extends BukkitRunnable {

    private final Replay replay;
    private final ReplayService replayService;
    private long currentTick = 0;
    private final User user;
    private boolean paused = true;

    public ReplayTask(Replay replay) {
        this.replay = replay;
        this.replayService = SecurityCam.getSecurityCam().getReplayService();
        this.user = PacketEvents.getAPI().getPlayerManager().getUser(replay.getViewer());
    }

    @Override
    public void run() {

        if (paused){
            return;
        }

        if (currentTick >= replay.getRecording().getTickDuration()){
            this.replay.getViewer().sendMessage("Replay has ended.");
            replayService.stopReplay(replay);
        }

        if (replay.getRecordableQueue().size() < 100){
            System.out.println("Current tick: " + currentTick);
            long nextTick = currentTick + replay.getRecordableQueue().size();
            replayService.loadRecordables(replay, nextTick);
        }

        if (replay.getRecordableQueue().isEmpty()){
            System.out.println("Buffering???");
            return;
        }

        //update bossbar
        replay.getBossBar().progress((float) currentTick / replay.getRecording().getTickDuration());

        //get all of the recordables for the current tick
        var tickRecordables = replay.getRecordableQueue().poll();

        //System.out.println("Tick recordables: " + (tickRecordables != null ? tickRecordables.size() : "null") + " for tick: " + currentTick);

        try{
            if (tickRecordables == null){
                return;
            }

            for (Recordable recordable : tickRecordables){
                //figure out how to replay a recordable
                recordable.replay(replay, user);
            }
        }catch (Exception ex){
            System.out.println("Tick: " + currentTick);
            System.out.println("Error processing recordable from the Replay Task: " + ex.getMessage());
            ex.printStackTrace();
        }finally {
            currentTick++;
        }
    }

    public void restart(){
        this.paused = true;
        currentTick = 0L;
    }

    public void play(){
        this.paused = false;
    }

    public void pause(){
        this.paused = true;
    }

}
