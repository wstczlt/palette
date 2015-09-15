package code.jesse.palette;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import code.jesse.palette.tools.ViewHelper;

/**
 * CardPresenter是用来把一个Model绑定到当前的Card上的, 参考{@link #bind(Object)}。
 *
 * 因为CardPresenter与一张卡片是对应，对卡片的编程都是面向与CardPresenter的，
 * 一个CardPresenter完全表征了一张卡片的绑定逻辑，是整个UI层最核心的类。
 *
 * 它的工作原理是组合模式，由一组{@link ViewPresenter}构成，其中的每个ViewPresenter都负责一个
 * 对应的View(通过ViewId来表示)的绑定逻辑，在执行{@link #bind(Object)}时，会逐个调用这些
 * ViewPresenter，从而完成整个Card的绑定。
 *
 * CardPresenter提供了一个{@link #helper()}方法来获取对所属View的简化操作对象，Helper封装了
 * 大量对View的操作，从而方便简化开发。当然，是否使用Helper这是可选的。
 *
 * 请注意，CardPresenter(以及ViewPresenter)是与View一一对应的，也就是说一旦CardPresenter被
 * 创建之后，它就隶属于这个View了，所以变量{@link #mView}是final修饰的，可变的是Model，每次绑定都
 * 会重新执行{@link #bind(Object)}。
 *
 * 如果要对设置好的CardPresenter做修改，如下:<br/>
 * 1. 添加，CardPresenter.set(R.id,xxx, xxxBinder, false);<br/>
 * ---> OR: CardPresenter.add(R.id.xxx, xxxBinder);<br/>
 * 2. 修改/替换，CardPresenter.set(R.id.xxx, xxxBinder, false);<br/>
 * ---> OR: CardPresenter.replace(R.id.xxx, xxxBinder);<br/>
 * 3. 删除，CardPresenter.set(R.id.xxx, null, false);<br/>
 * ---> OR: CardPresenter.remove(R.id.xxx);<br/>
 *
 * @see Presenter
 * @see ViewPresenter
 * @see ViewHelper
 *
 * @author zhulantian@gmail.com
 */
public final class CardPresenter implements Presenter {

    public static final int CARD_ID = 0;
    private static final String LOG_TAG = "Presenter";

    public final View mView;
    public final Context mContext;

    private final ViewHelper mHelper;
    private final SparseArray<ViewPresenter> mPresenters;

    /**
     * 当前使用的model对象，每次bind都会被重新赋值, unbind会置空.
     */
    public Object mModel;

    public CardPresenter(View view) {
        mView = view;
        mContext = view.getContext();
        mHelper = new ViewHelper(mView);
        mPresenters = new SparseArray<>();
    }

    /**
     * 把Model绑定到当前的ViewGroup上。
     * <p/>
     * 方法会串行执行{@link #mPresenters}中的所有Presenter的 {@link Presenter#bind(Object)}方法， 最终完成整个Card的绑定逻辑。
     * <p/>
     * 请注意，根据ViewId查找View的逻辑是lazy的，并且在{@link #helper()}中封装了查找缓存的逻辑，
     * 因此并不需要担心findViewById的效率问题，CardPresenter本身起到了ViewHolder的作用。
     *
     * @param model 需要绑定到当前View的数据源。
     * @see Presenter
     */
    public void bind(Object model) {
        this.mModel = model;
        // bind elements
        for (int i = 0; i < mPresenters.size(); ++i) {
            Presenter presenter = find(i);
            if (presenter != null) {
                presenter.bind(model);
            }
        }
    }

    /**
     * 方法或解绑整张卡片上的所有Presenter.
     */
    public void unbind() {
        this.mModel = null;
        for (int i = 0; i < mPresenters.size(); ++i) {
            Presenter presenter = find(i);
            if (presenter != null) {
                presenter.unbind();
            }
        }
    }

    private ViewPresenter find(int i) {
        View elementView;
        int viewId = mPresenters.keyAt(i);
        if (viewId == CARD_ID) {
            elementView = mView;
        } else {
            elementView = helper().id(viewId).getView();
        }
        if (elementView == null) {
            Log.w(LOG_TAG,
                    "IGNORED, VIEW ID NOT FOUND:" + Integer.toHexString(mPresenters.keyAt(i)));
            return null;
        } else {
            ViewPresenter presenter = mPresenters.valueAt(i);
            presenter.mView = elementView;
            return presenter;
        }
    }

    public View view() {
        return mView;
    }

    public ViewHelper helper() {
        return mHelper;
    }

    /**
     * 指定某个View的Presenter。
     * <p/>
     * 参数要求使用ViewId，因此要求针对Presenter的编程都需要使用ViewId作为基本单元。
     * <p/>
     * 方法返回当前CardPresenter对象，因此支持链式调用。
     * <p/>
     * 请注意View并不是立刻bind的，会在调用{@link #bind(Object)}的时赋值。
     *
     * @param id 对应的ViewId。
     * @param presenter View对应的Presenter。
     * @param replace 指定如果已经存在这个ViewId的Presenter时如何处理，true则替换之前的，
     *            false则将这个binder串联到之前的binder上。
     * @return self.
     * @see SerialPresenter
     */
    public CardPresenter set(int id, ViewPresenter presenter, boolean replace) {
        if (presenter == null) {
            if (replace) { // remove all exist for the id
                ViewPresenter oldPresenter = mPresenters.get(id);
                if (oldPresenter != null && oldPresenter.hasBind()) {
                    oldPresenter.unbind();
                }
                mPresenters.remove(id);
            }
            return this;
        }

        ViewPresenter exist = mPresenters.get(id);
        if (exist == presenter) { // repeat
            return this;
        }
        presenter.mCardPresenter = this;
        if (!replace && exist != null) { // already exist
            presenter = new SerialPresenter(exist, presenter);
            presenter.mCardPresenter = this;
        }
        mPresenters.put(id, presenter);
        if (replace && exist != null && exist.hasBind()) {
            exist.unbind();
        }
        return this;
    }


    /**
     * 将一个ViewBinder添加到对应ID的绑定逻辑上。
     *
     * @see {@link #set(int, ViewPresenter, boolean)}
     */
    public CardPresenter add(int id, ViewPresenter presenter) {
        return set(id, presenter, false);
    }

    /**
     * 将一个ViewBinder添加到整个Card绑定逻辑上。
     *
     * @see {@link #add(int, ViewPresenter)}
     */
    public CardPresenter add(ViewPresenter presenter) {
        return add(CARD_ID, presenter);
    }


    /**
     * 替换指定ID已有的ViewBinder。
     *
     * @see {@link #set(int, ViewPresenter, boolean)}
     */
    public CardPresenter replace(int id, ViewPresenter presenter) {
        return set(id, presenter, true);
    }

    /**
     * 删除指定ID全部的ViewBinder。
     *
     * @see {@link #set(int, ViewPresenter, boolean)}
     */
    public CardPresenter remove(int id) {
        return set(id, null, false);
    }

}
