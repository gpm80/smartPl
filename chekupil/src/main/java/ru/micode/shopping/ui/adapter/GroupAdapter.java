package ru.micode.shopping.ui.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import ru.micode.shopping.R;
import ru.micode.shopping.model.Group;
import ru.micode.shopping.service.GroupService;
import ru.micode.shopping.service.ServiceException;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerAdapter;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerHolder;
import ru.micode.shopping.ui.common.GroupBuySortedList;

/**
 * Адаптер списка групп товаров
 * Created by Petr Gusarov on 14.05.18.
 */
public class GroupAdapter extends MyRecyclerAdapter<Group> {

    private RecyclerView recyclerParent;

    public GroupAdapter(Context context, ItemTouchListener<Group> actionListener) {
        super(context, new Group(), new GroupBuySortedList<Group>(), actionListener);
    }


    @Override
    public MyRecyclerHolder<Group> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupViewHolder(this, context, parent, this);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder<Group> holder, int position) {
        holder.bind(values.get(position), position);
    }

    //    /**
//     * Возвращает список групп в текущей сортировке
//     *
//     * @return
//     */
//    public List<Group> getGroups() {
//        return new ArrayList<>(values);
//    }

    public List<Group> getAll() {
        return values;
    }

    /**
     * Проверяет наличие дубликатов групп
     *
     * @param name наименование
     * @return
     */
    public boolean checkDuplicate(String name) {
        for (Group g : values) {
            if (name.equalsIgnoreCase(g.getName())) {
                return true;
            }
        }
        return false;
    }

//    /**
//     * Возвращает наличие выделенных позиций
//     *
//     * @return
//     */
//    public boolean isSelectedItem() {
//        for (Group g : values) {
//            if (g.isSelected()) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public List<Group> getSelectedItems() {
//        List<Group> selected = new ArrayList<>();
//        for (Group g : values) {
//            if (g.isSelected()) {
//                selected.add(g);
//            }
//        }
//        return selected;
//    }

    public void setRecyclerParent(RecyclerView recyclerParent) {
        this.recyclerParent = recyclerParent;
    }

    public void scrollToPosition(int pos) {
        if (recyclerParent != null) {
            recyclerParent.scrollToPosition(pos);
        }
    }

//    @Override
//    public void onBindViewHolder(GroupViewHolder holder, int position) {
//        holder.bind(values, position);
//    }

    /**
     * Холдер групп
     */
    public static class GroupViewHolder extends MyRecyclerHolder<Group> {

        private final static String LOG_TAG = GroupViewHolder.class.getSimpleName();
        private GroupService groupService;
        private final TextView nameGroup;
        private final ImageButton downButton;
        private final ImageButton upButton;
        private final GroupAdapter adapter;
        private final int selectColor;

        public GroupViewHolder(GroupAdapter adapter, Context context, ViewGroup parent, ActionListener<Group> actionListener) {
            super(context, R.layout.group_adapter, parent, actionListener);
            selectColor = ContextCompat.getColor(context, R.color.che_background_inverse_light);
            groupService = GroupService.getInstance(context);
            this.adapter = adapter;
            nameGroup = itemView.findViewById(R.id.group_name);
            downButton = itemView.findViewById(R.id.group_down_position_button);
            upButton = itemView.findViewById(R.id.group_up_position_button);
        }

        @Override
        protected void bindData(Group value, final int position) {
            Log.d(LOG_TAG, "Биндим группу на позицию " + position + " (" + currentValue.getName() + ")");
            nameGroup.setText(currentValue.getName());
            if (currentValue.isSelected()) {
                itemView.setBackgroundColor(selectColor);
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }
            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swapItems(adapter, position, position - 1);
                }
            });
            downButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swapItems(adapter, position, position + 1);
                }
            });
        }

        /**
         * Меняет местами записи
         *
         * @param from позиция записи
         * @param to   целевая позиция
         * @return true если перестановка выполнена, иначе false
         */
        private boolean swapItems(GroupAdapter adapter, int from, int to) {
            List<Group> values = adapter.getAll();
            //TODO Сделать цикличное перемещение групп (сверху сразу вниз и наоборот)
            if (from == to) {
                return false;
            } else if (from > to && to < 0) {
                // Вверх
                return false;
            } else if (from < to && values.size() <= to) {
                // Вниз
                return false;
            }
            Group fromGroup = values.get(from);
            Group toGroup = values.get(to);
            fromGroup.setPosition(to);
            toGroup.setPosition(from);
            actionListener.itemAction(Action.MODIFIED, to, null, fromGroup);
            actionListener.itemAction(Action.MODIFIED, from, null, toGroup);
            try {
                if (fromGroup.getId() != null && toGroup.getId() != null) {
                    // Сохраняем в базу только существующие группы
                    groupService.save(fromGroup);
                    groupService.save(toGroup);
                }
                Collections.swap(values, from, to);
                Log.d(LOG_TAG, "Поменяли местами " + from + " и " + to);
                adapter.notifyItemMoved(from, to);
                adapter.notifyItemChanged(from);
                adapter.notifyItemChanged(to);
                adapter.scrollToPosition(to);
            } catch (ServiceException e) {
//                Toast.makeText(context, R.string.service_group_error_swap, Toast.LENGTH_LONG)
//                    .show();
                return false;
            } catch (Exception ex) {
                Log.e(LOG_TAG, "", ex);
                return false;
            }
            return true;
        }
    }
}
