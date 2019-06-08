package ru.micode.shopping.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.micode.shopping.R;

/**
 * Created by Petr Gusarov on 28.04.18.
 */
public abstract class BeanArrayAdapter<T> extends ArrayAdapter<T> {

    List<T> values;

    public BeanArrayAdapter(Context context, List<T> values) {
        super(context, R.layout.spinner_view, values);
        this.values = values;
    }

    /**
     * Возвращает наличие элемента в списке
     *
     * @param bean искомый элемент
     * @return true если элемент имеется в списке
     */
    public boolean contains(T bean) {
        return values.contains(bean);
    }

    /**
     * Добавляет элемент в смписок
     *
     * @param bean
     */
    public void add(T bean) {
        values.add(bean);
    }

    /**
     * Возвращает индекс искомого элемента
     *
     * @param bean искомый элемент
     * @return индекс элемента в списке, либо -1
     */
    public int getIndexByBean(T bean) {
        return values.indexOf(bean);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCommonView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCommonView(position, convertView, parent);
    }

    private View getCommonView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view, parent, false);
        TextView value = (TextView) view.findViewById(R.id.value_spinner);
        T bean = values.get(position);
        value.setText(getPresentValue(bean));
        return view;
    }

    protected abstract String getPresentValue(T bean);
}
