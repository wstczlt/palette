package code.jesse.palette.bind;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import code.jesse.palette.R;
import code.jesse.palette.tools.ViewTagger;

/**
 * @author zhulantian@gmail.com
 */
public class ViewFactory implements LayoutInflater.Factory {

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    private final LayoutInflater mInflater;

    public ViewFactory(LayoutInflater inflater) {
        mInflater = inflater;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.indexOf('.') == -1) {
            for (String prefix : sClassPrefixList) {
                try {
                    view = mInflater.createView(name, prefix, attrs);
                    if (view != null) {
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }
        } else {
            try {
                view = mInflater.createView(name, null, attrs);
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        setBindTag(view, attrs);
        return view;
    }

    public final LayoutInflater getInflater() {
        return mInflater;
    }

    private void setBindTag(View view, AttributeSet attrs) {
        TypedArray a = view.getContext().obtainStyledAttributes(attrs, R.styleable.Binding);
        String bindValue = a.getString(R.styleable.Binding_bindValue);
        if (!TextUtils.isEmpty(bindValue)) {
            ViewTagger.setBindValue(view, bindValue);
        }
        a.recycle();
    }
}
