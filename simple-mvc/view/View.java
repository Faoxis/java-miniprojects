package com.javarush.task.task36.task3608.view;

import com.javarush.task.task36.task3608.controller.Controller;
import com.javarush.task.task36.task3608.model.ModelData;

/**
 * Created by sergei on 3/6/17.
 */
public interface View {
    /**
     * method gets data, handles it and turns it back
     */
    void refresh(ModelData modelData);

    /** just a controller setter */
    void setController(Controller controller);
}
