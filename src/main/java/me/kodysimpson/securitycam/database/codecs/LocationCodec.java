package me.kodysimpson.securitycam.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationCodec implements Codec<Location> {
    @Override
    public Location decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = new DocumentCodec().decode(bsonReader, decoderContext);
        World world = Bukkit.getWorld(document.getString("world"));
        double x = document.getDouble("x");
        double y = document.getDouble("y");
        double z = document.getDouble("z");
        float pitch = document.getDouble("pitch").floatValue();
        float yaw = document.getDouble("yaw").floatValue();
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public void encode(BsonWriter writer, Location location, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("world", location.getWorld().getName());
        writer.writeDouble("x", location.getX());
        writer.writeDouble("y", location.getY());
        writer.writeDouble("z", location.getZ());
        writer.writeDouble("pitch", location.getPitch());
        writer.writeDouble("yaw", location.getYaw());
        writer.writeEndDocument();
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }
}
