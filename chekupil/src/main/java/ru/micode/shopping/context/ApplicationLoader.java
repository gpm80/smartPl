package ru.micode.shopping.context;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import ru.micode.shopping.service.task.NotificationHandler;
import ru.micode.shopping.service.task.NotificationService;
import ru.micode.shopping.ui.ShoppingActivity;

/**
 * Created by Petr Gusarov on 05.09.18.
 */
public class ApplicationLoader extends Application {

    public static volatile Context applicationContext;
    private final static String LOG_TAG = ApplicationLoader.class.getSimpleName();
    public static volatile boolean applicationInited;
    private static volatile Boolean currentIsDarkTheme = null;
    private static volatile Boolean tempIsDarkTheme = null;
    private static volatile long intervalNotification = 300;
    public static String shoplistBroadcastAction = ShoppingActivity.class.getSimpleName();


    public static class PrefKey {
        public final static String INIT = "initSetting";
        public final static String NOTIFICATION_ON = "listenService";
        public final static String INTERVAL_NOTIF = "pauseListen";
        public final static String IS_DARK_THEME = "darkTheme";
        public final static String IS_READY_FEEDBACK = "isReadyFeedback";
        public final static String IS_VIEW_BUY_HELP = "isViewBuyHelp";
        public final static String IS_VIEW_RECIPE_HELP = "isViewRecipeHelp";
        public final static String[] INTERVAL_VALUES = new String[]{"60", "300", "1800"};
//        public final static String[] INTERVAL_VALUES = new String[]{"60", "300", "1800", "10"};
    }

    /**
     * Отправлят широковещание
     *
     * @param action
     */
    public static void sendBroadcast(String action) {
        try {
            if (applicationContext != null) {
                applicationContext.sendBroadcast(new Intent(action));
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "an error sendBroadcast", e);
        }
    }

    @Override
    public void onCreate() {
        applicationContext = getApplicationContext();
        initSetting();
        startPushService();
    }

    /**
     * Перенастройка частоты активности сервиса
     *
     * @param working true - стартовая активность в работе;
     *                false - активность прекращена
     */
    public static void workActivity(boolean working) {
        Log.i(LOG_TAG, "intervalNotification workActivity=" + working);
        if (working) {
            intervalNotification = 5;
        } else {
            initSetting();
        }
    }

    @Override
    public void onTerminate() {
        Log.i(LOG_TAG, "onTerminate application");
        // Востановим настройки сервиса
        initSetting();
        super.onTerminate();
    }

    public static void postInitApplication() {
        if (applicationInited) {
            return;
        }
        applicationInited = true;
    }

    /**
     * Возвращает флаг включения темной темы
     *
     * @return true, если выбрана темная тема, иначе false
     */
    public static boolean isDarkTheme() {
        if (currentIsDarkTheme == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            currentIsDarkTheme = preferences.getBoolean(PrefKey.IS_DARK_THEME, false);
            tempIsDarkTheme = currentIsDarkTheme;
        }
        return currentIsDarkTheme;
    }

