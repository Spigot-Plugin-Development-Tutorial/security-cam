package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHurtAnimation;
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
@BsonDiscriminator(key = "type", value = "EntityHurt")
public class EntityHurtRecordable extends Recordable {

    private UUID bukkitEntityId;

    @Override
    public void replay(Replay replay, User user) throws Exception {
        var entityId = replay.getSpawnedEntities().get(bukkitEntityId);
        var damagePacket = new WrapperPlayServerHurtAnimation(
                entityId,
                90F
        );
        user.sendPacket(damagePacket);
    }
}
