package ru.micode.shopping.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.service.BuyService;
import ru.micode.shopping.service.ServiceException;
import ru.micode.shopping.service.ShoppingService;
import ru.micode.shopping.ui.adapter.BuyAdapter;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;
import ru.micode.shopping.ui.dialog.BuyDialog;
import ru.micode.shopping.ui.dialog.ConfirmFragment;
import ru.micode.shopping.ui.dialog.MessageFragment;
import ru.micode.shopping.ui.dialog.ShoppingNameDialog;

import java.util.List;

/**
 * Активность для редактирования списка
 * Created by Petr Gusarov on 20.03.18.
 */
public class BuyEditActivity extends MyAbstractActivity {

    public static final int CODE_SELECT_FRIEND = 101;
    public static final int REQUEST_EDIT_BUY = 102;
    public static final int REQUEST_EDIT_SHOP_NAME = 103;
    public static final String LOG_TAG = BuyEditActivity.class.getSimpleName();
    public static final String EXTRA_PARENT_LIST_ID = "parentListId";


    @Override
    protected boolean isViewBackButton() {
        return true;
    }

    @Override
    protected boolean isExit() {
        return true;
    }

    @Override
    protected Fragment createFragment() {
        return new BuyFragment();
    }

    /**
     * Класс фрагмента покупок
     */
    public static class BuyFragment extends MyAbstractFragment {
        private BuyAdapter buyAdapter;
        private BuyService buyService;
        private Shopping parentShopping;
        private RecyclerView recyclerView;
        private TextView emptyText;
        private String lastUseGroupName;
        private String lastUseMeasure;

        @Override
        protected int getResIdFragment() {
            return R.layout.buy_edit_activity;
        }

        @Override
        protected int getResIdMenu() {
            return R.menu.buy_menu;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void initFragment(View view) {
            recyclerView = view.findViewById(R.id.buy_recycler_view);
            emptyText = view.findViewById(R.id.buy_empty_list_text);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            buyAdapter = new BuyAdapter(getContext(), new ItemTouchListener<Buy>() {
                @Override
                public boolean touch(Type type, View view, List<Buy> selectList, int totalSize, Buy value) {
                    switch (type) {
                        case CLICK:
                            openBuy(value);
                            return true;
                        case LONG_CLICK:
                            buyService.negativeDone(value);
                            buyAdapter.refresh();
                            return true;
                    }
                    return false;
                }
            });
            recyclerView.setAdapter(buyAdapter);
            view.findViewById(R.id.float_new_buy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBuy();
                }
            });
            int parentId = getActivity().getIntent().getIntExtra(EXTRA_PARENT_LIST_ID, -1);
            // Получим родительский список
            parentShopping = ShoppingService.getInstance(getContext()).findOne(parentId);
            if (parentShopping == null) {
                finish();
            }
            buyService = BuyService.getInstance(getContext());
            getActivity().setTitle(parentShopping.getName());

            final SharedPreferences sharedPreferences = ApplicationLoader.getSharedPreferences();
            if (sharedPreferences != null) {
                if (!sharedPreferences.getBoolean(ApplicationLoader.PrefKey.IS_VIEW_BUY_HELP, false)) {
                    // Отобразить подсказку
                    MessageFragment.create()
                            .show(getActivity(), R.string.help_buy_message_get_start, new MessageFragment.OkListener() {
                                @Override
                                public void ok() {
                                    ApplicationLoader.setSharedPreferences(ApplicationLoader.PrefKey.IS_VIEW_BUY_HELP, true);
                                }
                            });
                }
            }
        }

        private void searchBuy() {
            Intent searchIntent = new Intent(getContext(), AddBuySearchActivity.class);
            searchIntent.putExtra(AddBuySearchActivity.EXTRA_PARENT_SHOPLIST_ID, parentShopping.getId());
            startActivity(searchIntent);
        }

