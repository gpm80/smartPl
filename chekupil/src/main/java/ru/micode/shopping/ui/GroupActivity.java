package ru.micode.shopping.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.R;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Group;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.service.BuyService;
import ru.micode.shopping.service.GroupService;
import ru.micode.shopping.service.ServiceException;
import ru.micode.shopping.service.ShoppingService;
import ru.micode.shopping.ui.adapter.GroupAdapter;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.TuningRecycler;
import ru.micode.shopping.ui.common.CloseFragmentListener;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;
import ru.micode.shopping.ui.dialog.ConfirmDialog;
import ru.micode.shopping.ui.dialog.GroupNameDialog;

/**
 * Группы товаров
 * Created by Petr Gusarov on 14.05.18.
 */
public class GroupActivity extends MyAbstractActivity {

    public static final String KEY_SORT_GROUP_BY_SHOPLIST = GroupActivity.class.getSimpleName() + ":sort-group-by-shoplist";

    @Override
    protected Fragment createFragment() {
        return new GroupFragment();
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
     * Фрагмент контента
     */
    public static class GroupFragment extends MyAbstractFragment implements CloseFragmentListener {

        private static final String LOG_TAG = GroupActivity.class.getSimpleName();
        private static final int REQUEST_RENAME_GROUP = 100;
        private GroupAdapter groupAdapter;
        private GroupService groupService;
        private RecyclerView recyclerView;
        private TextView emptyText;
        private Button saveSortForShopping;
        private Shopping sortingShopList;
        private boolean modified;
        private boolean selectMode;

        @Override
        protected int getResIdFragment() {
            return R.layout.group_activity;
        }

        @Override
        protected int getResIdMenu() {
            return R.menu.group_select_menu;
        }

        @Override
        protected void initFragment(final View view) {
            groupService = GroupService.getInstance(getContext());
            saveSortForShopping = (Button) view.findViewById(R.id.group_sort_save);
            saveSortForShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveSortGroupsShopping();
                }
            });
            View floatingButton = view.findViewById(R.id.float_new_group);
            final int sortByIdShoplist = getActivity().getIntent().getIntExtra(KEY_SORT_GROUP_BY_SHOPLIST, 0);
            if (sortByIdShoplist > 0) {
                sortingShopList = ShoppingService.getInstance(getContext()).findOne(sortByIdShoplist);
                floatingButton.setVisibility(View.GONE);
            } else {
                floatingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Добавить новую группу
                        createEditGroup(null);
                    }
                });
            }
            groupAdapter = new GroupAdapter(getContext(), new ItemTouchListener<Group>() {
                @Override
                public boolean touch(Type type, View view, List<Group> selectList, int totalSize, Group value) {
                    switch (type) {
                        case MODIFIED:
                            modified = true;
                            return true;
                        case CLICK:
                            if (sortingShopList == null) {
                                createEditGroup(value);
                            }
                            return true;
                        case SELECT:
                            // Выделение позиции
                            changeSelectMode(selectList.size(), totalSize);
                            return true;
                    }
                    return false;
                }
            });
            if (sortingShopList != null) {
                groupAdapter.setDeselectable();
            }
            recyclerView = TuningRecycler.builder(view.findViewById(R.id.group_list), getContext())
                .addDivider()
                .setAdapter(groupAdapter)
                .getRecyclerView();
            groupAdapter.setRecyclerParent(recyclerView);
            emptyText = view.findViewById(R.id.group_empty_list_text);
        }

        /**
         * Смена состояния фрагмента
         *
         * @param selectCount выделено позиций
         * @param totalCount  всего позиций
         */
        private void changeSelectMode(int selectCount, int totalCount) {
            if (selectCount > 0) {
                selectMode = true;
                getActivity().setTitle(String.format(
                    getString(R.string.group_title_activity_selected),
                    String.valueOf(selectCount), String.valueOf(totalCount)));
            } else {
                selectMode = false;
                getActivity().setTitle(R.string.navigator_group);
            }
            getCurrentMenu().setGroupVisible(R.id.group_menu_group_common, selectMode);
        }

        @Override
        protected void refresh() {
            if (getCurrentMenu() != null) {
                changeSelectMode(0, 0);
            }
            groupAdapter.clear();
            if (sortingShopList != null) {
                saveSortForShopping.setVisibility(View.VISIBLE);
                getActivity().setTitle(
                    String.format(
                        (String) getContext().getText(R.string.group_title_activity_sort_shopping),
                        sortingShopList.getName())
                );
                List<Buy> buyInShopping = BuyService.getInstance(getContext()).findAllByShopping(sortingShopList);
                Set<Group> groups = new HashSet<>();
                for (Buy buy : buyInShopping) {
                    groups.add(new Group(buy.getNameGroup(), buy.getPositionGroup()));
                }
                TreeSet<Group> sortGroup = new TreeSet<>(groups);
                groupAdapter.addAll(sortGroup);
                // Сортировка групп листа
            } else {
                saveSortForShopping.setVisibility(View.GONE);
                getActivity().setTitle(R.string.navigator_group);
                List<Group> groups = groupService.findAll();
                if (checkNotViewEmpty(groups.isEmpty(), recyclerView, emptyText)) {
                    groupAdapter.addAll(groups);
                }
            }
        }

        /**
         * Сохранение сортировки списка
         */
        private void saveSortGroupsShopping() {
            if (sortingShopList != null) {
                BuyService buyService = BuyService.getInstance(getContext());
                List<Buy> buys = buyService.findAllByShopping(sortingShopList);
                List<Group> groups = groupAdapter.getAll();
                Map<String, Group> mapGroup = new HashMap<>();
                for (Group group : groups) {
                    mapGroup.put(group.getName().toUpperCase(), group);
                }
                for (Buy b : buys) {
                    try {
                        String key = b.getNameGroup().toUpperCase();
                        Group group = mapGroup.get(key);
                        b.setNameGroup(group.getName());
                        b.setPositionGroup(group.getPosition());
                        buyService.save(b);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "", e);
                    }
                }
                finish();
            }
        }

        /**
         * Создает или редактирует группу
         *
         * @param group редактируемая группа или null для создания новой
         */
        private void createEditGroup(Group group) {
            if (group == null) {
                group = new Group();
                group.setName(getString(R.string.group_name_default_new));
                group.setPosition(groupAdapter.getItemCount());
            }
            GroupNameDialog.newInstance(group).target(this, REQUEST_RENAME_GROUP)
                .show(getActivity());
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.group_menu_delete:
                    ConfirmDialog.builder(getContext())
                        .title(R.string.group_dialog_delete_title)
                        .message(R.string.group_dialog_delete_message)
                        .positiveCaption(R.string.group_dialog_delete_positive)
                        .negativeCaption(R.string.group_dialog_delete_negative)
                        .show(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    try {
                                        for (Group group : groupAdapter.getSelectValues()) {
                                            GroupService.getInstance(getContext()).delete(group);
                                        }
                                        showInfoMessage(R.string.group_success_delete_message);
                                    } catch (ServiceException e) {
                                        Log.e(LOG_TAG, "", e);
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
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (REQUEST_RENAME_GROUP == requestCode) {
                if (Activity.RESULT_OK == resultCode) {
                    Group group = (Group) data.getSerializableExtra(GroupNameDialog.RESULT_GROUP);
                    if (StringUtils.isNotBlank(group.getName())) {
                        if (group.getId() == null) {
                            if (groupAdapter.checkDuplicate(group.getName())) {
                                Toast.makeText(getContext(), getString(R.string.group_duplicate_message), Toast.LENGTH_LONG).show();
                                return;
                            }
                            group.setPosition(groupAdapter.getItemCount());
                        }
                        try {
                            group.setId(groupService.save(group));
                            refresh();
                            recyclerView.scrollToPosition(group.getPosition());
                        } catch (ServiceException e) {
                            showErrorMessage(e);
                        }
                    }
                }
            }
        }

        @Override
        public boolean beforeClose() {
            if (selectMode) {
                groupAdapter.deselectionAll();
                changeSelectMode(0, 0);
                return false;
            }
            if (sortingShopList != null && modified) {
                ConfirmDialog.builder(getContext())
                    .title(R.string.group_dialog_save_sort_title)
                    .message(R.string.group_dialog_save_sort_message)
                    .positiveCaption(R.string.group_dialog_save_sort_positive)
                    .negativeCaption(R.string.group_dialog_save_sort_negative)
                    .show(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DialogInterface.BUTTON_POSITIVE == which) {
                                saveSortGroupsShopping();
                            }
                            finish();
                        }
                    });
                return false;
            }
            return true;
        }
    }

}
