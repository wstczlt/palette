package code.jesse.palette.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author zhulantian@gmail.com
 */
public class ViewInflater {

    private static ViewFactory sFactory;

    private ViewInflater() {
        // for utility class
    }

    /**
     * Creates a view.
     *
     * @param parent container
     * @param resId  resource id
     * @return view
     */
    public static <T extends View> T inflate(ViewGroup parent, int resId) {
        T result = (T) getInflater(parent.getContext()).inflate(resId, parent, false);
        ViewTagger.setLayoutIdTag(result, resId);

        return result;
    }

    /**
     * Creates a view.
     *
     * @param context context
     * @param resId   resource id
     * @return view
     */
    public static <T extends View> T inflate(Context context, int resId) {
        T result = (T) getInflater(context).inflate(resId, null);
        ViewTagger.setLayoutIdTag(result, resId);

        return result;
    }

    private static LayoutInflater getInflater(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context).cloneInContext(context);
        inflater.setFactory(new ViewFactory(inflater));
        return inflater;
    }
}
