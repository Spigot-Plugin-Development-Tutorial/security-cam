package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.mojang.authlib.GameProfile;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Replay;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "SpawnEntity")
public class SpawnEntityRecordable extends Recordable {

    private EntityType entityType;
    private Location location;
    private UUID bukkitEntityId;
    private String playerName;

    public SpawnEntityRecordable(Entity entity) {
        this.entityType = entity.getType();
        this.location = entity.getLocation();
        this.bukkitEntityId = entity.getUniqueId();
        if (this.entityType == EntityType.PLAYER){
            this.playerName = entity.getName();
        }
    }

    @Override
    public void replay(Replay replay, User user) throws Exception {
        if (entityType == EntityType.PLAYER){

            Player player = replay.getViewer();
            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerPlayer serverPlayer = craftPlayer.getHandle();
            MinecraftServer server = serverPlayer.getServer();
            ServerLevel level = serverPlayer.serverLevel().getLevel();

            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), playerName);
            ServerPlayer npc = new ServerPlayer(server, level, gameProfile);
            npc.setPos(location.getX(), location.getY(), location.getZ());

            ServerGamePacketListenerImpl ps = serverPlayer.connection;
            ps.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
            ps.send(new ClientboundAddPlayerPacket(npc));

            replay.getSpawnedEntities().put(bukkitEntityId, npc.getId());
        }else{

            Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
            Field field = entityClass.getDeclaredField("d");
            field.setAccessible(true);
            AtomicInteger ENTITY_COUNTER = (AtomicInteger) field.get(null);
            int entityId = ENTITY_COUNTER.incrementAndGet();

            com.github.retrooper.packetevents.protocol.entity.type.EntityType entityType1 = EntityTypes.getByName(entityType.getKey().toString());

            WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(
                    entityId, UUID.randomUUID(),
                    entityType1, SpigotConversionUtil.fromBukkitLocation(location),
                    0, 0, null);

            user.sendPacket(spawnEntityPacket);
            replay.getSpawnedEntities().put(bukkitEntityId, entityId);
        }
    }
}
