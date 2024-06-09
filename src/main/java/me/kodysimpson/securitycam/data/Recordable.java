package me.kodysimpson.securitycam.data;

import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;

@Data
@BsonDiscriminator(key = "type", value = "Recordable")
public abstract class Recordable {
    private ObjectId id;
    private ObjectId recordingId;
    private long tick;

    //every single recordable will implement this in their own
    // way to be able to replay itself
    public abstract void replay(Replay replay, User user) throws Exception;
}
