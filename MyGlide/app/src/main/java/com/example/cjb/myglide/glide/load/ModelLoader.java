package com.example.cjb.myglide.glide.load;

import com.example.cjb.myglide.glide.cache.Key;
import com.example.cjb.myglide.glide.load.data.DataFetcher;

public interface ModelLoader<Model,Data> {


    interface ModelLoadFactory<Model,Data>{
        ModelLoader<Model,Data> build(ModelLoaderRegistry registry);
    }

    class Loaddata<Data>{
        Key key;

        DataFetcher<Data> fetcher;

        public Loaddata(Key key, DataFetcher<Data> fetcher) {
            this.key = key;
            this.fetcher = fetcher;
        }
    }

    /**
     * 是否可以处理
     * @param model
     * @return
     */
    boolean handles(Model model);
    Loaddata<Data> buildData(Model model);

}
