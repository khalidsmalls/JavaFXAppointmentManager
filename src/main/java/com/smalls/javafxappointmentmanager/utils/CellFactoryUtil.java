package com.smalls.javafxappointmentmanager.utils;

import com.smalls.javafxappointmentmanager.model.Named;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class CellFactoryUtil {
    public static <T>Callback<ListView<T>, ListCell<T>> createCellFactory() {
        return new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> tListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getItemName(item));
                    }
                };
            }
        };
    }

    private static <T> String getItemName(T item) {
        if (item instanceof Named) {
            return ((Named) item).getName();
        } else {
            return null;
        }
    }
}
