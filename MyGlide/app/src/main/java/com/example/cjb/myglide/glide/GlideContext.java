package com.example.cjb.myglide.glide;

import android.content.Context;

import com.example.cjb.myglide.glide.load.Engine;

class GlideContext {

    Context context;
    RequestOption requestOption;
    Engine engine;
    Registry registry;

    public GlideContext(Context context, RequestOption defaultRequestOptions, Engine engine, Registry registry) {
        this.context = context;
        this.requestOption = defaultRequestOptions;
        this.engine = engine;
        this.registry = registry;
    }

    public Context getContext() {
        return context;
    }

    public RequestOption getRequestOption() {
        return requestOption;
    }

    public Engine getEngine() {
        return engine;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRequestOption(RequestOption requestOption) {
        this.requestOption = requestOption;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
