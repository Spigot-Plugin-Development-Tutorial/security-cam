package me.kodysimpson.securitycam.listeners;

import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.ActiveRecording;
import me.kodysimpson.securitycam.data.recordables.*;
import me.kodysimpson.securitycam.services.RecordingService;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class BukkitListener implements Listener {

    private final RecordingService recordingService;

    public BukkitListener() {
        this.recordingService = SecurityCam.getSecurityCam().getRecordingService();
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        ActiveRecording activeRecording = recordingService.getEntityInRecording(player);
        if (activeRecording != null){
            SneakRecordable recordable = new SneakRecordable(player.getUniqueId(), event.isSneaking());
            recordingService.addRecordable(activeRecording, recordable);
        }
    }

    @EventHandler
    public void onPlayerSprint(PlayerToggleSprintEvent event){
        Player player = event.getPlayer();
        ActiveRecording activeRecording = recordingService.getEntityInRecording(player);
        if (activeRecording != null){
            SprintRecordable recordable = new SprintRecordable(player.getUniqueId(), event.isSprinting());
            recordingService.addRecordable(activeRecording, recordable);
        }
    }

    @EventHandler
    public void onEntityHurt(EntityDamageEvent event){

        ActiveRecording recording = recordingService.getEntityInRecording(event.getEntity());
        if (recording != null){
            EntityHurtRecordable entityHurtRecordable = new EntityHurtRecordable(event.getEntity().getUniqueId());
            recordingService.addRecordable(recording, entityHurtRecordable);
        }

    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){

        ActiveRecording recording = recordingService.getEntityInRecording(event.getPlayer());
        if (recording != null){

            System.out.println("Dropped: " + event.getItemDrop().getItemStack().getType());
            System.out.println("Amount: " + event.getItemDrop().getItemStack().getAmount());

            recording.addEntityIfNotAlreadyBeingTracked(event.getItemDrop().getUniqueId());
            ItemDropRecordable itemDropRecordable = new ItemDropRecordable(event.getItemDrop().getUniqueId(),
                    event.getItemDrop().getLocation(),
                    event.getItemDrop().getItemStack());
            recordingService.addRecordable(recording, itemDropRecordable);
        }

    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event){
        ActiveRecording recording = recordingService.getEntityInRecording(event.getEntity());
        if (recording != null){
            ItemPickupRecordable itemDropRecordable = new ItemPickupRecordable(event.getEntity().getUniqueId(),
                    event.getItem().getUniqueId());
            recordingService.addRecordable(recording, itemDropRecordable);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){

        ActiveRecording recording = recordingService.getEntityInRecording(event.getPlayer());
        if (recording != null){
            BlockPlaceRecordable recordable = new BlockPlaceRecordable(event.getBlock().getType(), event.getBlock().getLocation());
            recordingService.addRecordable(recording, recordable);
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        ActiveRecording recording = recordingService.getEntityInRecording(event.getPlayer());
        if (recording != null){
            BlockBreakRecordable recordable = new BlockBreakRecordable(event.getBlock().getLocation());
            recordingService.addRecordable(recording, recordable);
        }

    }

}
