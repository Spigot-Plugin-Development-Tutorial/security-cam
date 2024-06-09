package me.kodysimpson.securitycam.database.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import me.kodysimpson.securitycam.SecurityCam;
import me.kodysimpson.securitycam.data.Camera;
import me.kodysimpson.securitycam.database.MongoService;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CameraDao implements IDao<Camera> {

    private final MongoCollection<Camera> collection;

    public CameraDao(MongoService mongoService) {
        this.collection = mongoService
                .getDatabase().getCollection("cameras", Camera.class);
    }

    @Override
    public void insert(Camera camera){
        collection.insertOne(camera);
    }

    @Override
    public Camera findById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    @Override
    public List<Camera> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void update(Camera entity) {
        var updates = Updates.combine(
                Updates.set("name", entity.getName())
        );
        collection.updateOne(Filters.eq("_id", entity.getId()), updates);
    }

    @Override
    public void delete(Camera entity) {
        collection.deleteOne(Filters.eq("_id", entity.getId()));
    }

    public List<Camera> findAllByOwnerId(UUID owner){
        return collection.find(Filters.eq("owner", owner)).into(new ArrayList<>());
    }

}
