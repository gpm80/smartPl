package ru.micode.shopping.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.service.ServiceException;
import ru.micode.shopping.service.ShoppingService;
import ru.micode.shopping.ui.adapter.ShoppingAdapter;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.TuningRecycler;
import ru.micode.shopping.ui.common.CloseFragmentListener;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;
import ru.micode.shopping.ui.dialog.ConfirmDialog;
import ru.micode.shopping.ui.dialog.ShoppingNameDialog;

/**
 * Списки покупок
 * Created by Petr Gusarov on 15.03.18.
 */
public class ShoppingActivity extends MyAbstractActivity {

    @Override
    protected Fragment createFragment() {
        return new ShoppingFragment();
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
     * Фрагмент
     */
    public static class ShoppingFragment extends MyAbstractFragment implements CloseFragmentListener {

        private static final String LOG_TAG = ShoppingFragment.class.getSimpleName();
        private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        private static final int REQUEST_RENAME_SHOPPING = 103;
        private static final int REQUEST_CREATE_SHOPPING = 104;
        private ShoppingAdapter shoppingAdapter;
        private ShoppingService shoppingService;
        private RecyclerView recyclerView;
        private TextView emptyText;
        private boolean selectMode;
        private UpdateReceiver updateReceiver;

        public class UpdateReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(LOG_TAG, "incoming broadcast");
                refresh();
            }
        }

        @Override
        protected int getResIdFragment() {
            return R.layout.shopping_activity;
        }

        @Override
        protected int getResIdMenu() {
            return R.menu.shopping_menu;
        }

        @Override
        protected void initFragment(View view) {
            getActivity().setTitle(R.string.navigator_list);
            shoppingAdapter = new ShoppingAdapter(getContext(), new ItemTouchListener<Shopping>() {
                @Override
                public boolean touch(Type type, View view, List<Shopping> selectList, int totalSize, Shopping value) {
                    switch (type) {
                        case CLICK:
                            openShopping(value);
                            return true;
                        case SELECT:
                            changeSelectMode(selectList.size(), totalSize);
                            return true;
                    }
                    return false;
                }
            });
            recyclerView = TuningRecycler
                .builder((RecyclerView) view.findViewById(R.id.shopping_list_view), getContext())
                .addDivider()
                .setAdapter(shoppingAdapter)
                .getRecyclerView();
            emptyText = (TextView) view.findViewById(R.id.shopping_empty_list_text);
            // Плавающая кнопа
            view.findViewById(R.id.float_new_shopping)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createNewShopping();
                    }
                });
            shoppingService = ShoppingService.getInstance(getContext());
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            updateReceiver = new UpdateReceiver();
        }

        @Override
        public void onResume() {
            // Создаём и регистрируем широковещательный приёмник
            if (getActivity() != null) {
                getActivity().registerReceiver(updateReceiver, new IntentFilter(ApplicationLoader.shoplistBroadcastAction));
                if(!ApplicationLoader.isOnNotificationService()){
                }
                Log.i(LOG_TAG, "register receiver " + getActivity().toString());
            }
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            try {
                if (getActivity() != null) {
                    Log.i(LOG_TAG, "unregister receiver " + getActivity().toString());
                    getActivity().unregisterReceiver(updateReceiver);
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, "an error unregister receiver", e);
            }
        }

        @Override
        protected void refresh() {
            if (getCurrentMenu() != null) {
                changeSelectMode(0, 0);
            }
            shoppingAdapter.clear();
            List<Shopping> all = ShoppingService.getInstance(getContext()).findAll();
            if (checkNotViewEmpty(all.isEmpty(), recyclerView, emptyText)) {
                shoppingAdapter.addAll(all);
            }

        }

        /**
         * Смена состояния фрагмента
         *
         * @param selectCount выделено позиций
         * @param totalCount  всего позиций
         */
        private void changeSelectMode(int selectCount, int totalCount) {
            selectMode = selectCount > 0;
            if (selectMode) {
                getCurrentMenu().setGroupVisible(R.id.shopping_menu_group_one, selectCount == 1);
                getCurrentMenu().setGroupVisible(R.id.shopping_menu_group_many, true);
                getActivity().setTitle(String.format(
                    getString(R.string.shopping_title_activity_selected),
                    String.valueOf(selectCount), String.valueOf(totalCount)));
            } else {
                getCurrentMenu().setGroupVisible(R.id.shopping_menu_group_one, false);
                getCurrentMenu().setGroupVisible(R.id.shopping_menu_group_many, false);
                getActivity().setTitle(R.string.navigator_list);
            }
        }

        /**
         * Открывает список
         *
         * @param shopping список
         */
        private void openShopping(Shopping shopping) {
            if (shopping != null && shopping.getId() != null) {
                Intent buyIntent = new Intent(getContext(), BuyEditActivity.class);
                buyIntent.putExtra(BuyEditActivity.EXTRA_PARENT_LIST_ID, shopping.getId());
                startActivity(buyIntent);
            }
        }

        /**
         * Создать новый список
         */
        private void createNewShopping() {
            String name = String.format(
                getResources().getText(R.string.shopping_new_name_template).toString(),
                sdf.format(new Date()));
            Shopping shopping = new Shopping();
            shopping.setName(name);
            ShoppingNameDialog.newInstance(shopping)
                .target(this, REQUEST_CREATE_SHOPPING)
                .show(getActivity());
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_RENAME_SHOPPING) {
                if (Activity.RESULT_OK == resultCode) {
                    Shopping shopping = (Shopping) data.getSerializableExtra(ShoppingNameDialog.RESULT_SHOP);
                    try {
                        // TODO Проверить заполнение названия
                        shoppingService.save(shopping);
                        refresh();
                    } catch (ServiceException e) {
                        showErrorMessage(e);
                    }
                }
            } else if (requestCode == REQUEST_CREATE_SHOPPING) {
                if (Activity.RESULT_OK == resultCode) {
                    Shopping shopping = (Shopping) data.getSerializableExtra(ShoppingNameDialog.RESULT_SHOP);
                    try {
                        shopping.setId(shoppingService.save(shopping));
                        openShopping(shopping);
                    } catch (ServiceException e) {
                        showErrorMessage(e);
                    }
                }
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.shopping_menu_edit:
                    List<Shopping> selectedItems = shoppingAdapter.getSelectValues();
                    if (selectedItems.size() == 1) {
                        Shopping shopping = selectedItems.get(0);
                        ShoppingNameDialog.newInstance(shopping)
                            .target(ShoppingFragment.this, REQUEST_RENAME_SHOPPING)
                            .show(getActivity());
                    }
                    return true;
                case R.id.shopping_menu_delete:
                    ConfirmDialog.builder(getContext())
                        .title(R.string.shopping_dialog_delete_title)
                        .message(R.string.shopping_dialog_delete_message)
                        .positiveCaption(R.string.shopping_dialog_delete_yes)
                        .negativeCaption(R.string.shopping_dialog_delete_no)
                        .show(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Dialog.BUTTON_POSITIVE == which) {
                                    try {
                                        for (Shopping shopping : shoppingAdapter.getSelectValues()) {
                                            shoppingService.delete(shopping);
                                        }
                                        showInfoMessage(R.string.shopping_delete_success);
                                    } catch (ServiceException e) {
                                        showErrorMessage(e);
                                    }
                                    refresh();
                                }
                                dialog.dismiss();
                            }
                        });
                    return true;
            }
            return false;
        }

        @Override
        public boolean beforeClose() {
            if (selectMode) {
                shoppingAdapter.deselectionAll();
                changeSelectMode(0, 0);
                return false;
            }
            return true;
        }
    }

}
