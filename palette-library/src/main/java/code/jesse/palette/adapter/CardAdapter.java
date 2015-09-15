package code.jesse.palette.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import code.jesse.palette.CardPresenter;
import code.jesse.palette.tools.ViewTagger;

/**
 * 使用Presenter(MVP)模式的ListAdapter.
 *
 * @author zhulantian@gmail.com
 */
public abstract class CardAdapter<T> extends BaseAdapter {

    private final List<T> mList;

    public CardAdapter() {
        mList = new ArrayList<>();
    }

    public void setList(List<T> list) {
        mList.clear();
        if (list != null) {
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardPresenter presenter;
        if (convertView == null) {
            presenter = onCreatePresenter(position, parent);
            ViewTagger.setCardPresenterTag(presenter.view(), presenter);
        } else {
            presenter = ViewTagger.getCardPresenter(convertView);
        }
        onBindPresenter(position, presenter);
        return presenter.view();
    }

    /**
     * 创建CardPresenter.
     *
     * @param position 当前Item的position.
     * @param parent   容器.
     * @return the card presenter.
     */
    protected abstract CardPresenter onCreatePresenter(int position, ViewGroup parent);

    /**
     * 使用CardPresenter绑定一张Card.
     *
     * @param position  当前Item的position.
     * @param presenter 使用的CardPresenter.
     */
    protected void onBindPresenter(int position, CardPresenter presenter) {
        presenter.bind(getItem(position));
    }
}
