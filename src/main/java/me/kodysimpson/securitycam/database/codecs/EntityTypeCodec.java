package me.kodysimpson.securitycam.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.entity.EntityType;

public class EntityTypeCodec implements Codec<EntityType> {
    @Override
    public EntityType decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();
        EntityType entityType = EntityType.valueOf(bsonReader.readString("entityType"));
        bsonReader.readEndDocument();
        return entityType;
    }

    @Override
    public void encode(BsonWriter bsonWriter, EntityType entityType, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();
        bsonWriter.writeString("entityType", entityType.name());
        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<EntityType> getEncoderClass() {
        return EntityType.class;
    }
}
