package ru.micode.shopping.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import ru.micode.shopping.dbo.BaseHelper;

/**
 * Абстрактный класс сервиса работы с SQLite
 * Created by Petr Gusarov on 26.03.18.
 */
public abstract class AbstractDBService<T> {

    /**
     * Контекст
     */
    private final Context mContext;
    /**
     * Подключение к БД
     */
    private SQLiteDatabase mDatabase;

    /**
     * Конструктор
     *
     * @param context контекст активности
     */
    protected AbstractDBService(Context context) {
        this.mContext = context;
        this.mDatabase = new BaseHelper(mContext).getWritableDatabase();
    }

    public SQLiteDatabase getDbo() {
        return mDatabase;
    }

    public Context getContext() {
        return mContext;
    }

    abstract public int save(T bean) throws ServiceException;

    abstract public T findOne(int id);

    abstract public boolean delete(T bean) throws ServiceException;

    abstract protected ContentValues getContentValues(T buy);

}
