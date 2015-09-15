package code.jesse.palette.tools;

import code.jesse.palette.R;

/**
 * 定义了框架内使用到的Tag.
 *
 * @author zhulantian@gmail.com
 */
public interface Tags {

    /**
     * Desc: 这个Tag放置的是这个View对应的layout id, 也就是XML的资源文件id. Usage: 用于在循环使用View的时候衡量View是否可以被复用.
     */
    int TAG_LAYOUT_ID = R.id.tag_layout_id;

    /**
     * Desc: 这个Tag放置的是绑定这个View使用的Presenter对象. Usage: 循环使用View的时候也会循环使用Presenter.
     */
    int TAG_CARD_PRESENTER = R.id.tag_card_presenter;

    /**
     * Desc: 这个Tag放置的动态Bind的取值. Usage: {@link code.jesse.palette.bind.BindingPresenter}.
     */
    int TAG_BIND_VALUE = R.id.tag_bind_value;

    /**
     * Desc: 用于放置recycleBin. 一般是一个ListView对应一个.
     */
    int TAG_RECYCLE_BIN = R.id.tag_recycle_bin;
}
