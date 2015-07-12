package code.jesse.palette;

/**
 * 描述了将一个Model对象绑定到View上的逻辑.
 * Presenter是基于MVVM的理念设计的，对应于ViewModel，它与View是一一对应的。
 * 这套MVP框架设计的理念是在MVVM的基础上更进一步简化，把View更进一步简化到XML，把ViewHolder去掉
 * 而使用CardPresenter代替，并采用组合模式来大幅度提升Presenter的复用性.
 *
 * @see ViewPresenter
 * @see CardPresenter
 * @see SerialPresenter
 *
 * @author zhulantian@gmail.com
 */
public interface Presenter {

    /**
     * 将Model绑定到View单元上。
     *
     * @param model 数据源Model。
     */
    void bind(Object model);

    /**
     * 在Presenter被遗弃时被调用。
     *
     * 一些通用的Presenter，在遇到不同的Model.Type时会需要重新生成新的Presenter，
     * 这时旧的Presenter就会被遗弃或者暂时回收，此时这个方法会被调用。
     *
     * 如果你的Presenter在被丢弃时还可能工作(例如反注册listener)，那么你需要重写这个方法，
     * 并保证调用完这个方法能正常的回收或遗弃此Presenter。
     */
    void unbind();
}
