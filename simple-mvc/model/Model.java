package com.javarush.task.task36.task3608.model;

/**
 * Created by sergei on 3/6/17.
 */
public interface Model {
    /** Just some data instead of database */
    ModelData getModelData();
    /** Putting values into ModelData object */
    void loadUsers();

    void loadDeletedUsers();
    void loadUserById(long userId);

    void deleteUserById(long id);
    void changeUserData(String name, long id, int level);
}
