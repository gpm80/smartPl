package ru.micode.shopping.ui.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Petr Gusarov on 02.11.18.
 */
public abstract class MyRecyclerHolder<BEAN> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    protected BEAN currentValue;
    protected ActionListener<BEAN> actionListener;
    protected int position;

    public MyRecyclerHolder(Context context, int resIdView, ViewGroup group, ActionListener<BEAN> actionListener) {
        super(LayoutInflater.from(context).inflate(resIdView, group, false));
        this.actionListener = actionListener;
        if (actionListener != null) {
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
    }

    public void bind(BEAN value, int position) {
        currentValue = value;
        this.position = position;
        bindData(value, position);
    }

    protected abstract void bindData(BEAN value, int position);

    @Override
    public void onClick(View view) {
        actionListener.itemAction(Action.CLICK, position, view, currentValue);
    }

    @Override
    public boolean onLongClick(View view) {
        return actionListener.itemAction(Action.LONG_CLICK, position, view, currentValue);
    }

    public interface ActionListener<BEAN> {
        boolean itemAction(Action action, int position, View view, BEAN bean);
    }

    public enum Action {
        CLICK,
        LONG_CLICK,
        MODIFIED,
        BIND
    }
}
