package code.jesse.palette.presenter;

import android.view.View;

import code.jesse.palette.ViewPresenter;


/**
 * @author zhulantian@gmail.com
 */
public abstract class ButtonPresenter extends ViewPresenter implements View.OnClickListener {

    private Object mModel;

    @Override
    public void bind(Object model) {
        this.mModel = model;
        resetState(model);
        helper().clicked(this);
    }

    @Override
    public void unbind() {
        mModel = null;
        helper().clicked(null);
    }

    @Override
    public void onClick(View v) {
        onClick(v, mModel);
    }


    /**
     * 在bind时调用, 初始化状态.
     *
     * @param model 当前绑定的model.
     */
    protected void resetState(Object model) {
        // default do nothing
    }

    /**
     * 在当前button被点击时调用.
     *
     * @param v     被点击的View.
     * @param model 当前绑定的model.
     */
    protected abstract void onClick(View v, Object model);
}
