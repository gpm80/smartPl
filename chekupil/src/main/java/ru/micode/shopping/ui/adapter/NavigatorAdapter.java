package ru.micode.shopping.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.micode.shopping.R;
import ru.micode.shopping.ui.adapter.recycler.ItemTouchListener;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerAdapter;
import ru.micode.shopping.ui.adapter.recycler.MyRecyclerHolder;

/**
 * Created by Petr Gusarov on 01.03.18.
 */
public class NavigatorAdapter extends MyRecyclerAdapter<NavigatorBean> {

    public NavigatorAdapter(Context context, ItemTouchListener<NavigatorBean> actionListener) {
        super(context, new NavigatorBean(0, 0, 0, 0, Object.class), new ArrayList<NavigatorBean>(), actionListener);
    }

    @Override
    public NavigatorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NavigatorHolder(context, parent, this);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder<NavigatorBean> holder, int position) {
        holder.bind(values.get(position), position);
    }

    public static class NavigatorHolder extends MyRecyclerHolder<NavigatorBean> {

        private TextView caption;
        private TextView description;
        private ImageView image;
        private final Context context;

        public NavigatorHolder(Context context, ViewGroup parent, ActionListener<NavigatorBean> actionListener) {
            super(context, R.layout.navigator_adapter, parent, actionListener);
            this.context = context;
            image = itemView.findViewById(R.id.navigator_image);
            caption = itemView.findViewById(R.id.navigator_caption);
            description = itemView.findViewById(R.id.navigator_description);
        }

        @Override
        protected void bindData(NavigatorBean value, int position) {
            caption.setText(value.getCaption(context));
            description.setText(value.getDescription(context));
            image.setImageResource(value.getImageResId());
        }
    }
}
