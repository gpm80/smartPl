package ru.micode.shopping.context;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Petr Gusarov on 05.09.18.
 */
public class AppStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.startPushService();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ApplicationLoader.startPushService();
//            }
//        }).start();
    }
}
