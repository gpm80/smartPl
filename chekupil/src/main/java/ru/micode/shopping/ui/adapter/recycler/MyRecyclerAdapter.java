package ru.micode.shopping.ui.adapter.recycler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Petr Gusarov on 01.11.18.
 */
public abstract class MyRecyclerAdapter<BEAN> extends RecyclerView.Adapter<MyRecyclerHolder<BEAN>> implements MyRecyclerHolder.ActionListener<BEAN> {

    protected final List<BEAN> values;
    protected final Context context;
    protected final ItemTouchListener<BEAN> itemTouchListener;
    private boolean selectable;
    private int selectedCount;

    public MyRecyclerAdapter(Context context, BEAN emptyBean, List<BEAN> collection, ItemTouchListener<BEAN> itemTouchListener) {
        this.values = collection;
        this.context = context;
        this.itemTouchListener = itemTouchListener;
        selectable = emptyBean instanceof Selectable;
        selectedCount = 0;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setDeselectable() {
        this.selectable = false;
    }

    public void clear() {
        selectedCount = 0;
        values.clear();
        notifyDataSetChanged();
    }

    public int add(BEAN value) {
        values.add(value);
        int i = values.indexOf(value);
        notifyItemInserted(i);
        return i;
    }

    public boolean addAll(Collection<? extends BEAN> collection) {
        boolean b = values.addAll(collection);
        notifyDataSetChanged();
        return b;
    }

    public void deselectionAll() {
        if (isSelectable()) {
            for (BEAN b : values) {
                ((Selectable) b).setSelected(false);
            }
            notifyDataSetChanged();
        }
        selectedCount = 0;
    }

    public List<BEAN> getSelectValues() {
        List<BEAN> list = new ArrayList<>();
        selectedCount = 0;
        if (isSelectable()) {
            for (BEAN b : values) {
                if (((Selectable) b).isSelected()) {
                    list.add(b);
                    selectedCount++;
                }
            }
        }
        return list;
    }

    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public boolean itemAction(MyRecyclerHolder.Action action, int position, View view, BEAN bean) {
        switch (action) {
            case CLICK:
                if (selectedCount > 0) {
                    return selectAction(position, view, bean);
                } else {
                    return itemTouchListener.touch(ItemTouchListener.Type.CLICK, view, new ArrayList<BEAN>(), getItemCount(), bean);
                }
            case LONG_CLICK:
                if (isSelectable()) {
                    return selectAction(position, view, bean);
                } else {
                    return itemTouchListener.touch(ItemTouchListener.Type.LONG_CLICK, view, new ArrayList<BEAN>(), getItemCount(), bean);
                }
            case MODIFIED:
                return itemTouchListener.touch(ItemTouchListener.Type.MODIFIED, view, new ArrayList<BEAN>(), getItemCount(), bean);
            case BIND:
                return itemTouchListener.touch(ItemTouchListener.Type.BIND, view, new ArrayList<BEAN>(), getItemCount(), bean);
        }
        return false;
    }

    private boolean selectAction(int position, View view, BEAN bean) {
        if (isSelectable()) {
            ((Selectable) bean).negative();
            notifyItemChanged(position);
            List<BEAN> selectValues = getSelectValues();
//            if (selectValues.isEmpty()) {
//                return itemTouchListener.touch(ItemTouchListener.Type.DESELECTION, view, selectValues, getItemCount(), bean);
//            }
            return itemTouchListener.touch(ItemTouchListener.Type.SELECT, view, selectValues, getItemCount(), bean);
        }
        return false;
    }
}
