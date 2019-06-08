package ru.micode.shopping.ui.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Настройщик списков
 * Created by Petr Gusarov on 14.05.18.
 */
public class TuningRecycler {

    private final RecyclerView recyclerView;

    public static TuningRecycler builder(View recyclerView, Context context) {
        if (recyclerView instanceof RecyclerView) {
            return new TuningRecycler((RecyclerView) recyclerView, context);
        }
        throw new IllegalArgumentException("First params must be extends RecyclerView.class");
    }

    private TuningRecycler(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * Устанавливает вертикальный разделитель элементов списка
     *
     * @return
     */
    public TuningRecycler addDivider() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
            recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        return this;
    }

    public TuningRecycler setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        return this;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
