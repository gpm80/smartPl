package ru.micode.shopping.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import ru.micode.shopping.R;
import ru.micode.shopping.dbo.AbstractCursorWrapper;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Group;
import ru.micode.shopping.model.Shopping;

import static ru.micode.shopping.dbo.dbSchema.BuyTable;

/**
 * Сервис для элементов покупок
 * Created by Petr Gusarov on 26.03.18.
 */
public class BuyService extends AbstractDBService<Buy> {

    private final static String LOG_TAG = BuyService.class.getSimpleName();
    private static BuyService buyService;
    private GroupService groupService;

    private BuyService(Context context) {
        super(context);
        groupService = GroupService.getInstance(context);
    }

    /**
     * Возвращает синглтон сервиса
     *
     * @param context контекст
     * @return
     */
    public static BuyService getInstance(Context context) {
        if (buyService == null) {
            buyService = new BuyService(context);
        }
        return buyService;
    }

    /**
     * Обновление времени изменения родительского списка
     *
     * @param buy редактируемая покупка
     */
    private void updateTimeShopping(Buy buy) {
        if (buy != null && buy.getShoppingId() != null) {
            updateTimeShopping(buy.getShopping());
        }
    }

    /**
     * Обновление времени изменения родительского списка
     *
     * @param shopping редактируемый список
     */
    private void updateTimeShopping(Shopping shopping) {
        if (shopping != null && shopping.getId() != null) {
            ShoppingService.getInstance(getContext()).updateTime(shopping.getId());
        }
    }

    /**
     * Сохранение покупки
     *
     * @param buy покупка
     * @return id сохраненного элемента
     * @throws ServiceException если возникли ошибки при сохранении
     */
    @Override
    public int save(Buy buy) throws ServiceException {
        try {
            if (buy.getShoppingId() == null) {
                throw new ServiceException(R.string.service_buy_not_link_shopping);
            }
            ContentValues values = getContentValues(buy);
            if (buy.getId() == null) {
                int insert = (int) getDbo().insert(BuyTable.NAME, null, values);
                updateTimeShopping(buy);
                return insert;
            } else {
                int update = getDbo().update(BuyTable.NAME, values,
                    BuyTable.Cols.ID.getName() + " = ?",
                    new String[]{String.valueOf(buy.getId())});
                if (update == 1) {
                    updateTimeShopping(buy);
                    return buy.getId();
                }
                throw new ServiceException(R.string.service_buy_error_update);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "an error save buy", e);
            throw new ServiceException(R.string.service_buy_error_fatal);
        }
    }

    /**
     * Инвертирует отметку "куплено" для позиции покупки
     *
     * @param buy покупка
     */
    public void negativeDone(Buy buy) {
        buy.setDone(!buy.isDone());
        if (buy.getId() != null) {
            ContentValues values = new ContentValues();
            values.put(BuyTable.Cols.DONE.getName(), buy.isDone());
            getDbo().update(BuyTable.NAME, values, BuyTable.Cols.ID.getName() + " = ?", new String[]{String.valueOf(buy.getId())});
            updateTimeShopping(buy);
        }
    }

    /**
     * Возвращает тестовый список
     *
     * @param parent
     * @param count
     * @return
     */
    public List<Buy> getTestList(Shopping parent, int count) {
        List<String> measures = Arrays.asList("шт.", "г.", "кг.", "дес.", "бут.");
        List<Buy> result = new ArrayList<>();
        List<Group> allGroup = groupService.findAll();
        Random random = new Random();
        for (int i = 1; i <= count; i++) {
            Buy b = new Buy();
            b.setShopping(parent);
            b.setName("Покупка " + i);
            b.setAmount(i);
            b.setMeasure(measures.get(random.nextInt(measures.size())));
            Group group = allGroup.get(random.nextInt(allGroup.size()));
            b.setNameGroup(group.getName());
            b.setPositionGroup(group.getPosition());
            result.add(b);
        }
        return result;
    }

    /**
     * Поиск всех элементов списка покупок
     *
     * @param parent id списка
     * @return
     */
    public List<Buy> findAllByShopping(Shopping parent) {
        if (parent != null && parent.getId() != null) {
            Cursor cursor = getDbo().query(BuyTable.NAME, null,
                BuyTable.Cols.LIST_ID.getName() + " = ?",
                new String[]{String.valueOf(parent.getId())},
                null, null, null);
            List<Buy> buys = new BuyCusor(cursor, groupService).getItems();
            for (Buy buy : buys) {
                buy.setShopping(parent);
            }
            return buys;
        }
        return Collections.emptyList();
    }

