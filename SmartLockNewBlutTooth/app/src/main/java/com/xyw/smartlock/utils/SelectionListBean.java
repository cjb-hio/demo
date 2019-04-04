package com.xyw.smartlock.utils;

/**
 * Created by 19428 on 2016/10/18.
 */
public class SelectionListBean {
    private String selectionBean1;
    private String selectionBean2;

    public String getSelectionBean1() {
        return selectionBean1;
    }

    public void setSelectionBean1(String selectionBean1) {
        this.selectionBean1 = selectionBean1;
    }

    public String getSelectionBean2() {
        return selectionBean2;
    }

    public void setSelectionBean2(String selectionBean2) {
        this.selectionBean2 = selectionBean2;
    }

    @Override
    public String toString() {
        return "SelectionListBean{" +
                "selectionBean1='" + selectionBean1 + '\'' +
                ", selectionBean2='" + selectionBean2 + '\'' +
                '}';
    }
}
