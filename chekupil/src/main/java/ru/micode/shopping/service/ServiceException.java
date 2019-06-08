package ru.micode.shopping.service;

import android.content.Context;

/**
 * Исключение при выполнении операций сервиса
 * Created by Petr Gusarov on 06.04.18.
 */
public class ServiceException extends Exception {

    private final int stringResId;

    /**
     * Конструктор
     *
     * @param stringResId id строкового ресурса
     */
    public ServiceException(int stringResId) {
        this(stringResId, "");
    }

    /**
     * Конструктор
     *
     * @param stringResId id строкового ресурса
     * @param message     сообщение об ошибке
     */
    public ServiceException(int stringResId, String message) {
        super(message);
        this.stringResId = stringResId;
    }


    /**
     * Id строкового ресурса
     *
     * @return
     */
    public int getStringResId() {
        return stringResId;
    }

    /**
     * Возвращает значение строкового ресурса
     *
     * @param context контекст
     * @return строковое значение ресурса
     */
    public String getStringResource(Context context) {
        return context.getString(stringResId);
    }

}
