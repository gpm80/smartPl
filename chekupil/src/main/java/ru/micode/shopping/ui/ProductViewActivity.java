package ru.micode.shopping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import ru.micode.shopping.BuildConfig;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Favorite;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.model.ex.ExBuy;
import ru.micode.shopping.model.ex.ExRecipe;
import ru.micode.shopping.model.ex.ShopListAdapter;
import ru.micode.shopping.rest.ApiFactory;
import ru.micode.shopping.rest.ApiHandler;
import ru.micode.shopping.service.BuyService;
import ru.micode.shopping.service.FavoriteService;
import ru.micode.shopping.service.ShoppingService;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;
import ru.micode.shopping.ui.map.MarkerMapActivity;
import ru.smart.planet.web.Product;

/**
 * Created by Petr Gusarov on 02.04.19.
 */
public class ProductViewActivity extends MyAbstractActivity {


    @Override
    protected Fragment createFragment() {
        return new ProductViewFragment();
    }

    @Override
    protected boolean isViewBackButton() {
        return true;
    }

    @Override
    protected boolean isExit() {
        return true;
    }

    /**
     * Фрагмент полного просмотра
     */
    public static class ProductViewFragment extends MyAbstractFragment  {

        private static final String LOG_TAG = ProductViewFragment.class.getSimpleName();
        public static final String INTENT_KEY_PRODUCT_UID = "keyProductUid";
        private Product currentProduct;
        private TextView title;
        private ImageView image;
        private ProgressBar loadingProgress;
        private TextView buys;
        private TextView description;
        private Button loadShopList;
        private boolean addedToShopList;
        private Button mapButton;

        @Override
        protected int getResIdFragment() {
            return R.layout.product_view_activity;
        }

        @Override
        protected int getResIdMenu() {
            return 0;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            addedToShopList = false;
            final String productUid = getActivity().getIntent().getStringExtra(INTENT_KEY_PRODUCT_UID);

            new ApiHandler<Product>(getContext())
                .checkNetState(true)
                .enableProcessView(R.string.recipe_view_wait_get_one)
                .request(new ApiHandler.FeedBack<Product>() {
                    @NonNull
                    @Override
                    public Call<Product> getCallableApi(Map<String, String> header) {
                        return ApiFactory.getProductService().getOne(productUid);
                    }

                    @Override
                    public void successful(Product result, String sessionId) {
                        if (result != null) {
                            currentProduct = result;
                            refresh();
                        }
                    }

                    @Override
                    public void viewErrorMessage(String message, int errorCode) {
                        Log.e(LOG_TAG, String.valueOf(message));
                        showErrorMessage(R.string.recipe_error_get_one);
                    }
                });

        }

        @Override
        protected void initFragment(View view) {
            getActivity().setTitle(R.string.product_view_caption);
            int captionColor = ContextCompat.getColor(getContext(), ApplicationLoader.isDarkTheme()
                ? R.color.che_text_header_dark : R.color.che_text_header_light);
            title = view.findViewById(R.id.product_view_title);
            title.setTextColor(captionColor);
            loadingProgress = view.findViewById(R.id.product_view_loading);
            image = view.findViewById(R.id.product_view_image);
//            ((TextView) view.findViewById(R.id.product_view_buys_caption))
//                .setTextColor(captionColor);
//            buys = view.findViewById(R.id.product_view_buys);
            ((TextView) view.findViewById(R.id.product_view_description_caption))
                .setTextColor(captionColor);
            description = view.findViewById(R.id.product_view_description);
            loadShopList = view.findViewById(R.id.product_view_get_shoplist_button);
            loadShopList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadToShopList();
                }
            });
            mapButton = view.findViewById(R.id.show_on_map_button);
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), MarkerMapActivity.class));
                }
            });
            //refreshStateButton();
        }

//        private void refreshStateButton() {
//            if (addedToShopList) {
//                loadShopList.setText(R.string.recipe_view_shoplist_ready_button);
//            } else {
//                loadShopList.setText(R.string.recipe_view_get_shoplist_button);
//            }
//        }

        /**
         * Загрузить в список покупок
         */
        private void loadToShopList() {
            if (currentProduct == null ) {
                return;
            }
            if (addedToShopList) {
                // Открыть список покупок
                startActivity(new Intent(getContext(), ShoppingActivity.class));
                return;
            }
//            try {
//                Shopping sl = new Shopping();
//                sl.setName(currentProduct.getTitle());
//                sl.setNew(true);
//                sl.setAuthor(getContext().getString(R.string.recipe_view_author));
//               // sl.setComment("recipe:" + currentProduct.getUId());
//                int saveId = ShoppingService.getInstance(ApplicationLoader.applicationContext).save(sl);
//
//                sl.setId(saveId);
//                if (saveId > 0) {
//                    for (Buy buy : ShopListAdapter.convert(currentProduct.getExBuyList(), sl)) {
//                        BuyService.getInstance(ApplicationLoader.applicationContext).save(buy);
//                    }
//                    addedToShopList = true;
//                    refreshStateButton();
//                    FavoriteService.getInstance(ApplicationLoader.applicationContext).save(new Favorite(currentRecipe.getId()));
//                    showInfoMessage(R.string.recipe_view_shoplist_ready_message);
//                } else {
//                    showErrorMessage(R.string.recipe_view_shoplist_error_message);
//                }
//            } catch (Exception e) {
//                Log.e(LOG_TAG, "an error load shopList recipe", e);
//            }
        }

        @Override
        protected void refresh() {
            if (currentProduct == null) {
                return;
            }
            title.setText(currentProduct.getTitle());
     //       List<String> buysView = new ArrayList<>();
//            for (ExBuy buy : currentProduct.getExBuyList()) {
//                buysView.add("- " + buy.getName()
//                    + (buy.getAmount() != null
//                    ? " - " + buy.getAmount().toString() + (StringUtils.isNotBlank(buy.getMeasure()) ? " " + buy.getMeasure() : "")
//                    : ""));
//            }
//            buys.setText(StringUtils.join(buysView, "\n"));
            description.setText(currentProduct.getDescription());


            if (StringUtils.isNotBlank(currentProduct.getSrcImage())) {
                loadingProgress.setVisibility(View.VISIBLE);
                String srcImage = currentProduct.getSrcImage();
                // Загрузить картинку
                Picasso picasso = Picasso.get();
                //picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
                picasso.load(srcImage)
                        .into(image, new Callback() {
                            @Override
                            public void onSuccess() {
                                loadingProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                loadingProgress.setVisibility(View.GONE);
                            }
                        });
            } else {
                loadingProgress.setVisibility(View.GONE);
            }
        }


    }


}
