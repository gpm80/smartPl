package ru.micode.shopping.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerAdapter;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerHolder;

/**
 * Адаптер списка покупок
 * Created by Petr Gusarov on 07.05.18.
 */
public class ShoppingAdapter extends MyRecyclerAdapter<Shopping> {

    private final static String LOG_TAG = ShoppingAdapter.class.getSimpleName();

    public ShoppingAdapter(Context context, ItemTouchListener<Shopping> itemTouchListener) {
        super(context, new Shopping(), new ArrayList<Shopping>(), itemTouchListener);
    }

    @Override
    public MyRecyclerHolder<Shopping> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShoppingHolder(context, parent, this);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder<Shopping> holder, int position) {
        holder.bind(values.get(position), position);
    }

    /**
     * Элемент списка
     */
    public static class ShoppingHolder extends MyRecyclerHolder<Shopping> {

        private TextView name;
        private TextView ratio;
        private TextView authorInfo;
        private ImageView isNewImage;
        private ImageView shoppingIcon;
        private LinearLayout authorLayout;
        private final int selectColor;
        private final int iconMaskColor;
        private final int iconDoneMaskColor;

        public ShoppingHolder(Context context, ViewGroup parent, ActionListener<Shopping> listener) {
            super(context, R.layout.shopping_adapter, parent, listener);
            boolean isDark = ApplicationLoader.isDarkTheme();
            selectColor = ContextCompat.getColor(context, isDark
                ? R.color.che_background_inverse_dark : R.color.che_background_inverse_light);
            iconMaskColor = ContextCompat.getColor(context, isDark
                ? R.color.che_mask_draw_dark : R.color.che_mask_draw_light);
            iconDoneMaskColor = ContextCompat.getColor(context, R.color.che_mask_draw_common);
            name = itemView.findViewById(R.id.shopping_name);
            isNewImage = itemView.findViewById(R.id.shopping_is_new);
            ratio = itemView.findViewById(R.id.shopping_ratio);
            authorLayout = itemView.findViewById(R.id.shopping_author_info);
            authorInfo = authorLayout.findViewById(R.id.shopping_author);
            shoppingIcon = itemView.findViewById(R.id.shopping_common_icon);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        protected void bindData(Shopping shopping, int position) {
            name.setText(shopping.getName());
            ratio.setText(String.format("%s/%s", shopping.getRequired(), shopping.getTotal()));
            int visibleNew = View.GONE;
            if (BooleanUtils.isTrue(shopping.isNew())) {
                visibleNew = View.VISIBLE;
                // Иконка по умолчанию скрывается
                shoppingIcon.setVisibility(View.GONE);
                if (StringUtils.isNotBlank(shopping.getAuthor())) {
                    String author = "прислал(а): " + shopping.getAuthor();
                    authorInfo.setText(author);
                }
            }
            isNewImage.setVisibility(visibleNew);
            authorLayout.setVisibility(visibleNew);
            authorInfo.setVisibility(visibleNew);
            shoppingIcon.setImageResource(R.drawable.ic_shopping);
            if (shopping.getRequired() < 1) {
                name.setEnabled(false);
                ratio.setEnabled(false);
                shoppingIcon.setEnabled(false);
                shoppingIcon.setColorFilter(iconDoneMaskColor, PorterDuff.Mode.MULTIPLY);
            } else {
                name.setEnabled(true);
                ratio.setEnabled(true);
                shoppingIcon.setEnabled(true);
                shoppingIcon.setColorFilter(iconMaskColor, PorterDuff.Mode.MULTIPLY);
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
