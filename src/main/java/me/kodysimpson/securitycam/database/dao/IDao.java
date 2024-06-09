package me.kodysimpson.securitycam.database.dao;

import org.bson.types.ObjectId;

import java.util.List;

public interface IDao<T> {
    void insert(T entity);
    T findById(ObjectId id);
    List<T> findAll();
    void update(T entity);
    void delete(T entity);
}
