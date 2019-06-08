package ru.micode.shopping.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.R;
import ru.micode.shopping.model.Group;
import ru.micode.shopping.ui.common.IllegalValueException;
import ru.micode.shopping.ui.common.MyDialogAbstract;

/**
 * Диалог наименования группы
 * Created by Petr Gusarov on 19.03.18.
 */
public class GroupNameDialog extends MyDialogAbstract<Group> {

    private static final String VIEW_ID = GroupNameDialog.class.getSimpleName();
    public static final String RESULT_GROUP = "GroupNameDialog.resultGoup";
    private EditText textField;

    /**
     * @param group
     * @return
     */
    public static GroupNameDialog newInstance(Group group) {
        GroupNameDialog fragment = new GroupNameDialog();
        fragment.setArgumentsToFragment(group);
        return fragment;
    }

    @Override
    public String getTagView() {
        return VIEW_ID;
    }

    @Override
    public Dialog initDialog(final Group argGroup, Bundle bundle) {
        String initName = null;
        if (argGroup != null) {
            initName = argGroup.getName();
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.group_name_dialog, null);
        textField = (EditText) view.findViewById(R.id.group_name_field);
        if (StringUtils.isNotBlank(initName)) {
            textField.setText(initName);
        }
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.group_name_dialog_title)
            .setView(view)
            .setPositiveButton(R.string.group_name_dialog_ok_caption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        sendResult(Activity.RESULT_OK, RESULT_GROUP, commit(argGroup));
                    } catch (IllegalValueException e) {
                        Log.e(VIEW_ID, "", e);
                    }
                }
            })
            .setNegativeButton(R.string.group_name_dialog_cancel_caption, null)
            .create();
    }

    @Override
    public Group commit(Group sourceValue) throws IllegalValueException {
        String name = textField.getText().toString();
        sourceValue.setName(name);
        return sourceValue;
    }


}
