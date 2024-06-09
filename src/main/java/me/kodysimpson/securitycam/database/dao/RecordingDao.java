package me.kodysimpson.securitycam.database.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import me.kodysimpson.securitycam.data.Recording;
import me.kodysimpson.securitycam.database.MongoService;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class RecordingDao implements IDao<Recording> {

    private final MongoCollection<Recording> collection;

    public RecordingDao(MongoService mongoService) {
        this.collection = mongoService.getDatabase().getCollection("recordings", Recording.class);
    }

    @Override
    public void insert(Recording entity) {
        collection.insertOne(entity);
    }

    @Override
    public Recording findById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    @Override
    public List<Recording> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void update(Recording entity) {
        var updates = Updates.combine(
                Updates.set("endTime", entity.getEndTime()),
                Updates.set("endTick", entity.getEndTick())
        );
        collection.updateOne(Filters.eq("_id", entity.getId()), updates);
    }

    @Override
    public void delete(Recording entity) {
        collection.deleteOne(Filters.eq("_id", entity.getId()));
    }

    public List<Recording> getCameraRecordings(ObjectId cameraId){
        return collection.find(Filters.eq("cameraId", cameraId))
                .sort(Sorts.descending("startTime"))
                .into(new ArrayList<>());
    }
}
