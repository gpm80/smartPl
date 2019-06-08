package ru.micode.shopping.ui.adapter;

import java.util.ArrayList;

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
import ru.micode.shopping.model.SearchBuy;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerAdapter;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerHolder;

/**
 * Адаптер списка найденных в справочнике позиций для списка покупок
 * Created by Petr Gusarov on 24.04.18.
 */
public class BuyNewSearchAdapter extends MyRecyclerAdapter<SearchBuy.WrapperBuy> {

    private final static String LOG_TAG = BuyNewSearchAdapter.class.getSimpleName();

    public BuyNewSearchAdapter(Context context, ItemTouchListener<SearchBuy.WrapperBuy> actionListener) {
        super(context, new SearchBuy.WrapperBuy("", "", 0), new ArrayList<SearchBuy.WrapperBuy>(), actionListener);
    }

    /**
     * Обновление отображаемых даных
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    public void refreshPos(int pos) {
        notifyItemChanged(pos);
    }

    @Override
    public BuyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "CreateViewHolder viewType=" + viewType);
        return new BuyHolder(context, parent, this);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder<SearchBuy.WrapperBuy> holder, int position) {
        Log.d(LOG_TAG, "BindViewHolder position=" + position);
        SearchBuy.WrapperBuy item = values.get(position);
        item.setPosition(position);
        holder.bind(item, position);
    }

    /**
     * Холдер элемента позиции списка покупки
     */
    public class BuyHolder extends MyRecyclerHolder<SearchBuy.WrapperBuy> {

        private TextView nameBuy;
        private TextView inListCount;
        private TextView nameGroup;
        private LinearLayout buyViewLayout;
        private LinearLayout inListLayout;
        private ImageView inListImageView;
        private final String manualDescription;
        private final int iconMaskColor;

        public BuyHolder(Context context, ViewGroup parent, ActionListener<SearchBuy.WrapperBuy> onActionListener) {
            super(context, R.layout.buy_search_adapter, parent, onActionListener);
            boolean isDark = ApplicationLoader.isDarkTheme();
            iconMaskColor = ContextCompat.getColor(context, isDark
                ? R.color.che_mask_draw_dark : R.color.che_mask_draw_light);
            manualDescription = context.getResources().getString(R.string.search_manual_item_buy_description);
            nameBuy = itemView.findViewById(R.id.buy_search_name);
            nameGroup = itemView.findViewById(R.id.buy_search_group);

            inListLayout = itemView.findViewById(R.id.buy_search_in_list_layout);
            inListCount = itemView.findViewById(R.id.buy_search_in_list_count);
            inListImageView = itemView.findViewById(R.id.buy_search_in_list_icon);

            buyViewLayout = itemView.findViewById(R.id.buy_search_item_layout);
            buyViewLayout.setOnClickListener(this);
            buyViewLayout.setOnLongClickListener(this);
        }

        @Override
        protected void bindData(SearchBuy.WrapperBuy value, int position) {
            currentValue = value;
            Buy buy = value.getBuy();
            // Установим значения
            nameBuy.setText(buy.getName());
            if (value.isManual()) {
                nameGroup.setText(manualDescription);
            } else {
                nameGroup.setText(buy.getNameGroup());
            }
            inListImageView.setColorFilter(iconMaskColor, PorterDuff.Mode.MULTIPLY);
            if (buy.getAmount() > 0) {
                inListLayout.setVisibility(View.VISIBLE);
            } else {
                inListLayout.setVisibility(View.GONE);
            }
            inListCount.setText(String.valueOf(buy.getAmount()));
        }
    }
}
