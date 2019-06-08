package ru.micode.shopping.ui.adapter.recycler;

import java.util.List;

import android.view.View;

/**
 * Created by Petr Gusarov on 02.11.18.
 */
public abstract class ItemTouchListener<T> {

    public enum Type {
        CLICK,
        LONG_CLICK,
        MODIFIED,
        SELECT,
        BIND
    }

    /**
     * Обработка действий со списком
     *
     * @param type       тип текущего действия
     * @param view       представление
     * @param selectList список выделенных позиций
     * @param value      текущее значение элемента на котором произведено действие
     * @param totalSize    размер всей коллекции списка
     * @return true если действие обработано иначе false
     */
    public abstract boolean touch(Type type, View view, List<T> selectList, int totalSize, T value);
}
