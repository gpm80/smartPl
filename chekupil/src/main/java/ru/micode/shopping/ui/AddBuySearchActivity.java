package ru.micode.shopping.ui;

import java.util.List;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.SearchBuy;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.service.BuyService;
import ru.micode.shopping.service.GroupService;
import ru.micode.shopping.service.ServiceException;
import ru.micode.shopping.service.ShoppingService;
import ru.micode.shopping.ui.adapter.BuyNewSearchAdapter;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;
import ru.micode.shopping.ui.dialog.BuyDialog;

/**
 * Created by Petr Gusarov on 19.11.18.
 */
public class AddBuySearchActivity extends MyAbstractActivity {

    public static final String EXTRA_PARENT_SHOPLIST_ID = "parentShopListId";

    @Override
    protected Fragment createFragment() {
        return new AddBuySearchFragment();
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
     * Фрагмент поиска и добавления покупок в список
     * Created by Petr Gusarov on 01.10.18.
     */
    public static class AddBuySearchFragment extends MyAbstractFragment {

        public static final int REQUEST_EDIT_BUY = 102;
        private BuyNewSearchAdapter buyNewSearchAdapter;
        private RecyclerView searchRecycler;
        private SearchBuy search;
        private Shopping currentShopping;
        private String lastUseGroupName;
        private String lastUseMeasure;
        private SearchBuy.WrapperBuy currentEditItem;


        @Override
        protected int getResIdFragment() {
            return R.layout.buy_add_buy_fragment;
        }

        @Override
        protected int getResIdMenu() {
            return 0;
        }

        @Override
        protected void initFragment(View view) {
            int parentShopListId = getActivity().getIntent().getIntExtra(EXTRA_PARENT_SHOPLIST_ID, -1);
            this.currentShopping = ShoppingService.getInstance(getContext()).findOne(parentShopListId);
            if (currentShopping == null) {
                finish();
            }
            getActivity().setTitle(currentShopping.getName());
            EditText searchName = view.findViewById(R.id.buy_search_text);
            searchRecycler = view.findViewById(R.id.buy_search_recycler_view);
            searchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            search = new SearchBuy(getContext(),
                BuyService.getInstance(getContext()).findAllByShopping(currentShopping),
                GroupService.getInstance(getContext()).findAllAndJoinShopping(currentShopping, null));
            buyNewSearchAdapter = new BuyNewSearchAdapter(getContext(), new ItemTouchListener<SearchBuy.WrapperBuy>() {
                @Override
                public boolean touch(Type type, View view, List<SearchBuy.WrapperBuy> selectList, int totalSize, SearchBuy.WrapperBuy value) {
                    switch (type) {
                        case CLICK:
                            addSelectSearchBuy(value, false);
                            return true;
                        case LONG_CLICK:
                            addSelectSearchBuy(value, true);
                            return true;
                    }
                    return false;
                }
            });

            searchRecycler.setAdapter(buyNewSearchAdapter);
            buyNewSearchAdapter.addAll(search.allItems());
            searchName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String s = editable.toString();
                    buyNewSearchAdapter.clear();
                    if (s.length() > 0) {
                        buyNewSearchAdapter.addAll(search.filterBy(s));
                    } else {
                        buyNewSearchAdapter.addAll(search.allItems());
                    }
                }
            });
            FloatingActionButton floatHelpButton = view.findViewById(R.id.float_help_buy_add);
            int colorHelp = ApplicationLoader.isDarkTheme()
                ? ApplicationLoader.getContextColor(R.color.che_float_help_button_dark)
                : ApplicationLoader.getContextColor(R.color.che_float_help_button_light);
            floatHelpButton.setBackgroundTintList(ColorStateList.valueOf(colorHelp));
            floatHelpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), HelpActivity.class);
                    intent.putExtra(HelpActivity.KEY_RES_ID_TEXT, R.string.help_buy_add_text);
                    startActivity(intent);
                }
            });
        }

        /**
         * Добавляет позицию в список
         *
         * @param searchItem
         */
        private void addSelectSearchBuy(SearchBuy.WrapperBuy searchItem, boolean beforeEdit) {
            Buy b = searchItem.getBuy();
            if (searchItem.isManual()) {
                beforeEdit = true;
                b.setAmount(1);
                if (lastUseMeasure != null) {
                    b.setMeasure(lastUseMeasure);
                }
                if (lastUseGroupName != null) {
                    b.setNameGroup(lastUseGroupName);
                }
            }
            if (beforeEdit) {
                currentEditItem = searchItem;
                openBuyDialog(b);
            } else {
                b.setAmount(b.getAmount() + 1);
                int count = saveToList(b);
                if (count == 0) {
                    b.setAmount(0);
                }
                buyNewSearchAdapter.refreshPos(searchItem.getPosition());
            }
        }

        /**
         * Открывает диалог редактирования добавляемой позиции товара
         *
         * @param buy покупка
         */
        private void openBuyDialog(Buy buy) {
            if (buy == null) {
                buy = new Buy();
                buy.setNameGroup(lastUseGroupName);
                buy.setMeasure(lastUseMeasure);
            }
            if (buy.getAmount() == 0) {
                buy.setAmount(1);
            }
            BuyDialog buyDialog = BuyDialog.newInstance(buy);
            buyDialog.setTargetFragment(this, REQUEST_EDIT_BUY);
            buyDialog.show(getActivity());
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_EDIT_BUY) {
                if (resultCode == RESULT_OK) {
                    Buy buy = (Buy) data.getSerializableExtra(BuyDialog.RESULT_BUY);
                    if (buy != null) {
                        saveToList(buy);
                        lastUseGroupName = buy.getNameGroup();
                        lastUseMeasure = buy.getMeasure();
                        if (currentEditItem != null) {
                            if (currentEditItem.isManual()) {
                                //Добавить его в коллекцию
                                search.addItem(buy);
                                currentEditItem.setManual(false);
                            }
                            currentEditItem.getBuy().setAmount(buy.getAmount());
                            buyNewSearchAdapter.refreshPos(currentEditItem.getPosition());
                        }
                    }
                }
                currentEditItem = null;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        /**
         * Сохраняет покупку в список
         *
         * @param buyForSave
         * @return количество по сохраненной позиции
         */
        private int saveToList(Buy buyForSave) {
            try {
                buyForSave.setShopping(currentShopping);
                int save = BuyService.getInstance(getContext()).save(buyForSave);
                if (save > 0) {
                    buyForSave.setId(save);
                    return buyForSave.getAmount();
                }
            } catch (ServiceException e) {
                Log.e(this.getClass().getSimpleName(), "", e);
                Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_LONG).show();
            }
            return 0;
        }

        @Override
        protected void refresh() {
        }
    }
}
