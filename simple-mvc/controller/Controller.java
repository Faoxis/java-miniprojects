package com.javarush.task.task36.task3608.controller;

import com.javarush.task.task36.task3608.bean.User;
import com.javarush.task.task36.task3608.model.Model;
import com.javarush.task.task36.task3608.view.EditUserView;
import com.javarush.task.task36.task3608.view.UsersView;

import java.util.List;

/**
 * Created by sergei on 3/6/17.
 */
public class Controller {
    private Model model;
    private UsersView usersView;
    private EditUserView editUserView;

    /**
     * Method for connection between model and view
     * */
    public void onShowAllUsers() {
        model.loadUsers();
        usersView.refresh(model.getModelData());
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setUsersView(UsersView usersView) {
        this.usersView = usersView;
    }

    public void onShowAllDeletedUsers() {
        model.loadDeletedUsers();
        usersView.refresh(model.getModelData());
    }

    public void setEditUserView(EditUserView editUserView) {
        this.editUserView = editUserView;
    }

    public void onOpenUserEditForm(long userId) {
        model.loadUserById(userId);
        editUserView.refresh(model.getModelData());
    }

    public void onUserDelete(long id) {
        model.deleteUserById(id);
        onShowAllUsers();
    }

    public void onUserChange(String name, long id, int level) {
        model.changeUserData(name, id, level);
        onShowAllUsers();
    }
}
