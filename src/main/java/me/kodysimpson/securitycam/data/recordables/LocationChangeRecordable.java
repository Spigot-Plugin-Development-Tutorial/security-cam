package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Replay;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "LocationChange")
public class LocationChangeRecordable extends Recordable {

    private Location location;
    private UUID bukkitEntityId;

    @Override
    public void replay(Replay replay, User user) throws Exception {
        var movePacket = new WrapperPlayServerEntityTeleport(replay.getSpawnedEntities().get(bukkitEntityId),
                SpigotConversionUtil.fromBukkitLocation(location),
                false);
        var headLookPacket = new WrapperPlayServerEntityHeadLook(replay.getSpawnedEntities().get(bukkitEntityId),
                location.getYaw());
        user.sendPacket(movePacket);
        user.sendPacket(headLookPacket);
    }
}
