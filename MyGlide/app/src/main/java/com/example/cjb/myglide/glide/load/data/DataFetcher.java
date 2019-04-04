package com.example.cjb.myglide.glide.load.data;


/**
 * 负责数据的获取
 *
 * @param <Data>
 */
public interface DataFetcher<Data> {
    interface DataFetcherCallBack<Data> {
        void onFetcherReady(Data data);

        void onLoadFailure(Exception e);
    }
    void loadData(DataFetcherCallBack<Data> callback);
    void cancel();

}
