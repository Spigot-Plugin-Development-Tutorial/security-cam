package me.kodysimpson.securitycam.data.recordables;

import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.data.Replay;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BsonDiscriminator(key = "type", value = "SetEquipment")
public class SetEquipmentRecordable extends Recordable {

    private UUID bukkitEntityId;
    private ItemStack mainHand;
    private ItemStack offHand;
    private ItemStack head;
    private ItemStack chest;
    private ItemStack legs;
    private ItemStack feet;

    @Override
    public void replay(Replay replay, User user) throws Exception {

        var entityId = replay.getSpawnedEntities().get(bukkitEntityId);

        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment(EquipmentSlot.MAIN_HAND, SpigotConversionUtil.fromBukkitItemStack(mainHand)));
        equipment.add(new Equipment(EquipmentSlot.OFF_HAND, SpigotConversionUtil.fromBukkitItemStack(offHand)));
        equipment.add(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(head)));
        equipment.add(new Equipment(EquipmentSlot.CHEST_PLATE, SpigotConversionUtil.fromBukkitItemStack(chest)));
        equipment.add(new Equipment(EquipmentSlot.LEGGINGS, SpigotConversionUtil.fromBukkitItemStack(legs)));
        equipment.add(new Equipment(EquipmentSlot.BOOTS, SpigotConversionUtil.fromBukkitItemStack(feet)));

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(entityId, equipment);
        user.sendPacket(packet);
    }


}
