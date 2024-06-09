package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
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
@BsonDiscriminator(key = "type", value = "DespawnEntity")
public class DespawnEntityRecordable extends Recordable {

    private UUID bukkitEntityId;

    @Override
    public void replay(Replay replay, User user) throws Exception {
        WrapperPlayServerDestroyEntities destroyEntities = new WrapperPlayServerDestroyEntities(replay.getSpawnedEntities().get(bukkitEntityId));
        user.sendPacket(destroyEntities);
        replay.getSpawnedEntities().remove(bukkitEntityId);
    }
}
