package ru.micode.shopping.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.R;
import ru.micode.shopping.dbo.AbstractCursorWrapper;
import ru.micode.shopping.dbo.dbSchema.GroupTable;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Group;
import ru.micode.shopping.model.Shopping;

/**
 * Created by Petr Gusarov on 23.04.18.
 */
public class GroupService extends AbstractDBService<Group> {

    private final static String LOG_TAG = GroupService.class.getSimpleName();
    private static GroupService groupService;
    public static final Group UNDEFINED_GROUP = new Group("Другое", 1000);
    public static final Group DONE_GROUP = new Group("Куплено", 1001);
    private final Context context;

    /**
     * Возвращает синглтон сервиса
     *
     * @param context контекст
     * @return
     */
    public static GroupService getInstance(Context context) {
        if (groupService == null) {
            groupService = new GroupService(context);
        }
        return groupService;
    }

    /**
     * Конструктор
     *
     * @param context контекст активности
     */
    private GroupService(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Создает справочные группы по умолчанию
     */
    public void createDirectDefault() {
        if (findAll().isEmpty()) {
            String[] nameGroups = new String[]{
                "Детские товары",
                "Хлебобулочные",
                "Чай/кофе",
                "Сладости",
                "Напитки, соки",
                "Виноводочный",
                "Фрукты/Овощи",
                "Мясо/Рыба",
                "Замороженные продукты",
                "Молочные",
                "Сыры/Колбасы",
                "Бакалея",
                "Товары для дома"
            };
            int i = 0;
            for (String n : nameGroups) {
                Group group = new Group(n, i++);
                try {
                    save(group);
                } catch (ServiceException e) {
                    Log.w(LOG_TAG, "An error createDirectDefault", e);
                }
            }
        }
    }

    /**
     * Поиск справочных групп
     *
     * @return
     */
    public List<Group> findAll() {
        Cursor cursor = getDbo().rawQuery("select * from " + GroupTable.NAME, new String[]{});
        GroupCursor groupCursor = new GroupCursor(cursor);
        return groupCursor.getItems();
    }

    /**
     * Возвращает отсортированный, объединенный список групп переданного листа и локального справочника.
     * Объединение идет по уникальности наименования, приориет отдается группе из списка.
     *
     * @param shopping    список покупок для объединения, если null,
     *                    то возвращается только список групп из справочника
     * @param manualGroup текущая группа покупки, если она уникальна
     * @return отсортированный, объединенный список групп
     */
    public List<Group> findAllAndJoinShopping(Shopping shopping, Group manualGroup) {
        List<Group> directoryGroup = findAll();
        directoryGroup.add(UNDEFINED_GROUP);
        //С начала берем из списка
        HashSet<Group> groups = new HashSet<>();
        if (shopping != null && shopping.getId() != null) {
            List<Buy> buys = BuyService.getInstance(context).findAllByShopping(shopping);
            for (Buy buy : buys) {
                groups.add(buy.getGroup(false));
            }
        }
        // Затем добавляем из справочника
        groups.addAll(directoryGroup);
        // Добавляем текущуюю группу
        if (manualGroup != null && StringUtils.isNotBlank(manualGroup.getName())) {
            groups.add(manualGroup);
        }
        ArrayList<Group> listGroup = new ArrayList<>(groups);
        Collections.sort(listGroup);
        return listGroup;
    }

    @Override
    public int save(Group group) throws ServiceException {
        try {
            ContentValues values = getContentValues(group);
            if (group.getId() == null) {
                return (int) getDbo().insert(GroupTable.NAME, null, values);
            } else {
                int update = getDbo().update(GroupTable.NAME, values,
                    GroupTable.Cols.ID.whereEq(),
                    new String[]{String.valueOf(group.getId())});
                if (update == 1) {
                    return group.getId();
                }
                throw new ServiceException(R.string.service_buy_error_update);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "an error save buy", e);
            throw new ServiceException(R.string.service_buy_error_fatal);
        }
    }

    @Override
    public Group findOne(int id) {
        try {
            Cursor cursor = getDbo().query(GroupTable.NAME, null,
                GroupTable.Cols.ID.whereEq(), new String[]{String.valueOf(id)},
                null, null, null);
            return new GroupCursor(cursor).getItem();
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error findOne group id:" + id, e);
        }
        return null;
    }

    @Override
    public boolean delete(Group group) throws ServiceException {
        try {
            Integer id = group.getId();
            if (id != null) {
                int delete = getDbo().delete(GroupTable.NAME, GroupTable.Cols.ID.whereEq(), new String[]{id.toString()});
                return delete > 0;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error delete", e);
        }
        return false;
    }

    @Override
    protected ContentValues getContentValues(Group value) {
        ContentValues values = new ContentValues();
        values.put(GroupTable.Cols.NAME.getName(), value.getName());
        values.put(GroupTable.Cols.POSITION.getName(), value.getPosition());
        return values;
    }

    /**
     * Курсор групп
     */
    private static class GroupCursor extends AbstractCursorWrapper<Group> {

        /**
         * Конструктор обертки курсора.
         *
         * @param cursor основной кусор.
         */
        public GroupCursor(Cursor cursor) {
            super(cursor);
        }

        @Override
        protected Group getModel() {
            Group group = new Group();
            group.setId(getInt(getColumnIndex(GroupTable.Cols.ID.getName())));
            group.setName(getString(getColumnIndex(GroupTable.Cols.NAME.getName())));
            group.setPosition(getInt(getColumnIndex(GroupTable.Cols.POSITION.getName())));
            return group;
        }
    }
}
