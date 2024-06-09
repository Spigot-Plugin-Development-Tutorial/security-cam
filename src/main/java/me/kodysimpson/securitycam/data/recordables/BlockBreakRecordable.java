package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Replay;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "BlockBreak")
public class BlockBreakRecordable extends Recordable {

    private Location location;

    @Override
    public void replay(Replay replay, User user) throws Exception {
        Vector3i position = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        var stateType = StateTypes.AIR;
        var wrappedBlockState = WrappedBlockState.getDefaultState(stateType);
        WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange(position, wrappedBlockState.getGlobalId());
        user.sendPacket(blockChangePacket);
    }
}
