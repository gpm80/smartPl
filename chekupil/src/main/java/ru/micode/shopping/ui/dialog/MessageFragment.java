package ru.micode.shopping.ui.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

/**
 * Сообщение в свободной форме
 * Created by Petr Gusarov on 04.05.18.
 */
public class MessageFragment extends DialogFragment {

    private String title;
    private String message;
    private OkListener okListener;

    public static MessageFragment create() {
        return new MessageFragment();
    }

    public void show(FragmentActivity activity, int strMessageResId, OkListener okListener) {
        show(activity, null, activity.getString(strMessageResId), okListener);
    }

    public void show(FragmentActivity activity, String title, String message, OkListener okListener) {
        this.title = title;
        this.message = message;
        this.okListener = okListener;
        super.show(activity.getSupportFragmentManager(), "TAG_MESSAGE");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (okListener != null) {
                        okListener.ok();
                    }
                    dialog.dismiss();
                }
            })
            .setCancelable(false)
            .create();
    }

    /**
     * Слушатель нажатия кнопки Ok
     */
    public interface OkListener {
        void ok();
    }
}
