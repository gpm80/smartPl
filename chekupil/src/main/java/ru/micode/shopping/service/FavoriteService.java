package ru.micode.shopping.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ru.micode.shopping.dbo.AbstractCursorWrapper;
import ru.micode.shopping.dbo.dbSchema;
import ru.micode.shopping.model.Favorite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Petr Gusarov on 05.04.19.
 */
public class FavoriteService extends AbstractDBService<Favorite> {

    private static FavoriteService instance;
    private final HashSet<String> allFavoriteSet;

    public static FavoriteService getInstance(Context context) {
        if (instance == null) {
            instance = new FavoriteService(context);
            instance.findAllUid();
        }
        return instance;
    }

    /**
     * Конструктор
     *
     * @param context контекст активности
     */
    private FavoriteService(Context context) {
        super(context);
        allFavoriteSet = new HashSet<>();
    }

    public List<String> findAllUid() {
        Cursor cursor = getDbo().query(dbSchema.FavoriteTable.NAME, null, null, null, null, null,
            dbSchema.FavoriteTable.Cols.TIME.getName() + " DESC");
        List<Favorite> items = new FavoriteCursor(cursor).getItems();
        List<String> uids = new ArrayList<>();
        allFavoriteSet.clear();
        for (Favorite f : items) {
            uids.add(f.getRecipeUid());
            allFavoriteSet.add(f.getRecipeUid());
        }
        return uids;
    }

    /**
     * Добавляет или удалаят ссылку избранных рецептов
     *
     * @param uid uid рецепта на сервере
     * @return true - если добавлено в избранное;<br/>
     * false - если удалено из избранного
     */
    public boolean changeFavorite(String uid) throws ServiceException {
        Cursor cursor = getDbo().query(dbSchema.FavoriteTable.NAME, null,
            dbSchema.FavoriteTable.Cols.UID.whereEq(), new String[]{uid}, null, null, null);
        FavoriteCursor favoriteCursor = new FavoriteCursor(cursor);
        List<Favorite> items = favoriteCursor.getItems();
        if (items.isEmpty()) {
            Favorite favorite = new Favorite(uid);
            favorite.setTime((int) (System.currentTimeMillis() / 1000));
            ContentValues values = getContentValues(favorite);
            getDbo().insert(dbSchema.FavoriteTable.NAME, null, values);
            allFavoriteSet.add(uid);
            return true;
        } else {
            //Если есть в избранном, то удалить
            delete(items.get(0));
            allFavoriteSet.remove(uid);
            return false;
        }
    }

    public boolean isFavorite(String uid) {
        return allFavoriteSet.contains(uid);
    }

    @Override
    public int save(Favorite bean) throws ServiceException {
        Cursor cursor = getDbo().query(dbSchema.FavoriteTable.NAME, null, dbSchema.FavoriteTable.Cols.UID.whereEq(), new String[]{bean.getRecipeUid()}
            , null, null, null);
        FavoriteCursor favoriteCursor = new FavoriteCursor(cursor);
        List<Favorite> items = favoriteCursor.getItems();
        if (items.isEmpty()) {
            bean.setTime((int) (System.currentTimeMillis() / 1000));
            ContentValues values = getContentValues(bean);
            getDbo().insert(dbSchema.FavoriteTable.NAME, null, values);
            allFavoriteSet.add(bean.getRecipeUid());
            return 1;
        } else {
            // Обновим дату
            Favorite favorite = items.get(0);
            favorite.setTime((int) (System.currentTimeMillis() / 1000));
            ContentValues contentValues = getContentValues(favorite);
            int upd = getDbo().update(dbSchema.FavoriteTable.NAME, contentValues,
                dbSchema.FavoriteTable.Cols.UID.whereEq(), new String[]{favorite.getRecipeUid()});
            if (upd == 1) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public Favorite findOne(int id) {
        return null;
    }

    @Override
    public boolean delete(Favorite bean) throws ServiceException {
        int delete = getDbo().delete(dbSchema.FavoriteTable.NAME, dbSchema.FavoriteTable.Cols.UID.whereEq(),
            new String[]{bean.getRecipeUid()});
        return delete == 1;
    }

    @Override
    protected ContentValues getContentValues(Favorite favorite) {
        ContentValues values = new ContentValues();
        values.put(dbSchema.FavoriteTable.Cols.UID.getName(), favorite.getRecipeUid());
        values.put(dbSchema.FavoriteTable.Cols.TIME.getName(), favorite.getTime());
        return values;
    }

    /**
     * Обертка курсора
     */
    private static class FavoriteCursor extends AbstractCursorWrapper<Favorite> {

        /**
         * Конструктор обертки курсора.
         *
         * @param cursor основной кусор.
         */
        public FavoriteCursor(Cursor cursor) {
            super(cursor);
        }

        @Override
        protected Favorite getModel() {
            Favorite favorite = new Favorite();
            favorite.setRecipeUid(getString(getColumnIndex(dbSchema.FavoriteTable.Cols.UID.getName())));
            favorite.setTime(getInt(getColumnIndex(dbSchema.FavoriteTable.Cols.TIME.getName())));
            return favorite;
        }
    }
}