        /**
         * Обновление источника данных
         */
        protected void refresh() {
            buyAdapter.clear();
            List<Buy> buys = buyService.findAllByShopping(parentShopping);
            if (checkNotViewEmpty(buys.isEmpty(), recyclerView, emptyText)) {
                buyAdapter.addAll(buys);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.buy_menu_rename_shop_list:
                    renameShopping();
                    break;
                case R.id.buy_menu_delete_select_buy:
                    deleteSelected();
                    break;
                case R.id.buy_menu_sort_group:


                    // Сортировка групп списка
                    Intent intent = new Intent(getActivity(), GroupActivity.class);
                    intent.putExtra(GroupActivity.KEY_SORT_GROUP_BY_SHOPLIST, parentShopping.getId());
                    startActivity(intent);
                    break;
                case R.id.buy_menu_deselect_all_shopping:
                    setAllDoneValue(false);
                    break;
                case R.id.buy_menu_select_all_shopping:
                    setAllDoneValue(true);
                    break;
                case R.id.buy_menu_inversion_all:
                    ConfirmFragment.create(ConfirmFragment.TypeMessage.INVERSION_ALL_ITEMS,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Dialog.BUTTON_POSITIVE == which) {
                                        if (buyService.inversionDoneByShopping(parentShopping)) {
                                            Toast.makeText(getContext(), "Готово", Toast.LENGTH_SHORT).show();
                                        }
                                        refresh();
                                    }
                                }
                            }).show(getActivity());
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        /**
         * Переименовывает список
         */
        private void renameShopping() {
            ShoppingNameDialog.newInstance(parentShopping)
                    .target(this, REQUEST_EDIT_SHOP_NAME)
                    .show(getActivity());
        }

        /**
         * Устанавливает состояние покупки в указанное положение
         *
         * @param done
         */
        private void setAllDoneValue(final boolean done) {
            ConfirmFragment.TypeMessage type = done
                    ? ConfirmFragment.TypeMessage.SELECT_ALL_ITEMS
                    : ConfirmFragment.TypeMessage.DESELECT_ALL_ITEMS;
            ConfirmFragment.create(type, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Dialog.BUTTON_POSITIVE == which) {
                        if (
                                buyService.setDoneAllItemsByShopping(parentShopping, done)) {
                            Toast.makeText(getContext(), "Готово", Toast.LENGTH_SHORT).show();
                        }
                        refresh();
                    }
                }
            }).show(getActivity());
        }

        /**
         * Удаляет отмеченные
         */
        private void deleteSelected() {
            ConfirmFragment.create(ConfirmFragment.TypeMessage.DELETE_BUY_SELECT,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Dialog.BUTTON_POSITIVE == which) {
                                if (buyService.deleteIsDoneByShopping(parentShopping)) {
                                    Toast.makeText(getContext(), "Готово", Toast.LENGTH_SHORT).show();
                                }
                                refresh();
                            }
                        }
                    }).show(getActivity());
        }

        /**
         * Открывает покупку на создание/редактирование
         *
         * @param buy если null то создает новую, иначе открывает.
         */
        private void openBuy(Buy buy) {
            if (buy == null) {
                buy = new Buy();
                buy.setNameGroup(lastUseGroupName);
                buy.setMeasure(lastUseMeasure);
            }
            BuyDialog buyDialog = BuyDialog.newInstance(buy);
            buyDialog.setTargetFragment(this, REQUEST_EDIT_BUY);
            buyDialog.show(getActivity());
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (REQUEST_EDIT_SHOP_NAME == requestCode) {
                if (resultCode == RESULT_OK) {
                    try {
                        Shopping resultShop = (Shopping) data.getSerializableExtra(ShoppingNameDialog.RESULT_SHOP);
                        parentShopping.setName(resultShop.getName());
                        ShoppingService.getInstance(getContext()).save(parentShopping);
                        getActivity().setTitle(resultShop.getName());
                    } catch (ServiceException e) {
                        showErrorMessage(e);
                    }
                }
            } else if (REQUEST_EDIT_BUY == requestCode) {
                // Результат редактирования/создания покупки
                if (resultCode == RESULT_OK) {
                    Buy value = (Buy) data.getSerializableExtra(BuyDialog.RESULT_BUY);
                    lastUseGroupName = value.getNameGroup();
                    lastUseMeasure = value.getMeasure();
                    value.setShopping(parentShopping);
                    try {
                        int id = buyService.save(value);
                        if (value.getId() == null) {
                            value.setId(id);
                            buyAdapter.add(value);
                        }
                        buyAdapter.refresh();
                    } catch (ServiceException sex) {
                        showErrorMessage(sex);
                    } finally {
                        refresh();
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
