package me.kodysimpson.securitycam.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import me.kodysimpson.securitycam.data.recordables.*;
import me.kodysimpson.securitycam.database.codecs.EntityTypeCodec;
import me.kodysimpson.securitycam.database.codecs.ItemStackCodec;
import me.kodysimpson.securitycam.database.codecs.LocationCodec;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoService {

    private final MongoClient mongoClient;
    private final String URI = "mongodb+srv://tutorial:2xo2FLiXV2huKcgC@spigotcluster.w08nt.gcp.mongodb.net/?retryWrites=true&w=majority&appName=SpigotCluster";

    public MongoService() {

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(new LocationCodec(), new EntityTypeCodec(), new ItemStackCodec()),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                                .register(SpawnEntityRecordable.class)
                                .register(DespawnEntityRecordable.class)
                                .register(LocationChangeRecordable.class)
                                .register(SneakRecordable.class)
                                .register(SprintRecordable.class)
                                .register(SwingHandRecordable.class)
                                .register(SetEquipmentRecordable.class)
                                .register(EntityHurtRecordable.class)
                                .register(ItemDropRecordable.class)
                                .register(ItemPickupRecordable.class)
                                .register(BlockPlaceRecordable.class)
                                .register(BlockBreakRecordable.class)
                        .automatic(true).build())
        );
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString(URI))
                .serverApi(serverApi)
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(settings);
    }

    public MongoDatabase getDatabase(){
        return mongoClient.getDatabase("security-cam");
    }

}
