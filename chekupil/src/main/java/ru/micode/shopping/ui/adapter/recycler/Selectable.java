package ru.micode.shopping.ui.adapter.recycler;

/**
 * Интерфейс выделяемых бинов
 * Created by Petr Gusarov on 17.07.18.
 */
public interface Selectable {

    boolean isSelected();

    void setSelected(boolean b);

    boolean negative();
}
