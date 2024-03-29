package ru.micode.shopping.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import ru.micode.shopping.BuildConfig;
import ru.micode.shopping.R;
import ru.micode.shopping.model.ex.ExBuy;
import ru.smart.planet.web.Product;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerAdapter;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerHolder;

public class ProductAdapter extends MyRecyclerAdapter<Product> {

    public ProductAdapter(Context context, ItemTouchListener<Product> actionListener) {
        super(context, new Product(), new ArrayList<Product>(), actionListener);
    }

    @Override
    public MyRecyclerHolder<Product> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductHolder(context, parent, this);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder<Product> holder, int position) {
        holder.bind(values.get(position), position);
    }

    public Product getByPosition(int pos) {
        if (pos >= 0 && pos < values.size()) {
            return values.get(pos);
        }
        return null;
    }


    public static class ProductHolder extends MyRecyclerHolder<Product> {

        private TextView title;
        private TextView description;
        private ImageView image;
        private ProgressBar loadingBar;
        private ImageView favoriteImage;

        public ProductHolder(Context context, ViewGroup group, ActionListener<Product> actionListener) {
            super(context, R.layout.product_adapter, group, actionListener);
            title = itemView.findViewById(R.id.product_title);
            description = itemView.findViewById(R.id.product_description);
            image = itemView.findViewById(R.id.product_image);
            loadingBar = itemView.findViewById(R.id.product_loading);
//            favoriteImage = itemView.findViewById(R.id.recipe_favorite);
        }

//        /**
//         * Установка статуса избранного рецепта
//         *
//         * @param isFavorite
//         */
//        private void setFavoriteState(boolean isFavorite) {
//            if (isFavorite) {
//                favoriteImage.setImageResource(R.drawable.ic_star);
//            } else {
//                favoriteImage.setImageResource(R.drawable.ic_star_border);
//            }
//        }

        @Override
        protected void bindData(final Product value, final int position) {
            title.setText(value.getTitle());
            description.setText(StringUtils.abbreviate(value.getDescription(), 150));
//            favoriteImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    boolean isFav = actionListener.itemAction(Action.MODIFIED, position, view, value);
//                    setFavoriteState(isFav);
//                }
//            });
            boolean b = actionListener.itemAction(Action.BIND, position, null, value);
//            setFavoriteState(b);
//            if (!value.getImages().isEmpty()) {
//                loadingBar.setVisibility(View.VISIBLE);
//                String srcImage = value.getImages().get(0);
//                // Загрузить картинку
//                Picasso picasso = Picasso.get();
//                picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
//                picasso.load(srcImage)
//                    .into(image, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            loadingBar.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            loadingBar.setVisibility(View.GONE);
//                        }
//                    });
//            } else {
//                loadingBar.setVisibility(View.GONE);
//            }
            if (StringUtils.isNotBlank(value.getSrcImage())) {
                loadingBar.setVisibility(View.VISIBLE);
                String srcImage = value.getSrcImage();
                // Загрузить картинку
                Picasso picasso = Picasso.get();
                //picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
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
    }}
}
