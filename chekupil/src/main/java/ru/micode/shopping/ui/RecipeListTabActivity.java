package ru.micode.shopping.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import ru.micode.shopping.BuildConfig;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.model.ex.ExRecipe;
import ru.micode.shopping.rest.ApiFactory;
import ru.micode.shopping.rest.ApiHandler;
import ru.micode.shopping.rest.RecipeService;
import ru.micode.shopping.service.FavoriteService;
import ru.micode.shopping.ui.adapter.RecipeAdapter;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.TuningRecycler;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;
import ru.micode.shopping.ui.dialog.MessageFragment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Petr Gusarov on 29.03.19.
 */
public class RecipeListTabActivity extends MyAbstractActivity {

    @Override
    protected Fragment createFragment() {
        return new RecipeListTabFragment();
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
    public static class RecipeListTabFragment extends MyAbstractFragment {

        private static final String LOG_TAG = RecipeService.class.getSimpleName();
        private static final String TAB_ALL = "tabAll";
        private static final String TAB_FAVORITE = "tabFavorite";

        private RecipeAdapter recipeAdapter;
        private RecyclerView recyclerView;
        private RecipeAdapter recipeFavoriteAdapter;
        private RecyclerView favoriteRecyclerView;
        private AtomicBoolean lookHistory = new AtomicBoolean();

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            ItemTouchListener<ExRecipe> touchListener = new ItemTouchListener<ExRecipe>() {
                @Override
                public boolean touch(Type type, View view, List<ExRecipe> selectList, int totalSize, ExRecipe value) {
                    switch (type) {
                        case CLICK:
                            if (value != null) {
                                Intent intent = new Intent(getContext(), RecipeViewActivity.class);
                                intent.putExtra(RecipeViewActivity.RecipeViewFragment.INTENT_KEY_RECIPE_UID, value.getId());
                                startActivity(intent);
                                return true;
                            }
                            break;
                        case MODIFIED:
                            try {
                                return FavoriteService.getInstance(ApplicationLoader.applicationContext)
                                        .changeFavorite(value.getId());
                            } catch (Exception e) {
                                Log.e(LOG_TAG, "an error add to favorite", e);
                                showErrorMessage(R.string.recipe_tab_add_favorite_error_message);
                            }
                            break;
                        case BIND:
                            return FavoriteService.getInstance(ApplicationLoader.applicationContext).isFavorite(value.getId());
                    }
                    return false;
                }
            };
            recipeAdapter = new RecipeAdapter(getContext(), touchListener);
            recipeFavoriteAdapter = new RecipeAdapter(getContext(), touchListener);
            listRefresh();
        }

        @Override
        protected int getResIdFragment() {
            return R.layout.recipe_list_tab_activity;
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
            getActivity().setTitle(R.string.recipe_list_caption);
            TabHost tabHost = view.findViewById(R.id.recipe_tab_host);
            tabHost.setup();
            setupTab(tabHost, R.id.tab1, TAB_ALL, R.string.recipe_tab_all_caption);
            setupTab(tabHost, R.id.tab2, TAB_FAVORITE, R.string.recipe_tab_favorite_caption);
            tabHost.setCurrentTabByTag(TAB_ALL);
            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String s) {
                    if (TAB_FAVORITE.equals(s)) {
                        favoritesRefresh();
                    }
                }
            });
            recyclerView = TuningRecycler
                    .builder(view.findViewById(R.id.recipe_tab_recycler_view), getContext())
                    .setAdapter(recipeAdapter)
                    .getRecyclerView();
            favoriteRecyclerView = TuningRecycler
                    .builder(view.findViewById(R.id.recipe_tab_favorite_recycler_view), getContext())
                    .setAdapter(recipeFavoriteAdapter)
                    .getRecyclerView();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 0) {
                        refreshViewPositionList();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            final SharedPreferences sharedPreferences = ApplicationLoader.getSharedPreferences();
            if (sharedPreferences != null) {
                if (!sharedPreferences.getBoolean(ApplicationLoader.PrefKey.IS_VIEW_RECIPE_HELP, false)) {
                    // Отобразить подсказку
                    MessageFragment.create()
                            .show(getActivity(), R.string.help_recipe_list_message_get_start, new MessageFragment.OkListener() {
                                @Override
                                public void ok() {
                                    ApplicationLoader.setSharedPreferences(ApplicationLoader.PrefKey.IS_VIEW_RECIPE_HELP, true);
                                }
                            });
                }
            }
        }

        private void setupTab(TabHost tabHost, final int viewId, final String tag, int resId) {
            View tabview = createTabView(getContext(), resId);
            TabHost.TabSpec setContent = tabHost
                    .newTabSpec(tag)
                    .setIndicator(tabview)
                    .setContent(viewId);
            tabHost.addTab(setContent);
        }

        private static View createTabView(final Context context, final int resId) {
            View view = LayoutInflater.from(context).inflate(R.layout.tabs_layout, null);
            TextView tv = view.findViewById(R.id.tabsText);
            tv.setText(resId);
            return view;
        }

        private void listRefresh() {
            new ApiHandler<List<ExRecipe>>(getContext())
                    .enableProcessView(R.string.recipe_list_wait_get_start)
                    .checkNetState(true)
                    .request(new ApiHandler.FeedBack<List<ExRecipe>>() {
                        @NonNull
                        @Override
                        public Call<List<ExRecipe>> getCallableApi(Map<String, String> header) {
                            return ApiFactory.getRecipeService().getStart();
                        }

                        @Override
                        public void successful(List<ExRecipe> result, String sessionId) {
                            if (!result.isEmpty()) {
                                recipeAdapter.clear();
                                recipeAdapter.addAll(result);
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

        private void favoritesRefresh() {
            final List<String> allUid = FavoriteService.getInstance(getContext()).findAllUid();
            if (allUid.isEmpty()) {
                return;
            }
            recipeFavoriteAdapter.clear();
            new ApiHandler<List<ExRecipe>>(getContext())
                    .checkNetState(true)
                    .enableProcessView(R.string.recipe_list_wait_favorite)
                    .request(new ApiHandler.FeedBack<List<ExRecipe>>() {
                        @NonNull
                        @Override
                        public Call<List<ExRecipe>> getCallableApi(Map<String, String> header) {
                            return ApiFactory.getRecipeService().getFavorite(allUid);
                        }

                        @Override
                        public void successful(List<ExRecipe> result, String sessionId) {
                            recipeFavoriteAdapter.addAll(result);
                        }

                        @Override
                        public void viewErrorMessage(String message, int errorCode) {
                            if (StringUtils.isBlank(message)) {
                                showErrorMessage(R.string.recipe_error_load_favorite);
                            } else {
                                showErrorMessage(String.valueOf(message));
                            }
                        }
                    });
        }

        private void refreshViewPositionList() {
//            int first = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            Log.i(LOG_TAG, "last =" + last + " count=" + recipeAdapter.getItemCount());
            if (last > recipeAdapter.getItemCount() - 2) {
                //Запрос на историю
                if (!lookHistory.get()) {
                    lookHistory.set(true);
                    new ApiHandler<List<ExRecipe>>(getContext())
                            .checkNetState(true)
                            .request(new ApiHandler.FeedBack<List<ExRecipe>>() {
                                @Override
                                public Call<List<ExRecipe>> getCallableApi(Map<String, String> header) {
                                    ExRecipe lastRecipe = recipeAdapter.getByPosition(recipeAdapter.getItemCount() - 1);
                                    long time = 0;
                                    if (lastRecipe != null) {
                                        time = lastRecipe.getEditTime();
                                    }
                                    Log.i(LOG_TAG, "last time for history =" + time);
                                    return ApiFactory.getRecipeService().getNext(time);
                                }

                                @Override
                                public void successful(List<ExRecipe> result, String sessionId) {
                                    lookHistory.set(false);
                                    if (!result.isEmpty()) {
                                        recipeAdapter.addAll(result);
                                    }
                                }

                                @Override
                                public void viewErrorMessage(String message, int errorCode) {
                                    lookHistory.set(false);
                                    Log.w(LOG_TAG, "an error get history");
                                }
                            });
                }
            }
        }
    }
}
