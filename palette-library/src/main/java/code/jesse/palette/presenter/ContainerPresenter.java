package code.jesse.palette.presenter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import code.jesse.palette.R;
import code.jesse.palette.ViewPresenter;
import code.jesse.palette.tools.RecycleBin;
import code.jesse.palette.tools.ViewTagger;


/**
 * @author zhulantian@gmail.com
 */
public abstract class ContainerPresenter extends ViewPresenter {

    private int mLayoutId;
    private RecycleBin<View> mRecycleBin;

    @Override
    public void bind(Object model) {
        ensureRecycleBin();
        ViewGroup container = (ViewGroup) view();
        int i;
        int itemCount = getItemCount(model);
        for (i = 0; i < itemCount; ++i) {
            View itemView;
            if (i < container.getChildCount()) {
                itemView = container.getChildAt(i);
            } else {
                itemView = createItemView(container);
                container.addView(itemView);
            }
            onBindItemView(itemView, i, model);
        }
        while (i < container.getChildCount()) {
            View reusedView = container.getChildAt(i);
            container.removeView(reusedView);
            if (mLayoutId != 0) {
                mRecycleBin.put(mLayoutId, reusedView);
            }
        }
    }

    protected final View createItemView(ViewGroup parent) {
        View itemView;
        if (mLayoutId == 0) {
            itemView = onCreateItemView(parent);
            mLayoutId = ViewTagger.getLayoutId(itemView);
        } else {
            itemView = mRecycleBin.get(mLayoutId);
            if (itemView == null) {
                itemView = onCreateItemView(parent);
            }
        }

        return itemView;
    }

    private void ensureRecycleBin() {
        if (mRecycleBin != null) {
            return;
        }
        View view = getAttachViewForRecycleBin();
        mRecycleBin = ViewTagger.getRecycleBin(view);
        if (mRecycleBin == null) {
            mRecycleBin = new RecycleBin<>();
            ViewTagger.setRecycleBin(view, mRecycleBin);
        }
    }


    protected abstract int getItemCount(Object model);

    protected abstract View onCreateItemView(ViewGroup parent);

    protected abstract void onBindItemView(View itemView, int i, Object model);

    protected View getAttachViewForRecycleBin() {
        return ((Activity) card().mContext).findViewById(R.id.list);
    }
}
