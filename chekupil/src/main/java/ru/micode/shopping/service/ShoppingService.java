package ru.micode.shopping.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import ru.micode.shopping.R;
import ru.micode.shopping.dbo.AbstractCursorWrapper;
import ru.micode.shopping.dbo.dbSchema.ShoppingTable;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Shopping;

/**
 * Сервис для списков покупок
 * Created by Petr Gusarov on 19.03.18.
 */
public class ShoppingService extends AbstractDBService<Shopping> {

    private final static String LOG_TAG = ShoppingService.class.getSimpleName();
    private static ShoppingService sService;
    private Context context;

    /**
     * Возвращает синглтон сервиса
     *
     * @param context контекс активности
     * @return
     */
    public static ShoppingService getInstance(Context context) {
        if (sService == null) {
            sService = new ShoppingService(context);
        }
        return sService;
    }

    /**
     * Конструктор
     *
     * @param context контекст приложения
     */
    private ShoppingService(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Сохраняет список
     *
     * @param shopping список покупок
     * @return ID списка нового или измененного, если ошибка то -1
     */
    public int save(Shopping shopping) throws ServiceException {
        try {
            ContentValues values = getContentValues(shopping);
            if (shopping.getId() == null) {
                return (int) getDbo().insert(ShoppingTable.NAME, null, values);
            } else {
                int update = getDbo().update(ShoppingTable.NAME, values,
                    ShoppingTable.Cols.ID.getName() + " = ?", new String[]{shopping.getId().toString()});
                if (update == 1) {
                    return shopping.getId();
                }
                throw new ServiceException(R.string.service_shopping_update_error);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, " an error save", e);
            throw new ServiceException(R.string.service_shopping_update_error, e.toString());
        }
    }

    /**
     * Снимает флаг нового списка
     *
     * @param shopping
     */
    private void notNewShopping(Shopping shopping) {
        try {
            if (shopping.isNew()) {
                shopping.setNew(false);
                ContentValues values = new ContentValues();
                values.put(ShoppingTable.Cols.NEW.getName(), false);
                getDbo().update(ShoppingTable.NAME, values, ShoppingTable.Cols.ID.whereEq(),
                    new String[]{String.valueOf(shopping.getId())});
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }

    }

    /**
     * Обновляет время последнего изменения списка
     *
     * @param shoppingId
     */
    public void updateTime(int shoppingId) {
        try {
            ContentValues values = new ContentValues();
            values.put(ShoppingTable.Cols.EDIT_DATE.getName(), (int) (System.currentTimeMillis() / 1000));
            getDbo().update(ShoppingTable.NAME, values,
                ShoppingTable.Cols.ID.getName() + " = ?", new String[]{String.valueOf(shoppingId)});
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error update edit date", e);
        }
    }

    /**
     * Поиск всех списков
     *
     * @return
     */
    public List<Shopping> findAll() {
        try {
            Cursor cursor = getDbo().query(ShoppingTable.NAME, null, null, null, null, null,
                ShoppingTable.Cols.EDIT_DATE.getName() + " DESC");
            List<Shopping> items = new ShoppingCursor(cursor).getItems();
            BuyService buyService = BuyService.getInstance(context);
            for (Shopping shopping : items) {
                int[] res = buyService.countAllBuy(shopping);
                shopping.setRequired(res[0]);
                shopping.setTotal(res[1]);
            }
            return items;
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        return Collections.emptyList();
    }

    public Shopping findOne(int id) {
        try {
            Cursor cursor = getDbo().query(ShoppingTable.NAME, null,
                ShoppingTable.Cols.ID.getName() + " = ?", new String[]{String.valueOf(id)}, null, null, null);
            Shopping oneShopping = new ShoppingCursor(cursor).getItem();
            notNewShopping(oneShopping);
            return oneShopping;
        } catch (Exception e) {
            Log.e(LOG_TAG, " an error findOne", e);
        }
        return null;
    }

    /**
     * Удаление списка
     *
     * @param shopping список для удаления
     * @return
     */
    public boolean delete(Shopping shopping) throws ServiceException {
        try {
            if (shopping != null && shopping.getId() != null) {
                // Удалим все дочерние элементы списка
                List<Buy> buys = BuyService.getInstance(context).findAllByShopping(shopping);
                for (Buy buy : buys) {
                    BuyService.getInstance(context).delete(buy);
                }
                int delete = getDbo().delete(ShoppingTable.NAME,
                    ShoppingTable.Cols.ID.getName() + " = ?",
                    new String[]{shopping.getId().toString()});
                return delete > 0;
            }
            return false;
        } catch (Exception e) {
            Log.e(LOG_TAG, " an error delete", e);
            throw new ServiceException(R.string.service_shopping_delete_error, e.toString());
        }
    }

    protected ContentValues getContentValues(Shopping shopping) {
        // Обновим время изменения записи
        shopping.setEditDate((int) (new Date().getTime() / 1000));
        ContentValues values = new ContentValues();
        values.put(ShoppingTable.Cols.NAME.getName(), shopping.getName());
        values.put(ShoppingTable.Cols.COMMENT.getName(), shopping.getComment());
        values.put(ShoppingTable.Cols.AUTHOR.getName(), shopping.getAuthor());
        values.put(ShoppingTable.Cols.EDIT_DATE.getName(), shopping.getEditDate());
        values.put(ShoppingTable.Cols.NEW.getName(), shopping.isNew());
        return values;
    }

    /**
     * Обертка курсора
     */
    private static class ShoppingCursor extends AbstractCursorWrapper<Shopping> {

        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public ShoppingCursor(Cursor cursor) {
            super(cursor);
        }

        @Override
        protected Shopping getModel() {
            Shopping shopping = new Shopping();
            shopping.setId(getInt(getColumnIndex(ShoppingTable.Cols.ID.getName())));
            shopping.setName(getString(getColumnIndex(ShoppingTable.Cols.NAME.getName())));
            shopping.setComment(getString(getColumnIndex(ShoppingTable.Cols.COMMENT.getName())));
            shopping.setAuthor(getString(getColumnIndex(ShoppingTable.Cols.AUTHOR.getName())));
            shopping.setEditDate(getInt(getColumnIndex(ShoppingTable.Cols.EDIT_DATE.getName())));
            shopping.setNew(getInt(getColumnIndex(ShoppingTable.Cols.NEW.getName())) > 0);
            return shopping;
        }
    }
}
