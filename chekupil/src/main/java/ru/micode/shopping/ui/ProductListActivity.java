package ru.micode.shopping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import ru.micode.shopping.R;
import ru.micode.shopping.rest.ApiFactory;
import ru.micode.shopping.rest.ApiHandler;
import ru.micode.shopping.rest.RecipeService;
import ru.micode.shopping.ui.adapter.ProductAdapter;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.TuningRecycler;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;
import ru.smart.planet.web.Product;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Petr Gusarov on 29.03.19.
 */
public class ProductListActivity extends MyAbstractActivity {

    @Override
    protected Fragment createFragment() {
        return new ProductListFragment();
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
     * Фрагмент рецептов
     */
    public static class ProductListFragment extends MyAbstractFragment {

        private static final String LOG_TAG = RecipeService.class.getSimpleName();

        private ProductAdapter productAdapter;
        private RecyclerView recyclerView;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            ItemTouchListener<Product> touchListener = new ItemTouchListener<Product>() {
                @Override
                public boolean touch(Type type, View view, List<Product> selectList, int totalSize, Product value) {
                    switch (type) {
                        case CLICK:
                            if (value != null) {
                                Intent intent = new Intent(getContext(), ProductViewActivity.class);
                                intent.putExtra(ProductViewActivity.ProductViewFragment.INTENT_KEY_PRODUCT_UID, value.getUid());
                                startActivity(intent);
                                return true;
                            }
                            break;
                    }
                    return false;
                }
            };
            productAdapter = new ProductAdapter(getContext(), touchListener);
            listRefresh();
        }

        @Override
        protected int getResIdFragment() {
            return R.layout.product_list_activity;
        }

        @Override
        protected int getResIdMenu() {
            return 0;
        }

        @Override
        protected void refresh() {
        }

        @Override
        protected void initFragment(View view) {
            getActivity().setTitle(R.string.product_list_caption);
            recyclerView = TuningRecycler
                    .builder(view.findViewById(R.id.product_recycler_view), getContext())
                    .setAdapter(productAdapter)
                    .getRecyclerView();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 0) {
//                        refreshViewPositionList();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }



        private void listRefresh() {
            new ApiHandler<List<Product>>(getContext())
                    .enableProcessView(R.string.product_list_wait_get_start)
                    .checkNetState(true)
                    .request(new ApiHandler.FeedBack<List<Product>>() {
                        @NonNull
                        @Override
                        public Call<List<Product>> getCallableApi(Map<String, String> header) {
                            return ApiFactory.getProductService().getAll();
                        }

                        @Override
                        public void successful(List<Product> result, String sessionId) {
                            if (!result.isEmpty()) {
                                productAdapter.clear();
                                productAdapter.addAll(result);
                            }
                        }

                        @Override
                        public void viewErrorMessage(String message, int errorCode) {
                            if (StringUtils.isBlank(message)) {
                                showErrorMessage(R.string.recipe_error_load_list);
                            } else {
                                showErrorMessage(String.valueOf(message));
                            }
                            Log.e(LOG_TAG, "an error get start messages");
                        }
                    });
        }



//        private void refreshViewPositionList() {
////            int first = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//            int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//            Log.i(LOG_TAG, "last =" + last + " count=" + productAdapter.getItemCount());
//            if (last > productAdapter.getItemCount() - 2) {
//                //Запрос на историю
//                if (!lookHistory.get()) {
//                    lookHistory.set(true);
//                    new ApiHandler<List<ExRecipe>>(getContext())
//                            .checkNetState(true)
//                            .request(new ApiHandler.FeedBack<List<ExRecipe>>() {
//                                @Override
//                                public Call<List<ExRecipe>> getCallableApi(Map<String, String> header) {
//                                    ExRecipe lastRecipe = productAdapter.getByPosition(productAdapter.getItemCount() - 1);
//                                    long time = 0;
//                                    if (lastRecipe != null) {
//                                        time = lastRecipe.getEditTime();
//                                    }
//                                    Log.i(LOG_TAG, "last time for history =" + time);
//                                    return ApiFactory.getRecipeService().getNext(time);
//                                }
//
//                                @Override
//                                public void successful(List<ExRecipe> result, String sessionId) {
//                                    lookHistory.set(false);
//                                    if (!result.isEmpty()) {
//                                        productAdapter.addAll(result);
//                                    }
//                                }
//
//                                @Override
//                                public void viewErrorMessage(String message, int errorCode) {
//                                    lookHistory.set(false);
//                                    Log.w(LOG_TAG, "an error get history");
//                                }
//                            });
//                }
//            }
//        }
    }
}
