package me.kodysimpson.securitycam.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation;
import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.ActiveRecording;
import me.kodysimpson.securitycam.data.recordables.SetEquipmentRecordable;
import me.kodysimpson.securitycam.data.recordables.SwingHandRecordable;
import org.bukkit.entity.Player;

public class SecurityCamPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        Player player = (Player) event.getPlayer();
        ActiveRecording activeRecording = SecurityCam.getSecurityCam().getRecordingService().getPlayerActiveRecording(player);

        if (activeRecording == null){
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.ANIMATION){
            WrapperPlayClientAnimation packet = new WrapperPlayClientAnimation(event);
            SwingHandRecordable recordable = new SwingHandRecordable(player.getUniqueId(), packet.getHand().getId());
            SecurityCam.getSecurityCam().getRecordingService().addRecordable(activeRecording, recordable);
        }else if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE){
            SetEquipmentRecordable recordable = new SetEquipmentRecordable(player.getUniqueId(),
                    player.getEquipment().getItemInMainHand(),
                    player.getEquipment().getItemInOffHand(),
                    player.getEquipment().getHelmet(),
                    player.getEquipment().getChestplate(),
                    player.getEquipment().getLeggings(),
                    player.getEquipment().getBoots());
            SecurityCam.getSecurityCam().getRecordingService().addRecordable(activeRecording, recordable);
        }

    }
}
