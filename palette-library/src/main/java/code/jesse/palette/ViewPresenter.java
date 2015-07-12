package code.jesse.palette;

import android.view.View;

import code.jesse.palette.tools.ViewHelper;

/**
 * View绑定的基本单元，从属于CardPresenter，负责将数据源Model绑定到Card的一个View单元上，不能单独存在。
 *
 * 另外，ViewPresenter由于是面向Object作为Model对象，并未做类型限定，如果业务是Model确定的，那么建议
 * 继承这个类覆盖{@link #bind(Object)}方法，强转为业务需要的类型来使用。
 * Example:
 * public abstract class BaseViewPresenter extends ViewPresenter {
 *
 * #Override
 * protected final void bind(Object model) {
 * bind((SpecialModel) model);
 * }
 *
 * protected abstract void bind(SpecialModel model);
 * }
 *
 * 重要，关于如何复用:
 * 1. 通过继承
 * ViewPresenter也是最基本的复用单元，建议把可复用的绑定逻辑都封装成ViewPresenter，同时ViewPresenter
 * 是允许继承的，支持特性扩展。不过建议继承不宜过深，过深的逻辑应该是可以拆分出去的，参见复用2。
 * 2. 通过拆分
 * CardPresenter的bind方法是允许对一个ViewId绑定多个ViewPresenter的，串行调用，因此可以把复杂
 * 的绑定逻辑拆分到几个ViewPresenter中，例如TextButton需要设置状态又需要控制progress，因此
 * 把progress单独拆分成一个ViewPresenter，这个ProgressPresenter还可以被其它业务直接复用。
 *
 * Example:
 * // base binder
 * CardPresenter.bind(R.id.title, new BasePresenter());
 * // custom binder
 * ViewGroupBinder.bind(R.id.banner, new BannerPresenter());
 * // serial binder
 * ViewGroupBinder.bind(new SerialBinder(new PostButtonPresenter(), new ProgressButtonPresenter()));
 *
 * @see CardPresenter
 * @see SerialPresenter
 *
 * @author zhulantian@gmail.com
 */
public abstract class ViewPresenter implements Presenter {

    View mView;
    CardPresenter mCardPresenter;
    private ViewHelper mHelper;

    public void unbind() {}

    public final View view() {
        throwIfIllegalState();
        return mView;
    }

    public final CardPresenter card() {
        throwIfIllegalState();
        return mCardPresenter;
    }

    public final ViewHelper helper() {
        if (mHelper == null) {
            mHelper = new ViewHelper(view());
        }
        return mHelper;
    }

    public final boolean hasBind() {
        return mView != null;
    }

    /**
     * 传递onBind方法给另一个Presenter。
     * 这个Presenter的view和card必须与为空或者当前的presenter完全一样。
     *
     * @see #passBind(ViewPresenter, View, Object)
     */
    protected void passBind(ViewPresenter presenter, Object model) {
        passBind(presenter, mView, model);
    }

    /**
     * 传递onBind方法给另一个Presenter。
     *
     * 请注意，这个方法负责安全的转调，且必须使用这个方法转调，因为方法内会判断所属的ViewGroup是否
     * 相同，仅支持group为空或者group相同的情况。
     *
     * 一般用于拆分复用Presenter的情况，因为一个大Presenter可能组合了几个小Presenter，在被
     * 调用onBind()时需要转调给子Presenter。
     *
     * @param presenter 传递目标。
     * @param targetView presenter需要作用在的view，这个View还必须在当前的卡片上。
     * @param model 传递的参数Model。
     *
     * @see #bind(Object)
     * @see SerialPresenter
     */
    protected void passBind(ViewPresenter presenter, View targetView, Object model) {
        if (presenter == null) {
            return;
        }
        if ((presenter.mCardPresenter == null || presenter.mCardPresenter == mCardPresenter)
                && (presenter.mView == null || presenter.mView == targetView)) {
            presenter.mView = targetView;
            presenter.mCardPresenter = mCardPresenter;
            presenter.bind(model);
        } else {
            throw new IllegalArgumentException("Must be in same card!");
        }
    }

    private void throwIfIllegalState() {
        if (!hasBind()) {
            throw new IllegalArgumentException("This method should not be invoke before bind.");
        }
    }
}
