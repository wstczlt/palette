package code.jesse.palette.bind;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import code.jesse.palette.ViewPresenter;
import code.jesse.palette.bind.resolver.ResolveUtils;
import code.jesse.palette.tools.ViewTagger;

/**
 * @author zhulantian@gmail.com
 */
public class BindingPresenter extends ViewPresenter {

    private static final String LOG_TAG = "BindingPresenter";

    @Override
    public void bind(Object model) {
        String bindValue = ViewTagger.getBindValue(view());
        if (TextUtils.isEmpty(bindValue)) {
            Log.w(LOG_TAG, "No bind value found!");
            return;
        }
        // 此处可作为扩展入口，目前只实现了最简单的表达式
        // ==> Resolve的扩展
        // 此处可作为扩展入口，目前只实现了最简单的表达式
        // ==> Resolve的扩展
        //     1. 数据源:
        //          a. 参数Model为数据源, eg: ${.age}, ${.screenName}
        //          b. 上下文为数据源, eg: ${context.systemTime}, ${context.appName}
        //          c. 环境变量为数据源, eg: ${env.javaHome}, ${env.classPath}
        //          其它自定义数据源...
        //
        //     2. 表达式:
        //          a. 静态文本, eg: 'hello, mvp!', 123456, 3.14156
        //          b. 动态表达式, eg: ${model.age > 0 ? model.age : 0}, ${substring(model.title, 3)}
        //          其它自定义表达式...
        //
        //     3. 指令(代码执行):
        //          a. 动态执行一些指令，便于在运行时处理逻辑, eg: <mvp:playAnimation attr='${model.anim}'>
        //          其它自定义指令...
        //
        // ==> Bind扩展
        //      1. 针对不同结果类型, 例如Text, Number, Image, List做不同绑定方式
        //      其它自定义绑定方式...
        //
        Object resolvedValue = resolveValue(model, bindValue);
        bindInternal(resolvedValue);
    }

    private static Object resolveValue(Object model, String bindValue) {
        final String prefix = "${.";
        final String suffix = "}";
        if (bindValue.startsWith(prefix) && bindValue.endsWith(suffix)) {
            String resolvedValue = bindValue.substring(prefix.length());
            resolvedValue = resolvedValue.substring(0, resolvedValue.length() - suffix.length());

            return ResolveUtils.resolveValue(model, resolvedValue);
        }
        return bindValue;
    }

    private void bindInternal(Object value) {
        if (value == null) {
            bindNull(view());
        } else if (value instanceof CharSequence) {
            bindText((TextView) view(), (CharSequence) value);
        }
    }

    /**
     * 在属性为null时调用。
     */
    protected void bindNull(View view) {
        view.setVisibility(View.GONE);
    }

    /**
     * 在属性为{@link java.lang.CharSequence}时调用。
     */
    protected void bindText(TextView textView, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }
}
