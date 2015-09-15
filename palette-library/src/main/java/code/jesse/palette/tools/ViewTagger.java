package code.jesse.palette.tools;

import android.view.View;

import code.jesse.palette.CardPresenter;

/**
 * View Tag工具类.
 *
 * @author zhulantian@gmail.com
 */
public class ViewTagger implements Tags {

    public static final int NO_ID = 0;

    private ViewTagger() {
        // utility
    }

    public static void setLayoutIdTag(View view, int layoutResId) {
        view.setTag(TAG_LAYOUT_ID, layoutResId);
    }

    public static int getLayoutId(View view) {
        Integer id = (Integer) view.getTag(TAG_LAYOUT_ID);
        return id == null ? NO_ID : id;
    }

    public static void setCardPresenterTag(View view, CardPresenter presenter) {
        view.setTag(TAG_CARD_PRESENTER, presenter);
    }

    public static CardPresenter getCardPresenter(View view) {
        return (CardPresenter) view.getTag(TAG_CARD_PRESENTER);
    }

    public static void setBindValue(View view, String bindValue) {
        view.setTag(TAG_BIND_VALUE, bindValue);
    }

    public static String getBindValue(View view) {
        return (String) view.getTag(TAG_BIND_VALUE);
    }

    public static <T> RecycleBin<T> getRecycleBin(View view) {
        return (RecycleBin<T>) view.getTag(TAG_CARD_PRESENTER);
    }

    public static void setRecycleBin(View view, RecycleBin<?> recycleBin) {
        view.setTag(TAG_RECYCLE_BIN, recycleBin);
    }
}
