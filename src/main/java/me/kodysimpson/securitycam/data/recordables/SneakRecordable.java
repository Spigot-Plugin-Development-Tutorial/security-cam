package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Replay;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "SneakEntity")
public class SneakRecordable extends Recordable {

    private UUID bukkitEntityId;
    private boolean isSneaking;

    @Override
    public void replay(Replay replay, User user) throws Exception {
        var entityId = replay.getSpawnedEntities().get(bukkitEntityId);
        List<EntityData> entityDataList = new ArrayList<>();
        entityDataList.add(new EntityData(6, EntityDataTypes.ENTITY_POSE, (isSneaking ? EntityPose.CROUCHING : EntityPose.STANDING)));
        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(entityId, entityDataList);
        user.sendPacket(metadataPacket);
    }
}
