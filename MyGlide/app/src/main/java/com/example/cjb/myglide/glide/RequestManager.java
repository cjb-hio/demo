package com.example.cjb.myglide.glide;

public class RequestManager {

    private Object model;
    public RequestManager(GlideContext glideContext) {

    }

    /**
     * 载入的数据来源
     * @param model
     */
    public RequestBuilder load(Object model){
        this.model=model;
        return new RequestBuilder().load(model);
    }

}
