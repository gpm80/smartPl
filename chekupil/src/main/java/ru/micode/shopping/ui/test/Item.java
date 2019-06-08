package ru.micode.shopping.ui.test;

import ru.micode.shopping.ui.adapter.recycler.Selectable;

/**
 * Created by Petr Gusarov on 02.11.18.
 */
public class Item implements Selectable {

    private String name;
    private int count;
    private boolean selected;

    public Item() {
    }

    public Item(String name, int count) {
        this.name = name;
        this.count = count;
        this.selected = false;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean b) {
        this.selected = b;
    }

    @Override
    public boolean negative() {
        return selected = !selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
