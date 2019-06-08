package ru.micode.shopping.ui;

import java.util.List;
import java.util.TreeSet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.service.GroupService;
import ru.micode.shopping.ui.adapter.NavigatorAdapter;
import ru.micode.shopping.ui.adapter.NavigatorBean;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;

/**
 * Навигационный экран
 * Created by Petr Gusarov on 22.02.18.
 */
public class NavigatorActivity extends MyAbstractActivity {

    @Override
    protected Fragment createFragment() {
        return new NavigatorFragment();
    }

    @Override
    protected boolean isViewBackButton() {
        return false;
    }

    @Override
    protected boolean isExit() {
        exit();
        return false;
    }

    /**
     * Фрагмент
     */
    public static class NavigatorFragment extends MyAbstractFragment {

        private static final String LOG_TAG = NavigatorFragment.class.getSimpleName();
        private TreeSet<NavigatorBean> navigatorReg = new TreeSet<>();
        private NavigatorAdapter navigatorAdapter;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ApplicationLoader.workActivity(true);
        }

        @Override
        public void onDestroy() {
            ApplicationLoader.workActivity(false);
            super.onDestroy();
        }

        @Override
        protected int getResIdFragment() {
            return R.layout.navigator_activity;
        }

        @Override
        protected int getResIdMenu() {
            return R.menu.navigator_menu;
        }

        @Override
        protected void initFragment(View view) {
            final RecyclerView navigatorRecycler = (RecyclerView) view.findViewById(R.id.navigator_list);
            navigatorRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                navigatorRecycler.getContext(),
                LinearLayoutManager.VERTICAL);
            navigatorRecycler.addItemDecoration(dividerItemDecoration);
            navigatorAdapter = new NavigatorAdapter(getContext(), new ItemTouchListener<NavigatorBean>() {
                @Override
                public boolean touch(Type type, View view, List<NavigatorBean> selectList, int totalSize, NavigatorBean value) {
                    switch (type) {
                        case CLICK:
                            startActivity(new Intent(getContext(), value.getActivityClass()));
                            return true;
                    }
                    return false;
                }
            });
            navigatorRecycler.setAdapter(navigatorAdapter);

            FloatingActionButton floatHelpButton = view.findViewById(R.id.float_help_navigator);
            int colorHelp = ApplicationLoader.isDarkTheme()
                ? ApplicationLoader.getContextColor(R.color.che_float_help_button_dark)
                : ApplicationLoader.getContextColor(R.color.che_float_help_button_light);
            floatHelpButton.setBackgroundTintList(ColorStateList.valueOf(colorHelp));
            floatHelpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), HelpActivity.class);
                    intent.putExtra(HelpActivity.KEY_RES_ID_TEXT, R.string.help_common_text);
                    startActivity(intent);
                }
            });
            GroupService.getInstance(getContext()).createDirectDefault();
            // Стартовое обновление данных
            Log.i(LOG_TAG, "run syncRefreshServer");
        }

        @Override
        protected void refresh() {
            ApplicationLoader.checkTheme(getActivity());
            navigatorReg.clear();
            int pos = 0;
            navigatorReg.add(new NavigatorBean(pos++, R.drawable.ic_nav_shoplist, R.string.navigator_list, R.string.navigator_list_description, ShoppingActivity.class));
            navigatorReg.add(new NavigatorBean(pos++, R.drawable.ic_restaurant, R.string.navigator_product, R.string.navigator_product_description, ProductListActivity.class));
            navigatorReg.add(new NavigatorBean(pos++, R.drawable.ic_nav_recipe, R.string.navigator_recipe, R.string.navigator_recipe_description, RecipeListTabActivity.class));
            navigatorReg.add(new NavigatorBean(pos++, R.drawable.ic_nav_groups, R.string.navigator_group, R.string.navigator_group_description, GroupActivity.class));
            navigatorReg.add(new NavigatorBean(pos++, R.drawable.ic_nav_settings, R.string.navigator_setting, R.string.navigator_setting_description, PrefActivity.class));
            navigatorAdapter.clear();
            navigatorAdapter.addAll(navigatorReg);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigator_menu_exit:
                exit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exit() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.navigator_exit_title)
            .setMessage(R.string.navigator_exit_message)
            .setNegativeButton(R.string.navigator_exit_caption_no, null)
            .setPositiveButton(R.string.navigator_exit_caption_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .create().show();
    }
}
