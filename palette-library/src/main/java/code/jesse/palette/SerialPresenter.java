package code.jesse.palette;


/**
 * Presenter序列组合成的Presenter，用于将多个Presenter单元都作用于一个viewId上的情况。 利用SerialPresenter可以将复杂Presenter逻辑拆分成多个串行的Presenter，以提高复用程度，
 * 以及提高代码可读性。
 * <p/>
 * 请注意，presenters是有序的。
 *
 * @author zhulantian@gmail.com
 */
public class SerialPresenter extends ViewPresenter {

    private final ViewPresenter[] mPresenters;

    public SerialPresenter(ViewPresenter... presenters) {
        this.mPresenters = presenters;
    }

    @Override
    public void bind(Object model) {
        if (mPresenters == null || mPresenters.length == 0) {
            return;
        }
        for (int i = 0; i < mPresenters.length; ++i) {
            passBind(mPresenters[i], model);
        }
    }

    @Override
    public void unbind() {
        if (mPresenters == null || mPresenters.length == 0) {
            return;
        }
        for (int i = 0; i < mPresenters.length; ++i) {
            mPresenters[i].unbind();
        }
    }
}
