package ru.micode.shopping.model.ex;

/**
 * Ключи заголовков
 * Created by Petr Gusarov on 11.10.17.
 */
public class KeyHeader {

    public final static String SESSION = "x-session";
    public final static String SECURITY = "x-security";
    public final static String MESSAGE_ERROR = "x-error-message";
    public final static String CODE_ERROR = "x-error-code";
    public final static String IS_ERROR = "x-error";

    // Коды зарезервированнх ошибок
    public final static int CODE_UNAUTHORIZED = 100;


}
