package ru.micode.shopping.ui.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.ui.common.CloseFragmentListener;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.TuningRecycler;

/**
 * Created by Petr Gusarov on 02.11.18.
 */
public class RecyclerTestActivity extends MyAbstractActivity {

    @Override
    protected Fragment createFragment() {
        return new RecyclerTestFragmentListener();
    }

    @Override
    protected boolean isViewBackButton() {
        return true;
    }

    @Override
    protected boolean isExit() {
        return false;
    }

    public static class RecyclerTestFragmentListener extends MyAbstractFragment implements CloseFragmentListener {

        private RecyclerView recyclerView;
        private ItemRecyclerAdapter itemRecyclerAdapter;
        private int count;
        private boolean selectMode = false;

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
            getActivity().setTitle(R.string.test_caption_activity);
            itemRecyclerAdapter = new ItemRecyclerAdapter(getContext(), new ArrayList<Item>(), new ItemTouchListener<Item>() {
                @Override
                public boolean touch(Type type, View view, List<Item> selectList, int totalSize, Item value) {
                    if (type == Type.SELECT) {
                        changeSelectMode(selectList.size(), totalSize);
                    } else if (type == Type.CLICK) {
                        Toast.makeText(getContext(), value.getName(), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
            recyclerView = TuningRecycler
                .builder(view.findViewById(R.id.shopping_list_view), getContext())
                .addDivider()
                .setAdapter(itemRecyclerAdapter)
                .getRecyclerView();
            view.findViewById(R.id.float_new_shopping)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createItem();
                    }
                });
            for (int i = 0; i < 10; i++) {
                createItem();
            }
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
                    ApplicationLoader.applicationContext.getString(R.string.shopping_title_activity_selected),
                    String.valueOf(selectCount), String.valueOf(totalCount)));
                getCurrentMenu().setGroupVisible(R.id.shopping_menu_group_one, selectCount == 1);
                getCurrentMenu().setGroupVisible(R.id.shopping_menu_group_many, true);
            } else {
                selectMode = false;
                getActivity().setTitle(R.string.test_caption_activity);
                getCurrentMenu().setGroupVisible(R.id.shopping_menu_group_one, false);
                getCurrentMenu().setGroupVisible(R.id.shopping_menu_group_many, false);
            }
        }

        private Item createItem() {
            Item item = new Item("Запись " + (++count), new Random().nextInt(11) + 1);
            itemRecyclerAdapter.add(item);
            return item;
        }

        @Override
        protected void refresh() {

        }

        @Override
        public boolean beforeClose() {
            if (selectMode) {
                itemRecyclerAdapter.deselectionAll();
                changeSelectMode(0, 0);
                return false;
            }
            return true;
        }
    }
}
