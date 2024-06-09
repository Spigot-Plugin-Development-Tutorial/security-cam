package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCollectItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Replay;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "ItemPickup")
public class ItemPickupRecordable extends Recordable {

    private UUID collectorBukkitEntityId;
    private UUID itemBukkitEntityId;

    @Override
    public void replay(Replay replay, User user) throws Exception {

        var collectorEntityId = replay.getSpawnedEntities().get(collectorBukkitEntityId);
        var itemEntityId = replay.getSpawnedEntities().get(itemBukkitEntityId);

        WrapperPlayServerCollectItem packet = new WrapperPlayServerCollectItem(itemEntityId, collectorEntityId, 1);
        user.sendPacket(packet);
    }
}
