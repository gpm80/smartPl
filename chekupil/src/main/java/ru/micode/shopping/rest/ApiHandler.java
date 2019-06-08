package ru.micode.shopping.rest;

import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.micode.shopping.model.ex.KeyHeader;

/**
 * Помошник формирования запроса
 * Created by gpm on 11.10.17.
 */

public class ApiHandler<T> {

    private static final String TAG = ApiHandler.class.getSimpleName();
    private Map<String, String> header;
    private boolean enableNet;
    private Dialog dialog;
    private final Context context;

    public ApiHandler(Context context) {
        enableNet = true;
        this.context = context;
    }

    /**
     * Устанавливает параметры безопасности в заголовок для запроса (без сесии)
     *
     * @param sign   подписываемое значение
     * @param secret секретный ключ пользователя
     * @return
     */
    public ApiHandler<T> setSecurity(String sign, String secret) {
        return setSecurity(sign, secret, null);
    }

    /**
     * Устанавливает параметры безопасности в заголовок для запроса
     *
     * @param sign      подписываемое значение
     * @param secret    секретный ключ пользователя
     * @param sessionId идентивикатор сессии (если требуется)
     * @return
     */
    public ApiHandler<T> setSecurity(String sign, String secret, String sessionId) {
        return this;
    }

    /**
     * Устанавливает статус подключения гаджета к сети
     *
     * @param isCheck true проверить состояние подключения
     * @return
     */
    public ApiHandler<T> checkNetState(boolean isCheck) {
        ConnectivityManager connetManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connetManager.getActiveNetworkInfo();
        this.enableNet = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        return this;
    }

    /**
     * Установка ожидающего окна
     *
     * @param message сообщение диалога
     * @return
     */
    public ApiHandler<T> enableProcessView(String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.show();
        this.dialog = progressDialog;
        return this;
    }

    /**
     * Установка ожидающего окна
     *
     * @param strResId идентификатор строкового ресурса
     * @return
     */
    public ApiHandler<T> enableProcessView(int strResId) {
        return enableProcessView(context.getString(strResId));
    }

    private void alwaysAfter() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    public void request(final FeedBack<T> feedBack) {
        if (!enableNet) {
            alwaysAfter();
            feedBack.viewErrorMessage("Нет подключения к интернету. Пожалуйста, подключитесь и повторите попытку", -1);
            return;
        }
        Call<T> call = feedBack.getCallableApi(header);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                alwaysAfter();
                if (response.isSuccessful()) {
                    if (StringUtils.isNotBlank(response.headers().get(KeyHeader.IS_ERROR))) {
                        try {
                            String msgserver = response.headers().get(KeyHeader.MESSAGE_ERROR);
                            String message = new String(Base64.decode(msgserver, 0), "utf-8");
                            int code = 0;
                            try {
                                code = Integer.valueOf(response.headers().get(KeyHeader.CODE_ERROR));
                            } catch (Exception e) {
                            }
                            feedBack.viewErrorMessage(message, code);
                        } catch (Exception e) {
                            feedBack.viewErrorMessage("Проблема раскодировки сообщения об ошибке", 0);
                        }
                    } else if (response.body() != null) {
                        feedBack.successful(response.body(), response.headers().get(KeyHeader.SESSION));
                    } else {
                        feedBack.viewErrorMessage("Сервис ничего не вернул", 0);
                    }
                } else {
                    feedBack.viewErrorMessage(null, 0);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                try {
                    alwaysAfter();
                    feedBack.viewErrorMessage(null, 0);
                    Log.e(TAG, "An error request", t);
                } catch (Exception e) {
                    Log.w(TAG, "An error postFailure", e);
                }
            }
        });
    }

    /**
     * Интерфейс резульатат запроса
     *
     * @param <T>
     */
    public interface FeedBack<T> {

        @NonNull
        Call<T> getCallableApi(Map<String, String> header);

        /**
         * Результат запроса
         *
         * @param result
         */
        void successful(T result, String sessionId);

        /**
         * Устанавливает сообщение об ошибке
         *
         * @param message
         */
        void viewErrorMessage(String message, int errorCode);

    }


//    /**
//     * Класс подписи запроса
//     */
//    public static class Signature {
//
//        private final String sign;
//
//        /**
//         * Конструктор подписи
//         *
//         * @param value  подписываемое значение
//         * @param secret секретное слово
//         */
//        public Signature(String value, String secret) {
//            if (StringUtils.isBlank(value)) {
//                throw new NullPointerException("Подписываемое значение не может быть null или пустое");
//            }
//            if (StringUtils.isBlank(secret)) {
//                throw new NullPointerException("Секретное слово не может быть null или пустое");
//            }
//            this.sign = crypto(value, secret);
//        }
//
//        String getSign() {
//            return sign;
//        }
//
//        private String crypto(final String value, final String secretKey) {
//            String hash = null;
//            String message = value + secretKey;
//            try {
//                MessageDigest m = MessageDigest.getInstance("MD5");
//                m.update(message.getBytes());
//                hash = new BigInteger(1, m.digest()).toString(16);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//            return hash;
//        }
//    }

}
