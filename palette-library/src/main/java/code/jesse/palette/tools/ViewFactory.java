package code.jesse.palette.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import code.jesse.palette.bind.BindingViewFactoryInterceptor;

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
    private final BindingViewFactoryInterceptor mBindingInterceptor;

    public ViewFactory(LayoutInflater inflater) {
        mInflater = inflater;
        mBindingInterceptor = new BindingViewFactoryInterceptor();
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
        if (view != null) {
            intercept(view, attrs);
        }
        return view;
    }

    private void intercept(View view, AttributeSet attrs) {
        // 此处开启binding模块
        mBindingInterceptor.onViewCreated(view, attrs);
    }
}
