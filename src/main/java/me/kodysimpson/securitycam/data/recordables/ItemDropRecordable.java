package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Replay;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "ItemDrop")
public class ItemDropRecordable extends Recordable {

    private UUID bukkitEntityId; //item entity
    private Location location;
    private ItemStack itemStack;

    @Override
    public void replay(Replay replay, User user) throws Exception {

        Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
        Field field = entityClass.getDeclaredField("d");
        field.setAccessible(true);
        AtomicInteger ENTITY_COUNTER = (AtomicInteger) field.get(null);
        int entityId = ENTITY_COUNTER.incrementAndGet();

        WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(
                entityId, UUID.randomUUID(),
                EntityTypes.ITEM, SpigotConversionUtil.fromBukkitLocation(location),
                0, 0, null);
        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(entityId,
                List.of(new EntityData(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(itemStack))));
        user.sendPacket(spawnEntityPacket);
        user.sendPacket(metadataPacket);
        System.out.println("Added spawned entity: " + entityId + " for bukkit entity: " + bukkitEntityId);
        replay.getSpawnedEntities().put(bukkitEntityId, entityId);
    }
}
