package ru.micode.shopping.ui.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Коллекция сгруппированных списков
 * Created by Petr Gusarov on 20.04.18.
 */
public class GroupBuySortedList<T extends Comparable<? super T>> extends ArrayList<T> {

    @Override
    public boolean add(T value) {
        boolean add = super.add(value);
        sort();
        return add;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean b = super.addAll(c);
        sort();
        return b;
    }

    @Override
    public void add(int index, T value) {
        this.add(value);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return this.addAll(c);
    }

    public void sort() {
        Collections.sort(this);
    }
}