    /**
     * Подсчет завершения списков
     *
     * @param parent
     * @return
     */
    public int[] countAllBuy(Shopping parent) {
        int required = 0, total = 0;
        if (parent != null && parent.getId() != null) {
            Cursor cursor = getDbo().query(BuyTable.NAME,
                new String[]{BuyTable.Cols.DONE.getName()},
                BuyTable.Cols.LIST_ID.getName() + " = ?",
                new String[]{String.valueOf(parent.getId())},
                null, null, null);
            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int done = cursor.getInt(cursor.getColumnIndex(BuyTable.Cols.DONE.getName()));
                    if (done == 0) {
                        required++;
                    }
                    total++;
                    cursor.moveToNext();
                }

            } finally {
                cursor.close();
            }
        }
        return new int[]{required, total};
    }

    @Override
    public Buy findOne(int id) {
        try {
            Cursor cursor = getDbo().query(BuyTable.NAME, null,
                BuyTable.Cols.ID.getName() + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
            return new BuyCusor(cursor, groupService).getItem();
        } catch (Exception e) {
            Log.e(LOG_TAG, "an error findOne buy id:" + id, e);
        }
        return null;
    }

    @Override
    public boolean delete(Buy buy) {
        try {
            Integer id = buy.getId();
            if (id != null) {
                int delete = getDbo().delete(BuyTable.NAME, BuyTable.Cols.ID.getName() + " = ?", new String[]{id.toString()});
                updateTimeShopping(buy);
                return delete > 0;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, " an error delete", e);
        }
        return false;
    }

    /**
     * Удалить все отмеченные позиции списка
     *
     * @param shopping родительский список
     * @return
     */
    public boolean deleteIsDoneByShopping(Shopping shopping) {
        if (shopping != null && shopping.getId() != null) {
            int delete = getDbo().delete(BuyTable.NAME,
                String.format("%s = ? and %s > 0", BuyTable.Cols.LIST_ID.getName(), BuyTable.Cols.DONE.getName()),
                new String[]{String.valueOf(shopping.getId())});
            updateTimeShopping(shopping);
            return delete > 0;
        }
        return false;
    }

    /**
     * Устанавливает указанную отметку для всех позиций списка
     *
     * @param shopping список
     * @param done     устанавливаемое значение
     * @return
     */
    public boolean setDoneAllItemsByShopping(Shopping shopping, boolean done) {
        if (shopping != null && shopping.getId() != null) {
            ContentValues values = new ContentValues();
            values.put(BuyTable.Cols.DONE.getName(), done);
            int update = getDbo().update(BuyTable.NAME, values,
                String.format("%s = ?", BuyTable.Cols.LIST_ID.getName()),
                new String[]{String.valueOf(shopping.getId())});
            updateTimeShopping(shopping);
            return update > 0;
        }
        return false;
    }

    /**
     * Инвертирует отметки списка
     *
     * @param shopping список
     * @return
     */
    public boolean inversionDoneByShopping(Shopping shopping) {
        if (shopping != null && shopping.getId() != null) {
            getDbo().execSQL("UPDATE '" + BuyTable.NAME + "' SET done = not done WHERE " + BuyTable.Cols.LIST_ID.getName() + " = ?",
                new String[]{String.valueOf(shopping.getId())});
            updateTimeShopping(shopping);
            return true;
        }
        return false;
    }

    protected ContentValues getContentValues(Buy buy) {
        ContentValues values = new ContentValues();
        values.put(BuyTable.Cols.NAME.getName(), buy.getName());
        values.put(BuyTable.Cols.AMOUNT.getName(), buy.getAmount());
        values.put(BuyTable.Cols.MEASURE.getName(), buy.getMeasure());
        values.put(BuyTable.Cols.DONE.getName(), buy.isDone());
        values.put(BuyTable.Cols.GROUP_NAME.getName(), buy.getNameGroup());
        values.put(BuyTable.Cols.GROUP_POS.getName(), buy.getPositionGroup());
        values.put(BuyTable.Cols.LIST_ID.getName(), buy.getShoppingId());
        return values;
    }

    private static class BuyCusor extends AbstractCursorWrapper<Buy> {

        private final GroupService groupService;

        public BuyCusor(Cursor cursor, GroupService groupService) {
            super(cursor);
            this.groupService = groupService;
        }

        @Override
        protected Buy getModel() {
            Buy buy = new Buy();
            buy.setId(getInt(getColumnIndex(BuyTable.Cols.ID.getName())));
            buy.setName(getString(getColumnIndex(BuyTable.Cols.NAME.getName())));
            buy.setAmount(getInt(getColumnIndex(BuyTable.Cols.AMOUNT.getName())));
            buy.setMeasure(getString(getColumnIndex(BuyTable.Cols.MEASURE.getName())));
            // Устанавливаем группу
            buy.setNameGroup(getString(getColumnIndex(BuyTable.Cols.GROUP_NAME.getName())));
            buy.setPositionGroup(getInt(getColumnIndex(BuyTable.Cols.GROUP_POS.getName())));
            buy.setDone(getInt(getColumnIndex(BuyTable.Cols.DONE.getName())) > 0);
            return buy;
        }
    }
}
