package ru.micode.shopping.ui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.BuildConfig;
import ru.micode.shopping.R;
import ru.micode.shopping.model.ex.ExBuy;
import ru.micode.shopping.model.ex.ExRecipe;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerAdapter;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerHolder;

/**
 * Created by Petr Gusarov on 29.03.19.
 */
public class RecipeAdapter extends MyRecyclerAdapter<ExRecipe> {

    public RecipeAdapter(Context context, ItemTouchListener<ExRecipe> actionListener) {
        super(context, new ExRecipe(), new ArrayList<ExRecipe>(), actionListener);
    }

    @Override
    public MyRecyclerHolder<ExRecipe> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecipeHolder(context, parent, this);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder<ExRecipe> holder, int position) {
        holder.bind(values.get(position), position);
    }

    public ExRecipe getByPosition(int pos) {
        if (pos >= 0 && pos < values.size()) {
            return values.get(pos);
        }
        return null;
    }


    public static class RecipeHolder extends MyRecyclerHolder<ExRecipe> {

        private TextView title;
        private ImageView favoriteImage;
        private TextView buys;
        private TextView description;
        private ImageView image;
        private ProgressBar loadingBar;
        private LinearLayout adLayout;
        private final Context context;

        public RecipeHolder(Context context, ViewGroup group, ActionListener<ExRecipe> actionListener) {
            super(context, R.layout.recipe_adapter, group, actionListener);
            this.context = context;
            title = itemView.findViewById(R.id.recipe_title);
            favoriteImage = itemView.findViewById(R.id.recipe_favorite);
            buys = itemView.findViewById(R.id.recipe_buys);
            description = itemView.findViewById(R.id.recipe_description);
            image = itemView.findViewById(R.id.recipe_image);
            loadingBar = itemView.findViewById(R.id.recipe_loading);
            adLayout = itemView.findViewById(R.id.recipe_ad_layout);
        }

        /**
         * Установка статуса избранного рецепта
         *
         * @param isFavorite
         */
        private void setFavoriteState(boolean isFavorite) {
            if (isFavorite) {
                favoriteImage.setImageResource(R.drawable.ic_star);
            } else {
                favoriteImage.setImageResource(R.drawable.ic_star_border);
            }
        }

        /**
         * Загружает рекламу по суловию позиции элемента в списке
         *
         * @param position
         */
        private void loadAdView(int position) {
            if (position % 3 == 0) {
                adLayout.setVisibility(View.VISIBLE);
            } else {
                adLayout.setVisibility(View.GONE);
            }
        }

        @Override
        protected void bindData(final ExRecipe value, final int position) {
            loadAdView(position);
            title.setText(value.getTitle());
            List<ExBuy> exBuyList = value.getExBuyList();
            List<String> buysView = new ArrayList<>();
            if (!exBuyList.isEmpty()) {
                for (ExBuy buy : exBuyList) {
                    buysView.add(buy.getName()
                        + (buy.getAmount() != null
                        ? " - " + buy.getAmount().toString() + (StringUtils.isNotBlank(buy.getMeasure()) ? " " + buy.getMeasure() : "")
                        : ""));
                }
                buys.setText(StringUtils.abbreviate(StringUtils.join(buysView, ", "), 100));
            } else {
                buys.setText("");
            }
            description.setText(StringUtils.abbreviate(value.getDescription(), 150));
            favoriteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isFav = actionListener.itemAction(Action.MODIFIED, position, view, value);
                    setFavoriteState(isFav);
                }
            });
            boolean b = actionListener.itemAction(Action.BIND, position, null, value);
            setFavoriteState(b);
            if (!value.getImages().isEmpty()) {
                loadingBar.setVisibility(View.VISIBLE);
                String srcImage = value.getImages().get(0);
                // Загрузить картинку
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
                picasso.load(srcImage)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadingBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            loadingBar.setVisibility(View.GONE);
                        }
                    });
            } else {
                loadingBar.setVisibility(View.GONE);
            }
        }
    }
}
