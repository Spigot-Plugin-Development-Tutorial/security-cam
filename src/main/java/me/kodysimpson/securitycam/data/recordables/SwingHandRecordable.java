package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
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
@BsonDiscriminator(key = "type", value = "SwingHand")
public class SwingHandRecordable extends Recordable {

    private UUID bukkitEntityId;
    private int handId;

    @Override
    public void replay(Replay replay, User user) throws Exception {

        var entityId = replay.getSpawnedEntities().get(bukkitEntityId);
        var entityAnimation = handId == 0 ?
                WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM
                : WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_OFF_HAND;
        var animationPacket = new WrapperPlayServerEntityAnimation(entityId, entityAnimation);
        user.sendPacket(animationPacket);
    }
}
