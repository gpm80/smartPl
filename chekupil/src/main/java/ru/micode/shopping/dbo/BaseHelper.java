package ru.micode.shopping.dbo;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ru.micode.shopping.dbo.dbSchema.BuyTable;
import ru.micode.shopping.dbo.dbSchema.FriendTable;
import ru.micode.shopping.dbo.dbSchema.GroupTable;
import ru.micode.shopping.dbo.dbSchema.ShoppingTable;
import ru.micode.shopping.dbo.dbSchema.FavoriteTable;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Group;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.service.GroupService;

/**
 * Created by Petr Gusarov on 15.03.18.
 */
public class BaseHelper extends SQLiteOpenHelper {

    //https://sqlite.org/foreignkeys.html
    //http://xbb.uz/db/Tipy-dannyh-v-SQLite-versii-3
    //http://developer.alexanderklimov.ru/android/sqlite/android-sqlite.php
    //adb shell
    // sqlite3 path/to/base
    private static String LOG_TAG = BaseHelper.class.getSimpleName();
    private static final int VERSION = 3;
    private static final String DB_NAME = "shop.db";

    public BaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ShoppingTable.sqlCreate());
        db.execSQL(BuyTable.sqlCreate());
        db.execSQL(FriendTable.sqlCreate());
        db.execSQL(GroupTable.sqlCreate());
        db.execSQL(FavoriteTable.sqlCreate());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            migrationDb(db);
        } else if (oldVersion < 3) {
            db.execSQL(FavoriteTable.sqlCreate());
        }
    }

    /**
     * Миграция БД
     *
     * @param db dbo
     */
    private void migrationDb(SQLiteDatabase db) {
//        dropTable(db, ShoppingTable.NAME);
//        dropTable(db, BuyTable.NAME);
//        dropTable(db, FriendTable.NAME);
//        dropTable(db, GroupTable.NAME);
        try {
            // Создаем новые таблицы
            onCreate(db);
            // Копируем данные списков
            AbstractCursorWrapper<Shopping> wrap = new AbstractCursorWrapper<Shopping>(db.query("shopList", null, null, null, null, null, null)) {
                @Override
                protected Shopping getModel() {
                    Shopping shopping = new Shopping();
                    shopping.setId(getInt(getColumnIndex("id")));
                    shopping.setName(getString(getColumnIndex("name")));
                    shopping.setAuthor(getString(getColumnIndex("author")));
                    shopping.setEditDate(getInt(getColumnIndex("editDate")));
                    shopping.setNew(false);
                    return shopping;
                }
            };
            List<Shopping> items = wrap.getItems();
            for (Shopping shopping : items) {
                Integer shoppingIdSource = shopping.getId();
                shopping.setId(null);
                {//Сохраним новый экземпляр
                    ContentValues values = new ContentValues();
                    values.put(ShoppingTable.Cols.NAME.getName(), shopping.getName());
                    values.put(ShoppingTable.Cols.COMMENT.getName(), shopping.getComment());
                    values.put(ShoppingTable.Cols.AUTHOR.getName(), shopping.getAuthor());
                    values.put(ShoppingTable.Cols.EDIT_DATE.getName(), shopping.getEditDate());
                    values.put(ShoppingTable.Cols.NEW.getName(), shopping.isNew());
                    long insertId = db.insert(ShoppingTable.NAME, null, values);
                    shopping.setId((int) insertId);
                }
                AbstractCursorWrapper<Buy> buyWrap = new AbstractCursorWrapper<Buy>(db.query("buy", null, "list_id = ?", new String[]{String.valueOf(shoppingIdSource)}, null, null, null)) {
                    @Override
                    protected Buy getModel() {
                        Buy buy = new Buy();
                        buy.setName(getString(getColumnIndex("name")));
                        buy.setAmount(getInt(getColumnIndex("amount")));
                        buy.setDone(getInt(getColumnIndex("done")) > 0);
                        return buy;
                    }
                };
                for (Buy buy : buyWrap.getItems()) {
                    buy.setShopping(shopping);
                    Group undefinedGroup = GroupService.UNDEFINED_GROUP;
                    buy.setNameGroup(undefinedGroup.getName());
                    buy.setPositionGroup(undefinedGroup.getPosition());
                    {// Сохраним новую запись
                        ContentValues values = new ContentValues();
                        values.put(BuyTable.Cols.NAME.getName(), buy.getName());
                        values.put(BuyTable.Cols.AMOUNT.getName(), buy.getAmount());
                        values.put(BuyTable.Cols.MEASURE.getName(), buy.getMeasure());
                        values.put(BuyTable.Cols.DONE.getName(), buy.isDone());
                        values.put(BuyTable.Cols.GROUP_NAME.getName(), buy.getNameGroup());
                        values.put(BuyTable.Cols.GROUP_POS.getName(), buy.getPositionGroup());
                        values.put(BuyTable.Cols.LIST_ID.getName(), buy.getShoppingId());
                        db.insert(BuyTable.NAME, null, values);
                    }
                }
            }
            // TODO Дропнуть старые таблицы
        } catch (Exception e) {
            Log.e(LOG_TAG, "an fatal error migrate db", e);
        }
    }

    /**
     * Удаляет таблицу из базы
     *
     * @param db
     * @param nameTable название таблицы
     */
    private void dropTable(SQLiteDatabase db, String nameTable) {
        try {
            db.execSQL("DROP TABLE " + nameTable);
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
    }
}
