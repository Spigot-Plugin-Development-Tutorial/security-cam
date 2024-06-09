package me.kodysimpson.securitycam.database.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import me.kodysimpson.securitycam.data.Recordable;
import me.kodysimpson.securitycam.database.MongoService;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class RecordableDao implements IDao<Recordable> {

    private final MongoCollection<Recordable> collection;

    public RecordableDao(MongoService mongoService) {
        this.collection = mongoService.getDatabase()
                .getCollection("recordables", Recordable.class);
    }


    @Override
    public void insert(Recordable entity) {
        collection.insertOne(entity);
    }

    @Override
    public Recordable findById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    @Override
    public List<Recordable> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void update(Recordable entity) {

    }

    @Override
    public void delete(Recordable entity) {

    }

    public List<Recordable> findByRecordingIdAndTickBetween(ObjectId recordingId, long startTick, long endTick){
        return collection.find(Filters.and(Filters.eq("recordingId", recordingId),
                        Filters.gte("tick", startTick),
                        Filters.lte("tick", endTick)))
                .sort(Sorts.ascending("tick"))
                .into(new ArrayList<>());
    }

    public void insertMany(List<Recordable> recordables){
        collection.insertMany(recordables);
    }

}
