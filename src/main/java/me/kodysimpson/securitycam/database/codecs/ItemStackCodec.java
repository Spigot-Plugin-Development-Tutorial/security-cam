package me.kodysimpson.securitycam.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackCodec implements Codec<ItemStack> {
    @Override
    public ItemStack decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();
        Material type = Material.getMaterial(bsonReader.readString("type"));
        int amount = bsonReader.readInt32("amount");
        bsonReader.readEndDocument();
        if (type == null){
            return null;
        }
        return new ItemStack(type, amount);
    }

    @Override
    public void encode(BsonWriter writer, ItemStack itemStack, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("type", itemStack.getType().name());
        writer.writeInt32("amount", itemStack.getAmount());
        writer.writeEndDocument();
    }

    @Override
    public Class<ItemStack> getEncoderClass() {
        return ItemStack.class;
    }
}

