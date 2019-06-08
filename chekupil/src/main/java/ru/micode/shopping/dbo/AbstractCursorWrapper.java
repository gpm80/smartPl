package ru.micode.shopping.dbo;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by Petr Gusarov on 19.03.18.
 */
public abstract class AbstractCursorWrapper<T> extends CursorWrapper {
    /**
     * Конструктор обертки курсора.
     *
     * @param cursor основной кусор.
     */
    public AbstractCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Возвращает список экземпляров модели
     *
     * @return
     */
    public List<T> getItems() {
        List<T> result = new ArrayList<>();
        try {
            moveToFirst();
            while (!isAfterLast()) {
                result.add(getModel());
                moveToNext();
            }
            return result;
        } finally {
            close();
        }
    }

    /**
     * Возвращает один элемент модели
     *
     * @return
     */
    public T getItem() {
        try {
            if (getCount() == 0) {
                return null;
            }
            moveToFirst();
            return getModel();
        } finally {
            close();
        }
    }

    /**
     * Абстрактный метод извлечение полей модели из курсора
     *
     * @return
     */
    protected abstract T getModel();
}
