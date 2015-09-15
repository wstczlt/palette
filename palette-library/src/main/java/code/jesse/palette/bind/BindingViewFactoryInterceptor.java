package code.jesse.palette.bind;

import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import code.jesse.palette.R;
import code.jesse.palette.tools.ViewTagger;


/**
 * @author zhulantian@gmail.com
 */
public class BindingViewFactoryInterceptor {

    /**
     * 在View创建完成时为View设置其bindingValue的值.
     *
     * @param view  被创建的View.
     * @param attrs 被创建View的属性.
     */
    public void onViewCreated(View view, AttributeSet attrs) {
        TypedArray a = view.getContext().obtainStyledAttributes(attrs, R.styleable.Binding);
        String bindValue = a.getString(R.styleable.Binding_bindValue);
        if (!TextUtils.isEmpty(bindValue)) {
            ViewTagger.setBindValue(view, bindValue);
        }
        a.recycle();
    }
}
