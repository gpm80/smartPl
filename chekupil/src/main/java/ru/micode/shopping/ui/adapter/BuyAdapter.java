package ru.micode.shopping.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerAdapter;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerHolder;
import ru.micode.shopping.ui.common.GroupBuySortedList;

/**
 * Адаптер покупок
 * Created by Petr Gusarov on 24.04.18.
 */
public class BuyAdapter extends MyRecyclerAdapter<Buy> {

    private final static String LOG_TAG = BuyAdapter.class.getSimpleName();

    public BuyAdapter(Context context, ItemTouchListener<Buy> itemTouchListener) {
        super(context, new Buy(), new GroupBuySortedList<Buy>(), itemTouchListener);
    }

    /**
     * Обновление отображаемых даных
     */
    public void refresh() {
        ((GroupBuySortedList) values).sort();
        notifyDataSetChanged();
    }

    @Override
    public MyRecyclerHolder<Buy> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BuyHolder(context, parent, this);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder<Buy> holder, int position) {
        Log.d(LOG_TAG, "BindViewHolder position=" + position);
        Buy buy = values.get(position);
        Buy prev = null;
        if (position > 0) {
            prev = values.get(position - 1);
        }
        holder.bind(buy, position);
        ((BuyHolder) holder).bindCustom(buy, prev);
    }

    /**
     * Холдер элемента позиции списка покупки
     */
    public class BuyHolder extends MyRecyclerHolder<Buy> {

        private TextView header;
        private TextView nameBuy;
        private ImageView checkImageView;
        private LinearLayout headerLayout;
        private LinearLayout buyViewLayout;
        private final int headerColor;
        private final int iconDoneMaskColor;

        public BuyHolder(Context context, ViewGroup parent, ActionListener actionListener) {
            super(context, R.layout.buy_adapter, parent, actionListener);
            boolean darkTheme = ApplicationLoader.isDarkTheme();
            headerColor = ContextCompat.getColor(context, darkTheme
                ? R.color.che_text_header_dark : R.color.che_text_header_light);
            iconDoneMaskColor = ContextCompat.getColor(context, R.color.che_mask_draw_common);
            nameBuy = (TextView) itemView.findViewById(R.id.buy_name);
            checkImageView = (ImageView) itemView.findViewById(R.id.buy_done);

            header = (TextView) itemView.findViewById(R.id.buy_header);
            header.setTextColor(headerColor);
            headerLayout = (LinearLayout) itemView.findViewById(R.id.buy_header_layout);
            buyViewLayout = (LinearLayout) itemView.findViewById(R.id.buy_view_layout);
            buyViewLayout.setOnClickListener(this);
            buyViewLayout.setOnLongClickListener(this);
        }

        public void bindCustom(Buy buy, Buy previous) {
            // Отображнение групы
            if (previous == null || !previous.getGroup(true).equals(buy.getGroup(true))) {
                headerLayout.setVisibility(View.VISIBLE);
                header.setVisibility(View.VISIBLE);
                header.setText(String.valueOf(currentValue.getGroup(true).getName()));
            } else {
                headerLayout.setVisibility(View.GONE);
                header.setVisibility(View.GONE);
            }
            // Установим значения
            StringBuilder viewText = new StringBuilder(currentValue.getName());
            if (currentValue.getAmount() != 0) {
                viewText.append(" - ").append(currentValue.getAmount());
                if (currentValue.getMeasure() != null) {
                    viewText.append(" (").append(currentValue.getMeasure()).append(")");
                }
            }
            nameBuy.setText(viewText);
            nameBuy.setEnabled(!currentValue.isDone());
            // Статус выделения
            if (currentValue.isDone()) {
                checkImageView.setImageResource(R.drawable.ic_buy_basket);
                checkImageView.setColorFilter(iconDoneMaskColor, PorterDuff.Mode.MULTIPLY);
            } else {
                checkImageView.setImageResource(0);
            }
        }

        @Override
        protected void bindData(Buy value, int position) {
        }
    }
}