    /**
     * Проверяет отметку готовности пользователя к оценке приложения
     *
     * @return
     */
    public static boolean isReadyFeedback() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        int feedback = preferences.getInt(PrefKey.IS_READY_FEEDBACK, 0);
        return feedback == 1;
    }

    /**
     * Возвращает настройки приложения
     *
     * @return
     */
    public static SharedPreferences getSharedPreferences() {
        if (applicationContext != null) {
            return PreferenceManager.getDefaultSharedPreferences(applicationContext);
        }
        return null;
    }

    /**
     * Устанавливает значение настроек
     *
     * @param prefKey ключ
     * @param value   значение
     */
    public static void setSharedPreferences(String prefKey, boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences != null) {
            try {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean(prefKey, value);
                edit.apply();
            } catch (Exception e) {
                Log.e(LOG_TAG, "an error save " + prefKey, e);
            }
        }
    }


    public static int getContextColor(int colorResId) {
        try {
            return ContextCompat.getColor(applicationContext, colorResId);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Горячее переключение стиля активности
     *
     * @param activity активность которая инициирует переключение
     */
    public static void checkTheme(Activity activity) {
        if (currentIsDarkTheme != tempIsDarkTheme) {
            currentIsDarkTheme = null;
            changeThemeForce(activity, isDarkTheme());
        }
    }

    /**
     * Принудительное изменение темы активности
     *
     * @param activity активность
     * @param dark
     */
    public static void changeThemeForce(Activity activity, boolean dark) {
        currentIsDarkTheme = dark;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /**
     * Запускает фоновый сервис опроса данных
     */
    public static void startPushService() {
        if (isOnNotificationService()) {
            AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
            Intent intentService = new Intent(applicationContext, NotificationService.class);
            PendingIntent pendingIntent = PendingIntent.getService(applicationContext, 0, intentService, 0);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, pendingIntent);
        } else {
            stopPushService();
        }
    }

    /**
     * Проверяет работоспособность сервиса
     *
     * @return
     */
    public static boolean checkPushService() {
        ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationService.class.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет подключение к интернту
     *
     * @return
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public static boolean isOnNotificationService() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        return preferences.getBoolean(PrefKey.NOTIFICATION_ON, true);
    }

    public static void stopPushService() {
        applicationContext.stopService(new Intent(applicationContext, NotificationService.class));
        PendingIntent pintent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationService.class), 0);
        AlarmManager alarm = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
    }

    public static boolean setIntervalNotification(String valueNumber) {
        Log.i(LOG_TAG, "intervalNotification value=" + valueNumber);
        try {
            Long value = Long.valueOf(valueNumber);
            if (value > 0) {
                intervalNotification = value;
                Log.i(LOG_TAG, "notification service change interval = " + value);
                return true;
            }
            Log.w(LOG_TAG, "notification service interval notification < 0");
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        return false;
    }

    public static long getIntervalNotification() {
        return intervalNotification;
    }

    /**
     * Инициализация первичных настроек приложения
     *
     * @return
     */
    private static boolean initSetting() {
        Log.d(LOG_TAG, "notification service Проверка настроек");
        Context context = applicationContext;
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            // Проверим инициализированны ли настройки
            boolean init = preferences.getBoolean(PrefKey.INIT, false);
            if (!init) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean(PrefKey.INIT, true);
                edit.putBoolean(PrefKey.NOTIFICATION_ON, true);
                edit.putString(PrefKey.INTERVAL_NOTIF, PrefKey.INTERVAL_VALUES[1]);
                edit.apply();
            }
            setIntervalNotification(preferences.getString(PrefKey.INTERVAL_NOTIF, PrefKey.INTERVAL_VALUES[1]));
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        return false;
    }

    /**
     * Обрабатывет теги сообщения для отображения жирного текста и переноса строк
     *
     * @param resId id ресурса текста с тегами
     * @return
     */
    public static SpannableStringBuilder replaceTags(int resId) {
        return replaceTags(applicationContext.getString(resId));
    }

    /**
     * Обрабатывет теги сообщения для отображения жирного текста и переноса строк
     *
     * @param str текст с тегами
     * @return
     */
    public static SpannableStringBuilder replaceTags(String str) {
        try {
            int start;
            int end;
            StringBuilder stringBuilder = new StringBuilder(str);

            while ((start = stringBuilder.indexOf("<br>")) != -1) {
                stringBuilder.replace(start, start + 4, "\n");
            }
            while ((start = stringBuilder.indexOf("<br/>")) != -1) {
                stringBuilder.replace(start, start + 5, "\n");
            }
            ArrayList<Integer> bolds = new ArrayList<>();
            while ((start = stringBuilder.indexOf("<b>")) != -1) {
                stringBuilder.replace(start, start + 3, "");
                end = stringBuilder.indexOf("</b>");
                if (end == -1) {
                    end = stringBuilder.indexOf("<b>");
                }
                stringBuilder.replace(end, end + 4, "");
                bolds.add(start);
                bolds.add(end);
            }
            while ((start = stringBuilder.indexOf("**")) != -1) {
                stringBuilder.replace(start, start + 2, "");
                end = stringBuilder.indexOf("**");
                if (end >= 0) {
                    stringBuilder.replace(end, end + 2, "");
                    bolds.add(start);
                    bolds.add(end);
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), bolds.get(a * 2), bolds.get(a * 2 + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                new UnderlineSpan()
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
        return new SpannableStringBuilder(str);
    }
}

