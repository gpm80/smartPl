package ru.micode.shopping.ui.test;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerAdapter;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerHolder;

/**
 * Created by Petr Gusarov on 02.11.18.
 */
public class ItemRecyclerAdapter extends MyRecyclerAdapter<Item> {

    public ItemRecyclerAdapter(Context context, List<Item> collection, ItemTouchListener<Item> itemTouchListener) {
        super(context, new Item(), collection, itemTouchListener);
    }

    @Override
    public MyRecyclerHolder<Item> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(context, parent, this);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder<Item> holder, int position) {
        holder.bind(values.get(position), position);
    }

    /**
     * Ходдер
     */
    public static class ItemViewHolder extends MyRecyclerHolder<Item> {

        private final TextView nameTextView;
        private final TextView countTextView;
        private final int selectColor;

        public ItemViewHolder(Context context, ViewGroup group, ActionListener<Item> actionListener) {
            super(context, R.layout.test_adapter, group, actionListener);
            boolean isDark = ApplicationLoader.isDarkTheme();
            selectColor = ContextCompat.getColor(context, isDark
                ? R.color.che_background_inverse_dark : R.color.che_background_inverse_light);
            nameTextView = itemView.findViewById(R.id.test_name);
            countTextView = itemView.findViewById(R.id.test_count);
        }

        @Override
        protected void bindData(Item value, int position) {
            if (value != null) {
                nameTextView.setText(value.getName());
                countTextView.setText(String.valueOf(value.getCount()));
            } else {
                nameTextView.setText("-");
                countTextView.setText("-");
            }
            // Маска цвета по текущему стилю
            if (currentValue.isSelected()) {
                itemView.setBackgroundColor(selectColor);
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}
