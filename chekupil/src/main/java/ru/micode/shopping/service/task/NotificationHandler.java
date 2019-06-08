package ru.micode.shopping.service.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;

/**
 * Created by Petr Gusarov on 05.09.18.
 */
public class NotificationHandler {

    private static final String LOG_TAG = NotificationHandler.class.getSimpleName();
    private static final int NOTIF_ID_NEW_SHOPLIST = 100;
    private static final int NOTIF_ID_FRIEND = 101;
    private int counterTest;
    private static NotificationHandler notificationHandler;

    public static NotificationHandler getInstance() {
        if (notificationHandler == null) {
            notificationHandler = new NotificationHandler();
        }
        return notificationHandler;
    }

    private NotificationHandler() {
    }


    /**
     * Посылает уведомление о новых списках
     *
     * @param setting настройки
     * @param cls     активити, которое активируется при нажатии на уведомление
     */
    private void viewNotification(SettingNotification setting, Class<?> cls) {
        NotificationManager nm = (NotificationManager) ApplicationLoader.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(ApplicationLoader.applicationContext, cls);
        PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
//        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(ApplicationLoader.applicationContext, "notification");
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), R.drawable.ic_notification))
                .setTicker(setting.tiket)
                .setAutoCancel(true)
                .setContentTitle(setting.title)
                .setContentText(setting.message)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);
        Notification notification = (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) ?
                builder.getNotification() :
                builder.build();
        nm.notify(setting.id, notification);
    }

    /**
     * Параметры уведомления
     */
    private static class SettingNotification {

        public String tiket;
        public String title;
        public String message;
        public int id;

        private SettingNotification() {
            tiket = "";
            title = "";
            message = "";
        }

        public static SettingNotification builder() {
            return new SettingNotification();
        }

        public SettingNotification setId(int id) {
            this.id = id;
            return this;
        }

        public SettingNotification setTiket(String tiket) {
            this.tiket = tiket;
            return this;
        }

        public SettingNotification setTitle(String title) {
            this.title = title;
            return this;
        }

        public SettingNotification setMessage(String message) {
            this.message = message;
            return this;
        }
    }
}
