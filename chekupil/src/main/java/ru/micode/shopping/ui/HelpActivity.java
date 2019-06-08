package ru.micode.shopping.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;
import ru.micode.shopping.ui.common.MyAbstractActivity;
import ru.micode.shopping.ui.common.MyAbstractFragment;

/**
 * Created by Petr Gusarov on 14.11.18.
 */
public class HelpActivity extends MyAbstractActivity {

    public static final String KEY_RES_ID_TEXT = HelpActivity.class.getSimpleName() + ":resIdText";

    @Override
    protected Fragment createFragment() {
        return new HelpFragment();
    }

    @Override
    protected boolean isViewBackButton() {
        return true;
    }

    @Override
    protected boolean isExit() {
        return true;
    }

    /**
     * Фрагмент инструкции
     */
    public static class HelpFragment extends MyAbstractFragment {

        private TextView helpTextView;

        @Override
        protected int getResIdFragment() {
            return R.layout.help_activity;
        }

        @Override
        protected int getResIdMenu() {
            return 0;
        }

        @Override
        protected void initFragment(View view) {
            getActivity().setTitle(R.string.help_empty_caption);
            helpTextView = view.findViewById(R.id.help_context);
        }

        @Override
        protected void refresh() {
            int intExtra = getActivity().getIntent().getIntExtra(KEY_RES_ID_TEXT, 0);
            if (intExtra != 0) {
                helpTextView.setText(ApplicationLoader.replaceTags(intExtra));
            } else {
                helpTextView.setText(R.string.help_empty_text);
            }
        }
    }
}
