package ru.micode.shopping.service.task;

import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import ru.micode.shopping.context.ApplicationLoader;

/**
 * Сервис уведомлений
 * Created by Petr Gusarov on 05.09.18.
 */
public class NotificationService extends Service {

    private final static String LOG_TAG = NotificationService.class.getSimpleName();
    private ListenThread listenThread;

    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "notification service created");
//        ApplicationLoader.postInitApplication();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopPrevious();
        Log.i(LOG_TAG, "notification service start");
        listenThread = new ListenThread(startId);
        new Thread(listenThread).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopPrevious();
        Log.i(LOG_TAG, "notification service destroy");
        if (ApplicationLoader.isOnNotificationService()) {
            Intent intent = new Intent("ru.micode.shopping.start");
            sendBroadcast(intent);
        }
    }

    /**
     * Тормозит предыдущий поток, если он был
     */
    private void stopPrevious() {
        if (listenThread != null) {
            listenThread.stop();
        }
    }

    /**
     * Обработчик потока сервиса.
     */
    private static class ListenThread implements Runnable {

        final String LOG_TAG = ListenThread.class.getSimpleName();
        final int startId;
        private boolean enable;
        private long lastActive = 0;
        private final NotificationHandler notificationHandler;

        /**
         * Конструктор
         *
         * @param startId системный ID сервиса
         */
        public ListenThread(int startId) {
            this.startId = startId;
            notificationHandler = NotificationHandler.getInstance();
            this.enable = true;
            Log.i(LOG_TAG, "Created run id=" + startId);
        }

        @Override
        public void run() {
            while (enable) {
                long now = System.currentTimeMillis();
                long currentInterval = ApplicationLoader.getIntervalNotification();
                Log.i(LOG_TAG, "notification service current interval=" + currentInterval);
                long nextPoint = lastActive + TimeUnit.SECONDS.toMillis(currentInterval);
                if (nextPoint < now) {
                    lastActive = now;
                    try {
                        Log.i(LOG_TAG, "notification service job");
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "", e);
                    }
                }
                pause();
            }
        }

        /**
         * Останавливает сервиса опроса
         */
        public void stop() {
            Log.i(LOG_TAG, "notification service stop");
            enable = false;
        }

        /**
         * Пауза потока
         */
        private void pause() {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }
    }
}
