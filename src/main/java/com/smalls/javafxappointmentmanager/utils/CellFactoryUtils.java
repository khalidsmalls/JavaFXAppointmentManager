package com.smalls.javafxappointmentmanager.utils;

import com.smalls.javafxappointmentmanager.model.Named;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CellFactoryUtils {

    public static <T>Callback<ListView<T>, ListCell<T>> comboBoxCellFactory() {
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

    public static <S> TableCell<S, OffsetDateTime> offsetDateTimeTableCellFactory(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return new TableCell<>() {
            @Override
            protected void updateItem(OffsetDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(formatter.format(time));
                }
            }
        };
    }
}
