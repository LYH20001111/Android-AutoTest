package com.hudou.autotest.base.fragment.listener;

public interface FragmentInterface {
    /**
     * 布局加载后，可用于数据初始化；(比如一些控件的定义，初始化等)
     */
     void onInitData();

    /**
     * 行为初始化，用于对布局控件的行为进行处理；（比如对按钮，等控件的行为进行设置）
     */
    void onActionAfterInitData();

    /**
     * 用于作为ViewPage对应标签页被选择时，触发该方法，进行相应的数据处理等；（当page被选中时，可在重新该方法进行自定义数据，行为处理）
     */
    void onFragmentVisibility();
}
