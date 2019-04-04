package com.xyw.smartlock.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xyw.smartlock.R;
import com.xyw.smartlock.common.LoadingDialog;
import com.xyw.smartlock.utils.ActivityUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import uk.co.senab.photoview.PhotoView;

public class VillageAddressMapActivity extends AppCompatActivity {

    private PhotoView village_address_map;
    private ImageLoader mImageLoader;
    private LoadingDialog mDialog;
    private String imageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_address_map);
        getSupportActionBar().hide();

        initView();
        initData();
    }

    private void initData() {
        mImageLoader = ImageLoader.getInstance();
        if (ActivityUtils.getInstance().isNetworkAvailable(VillageAddressMapActivity.this)) {
//            getImageUrl();
        } else {
            Toast.makeText(VillageAddressMapActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        village_address_map = (PhotoView) findViewById(R.id.village_address_map);
        mDialog = new LoadingDialog(VillageAddressMapActivity.this, R.style.dailogStyle);
        mDialog.setCanceledOnTouchOutside(false);
    }

    private void getImageUrl() {
        String url = "";
        Request<String> req = NoHttp.createStringRequest(url, RequestMethod.POST);
        req.add("", "");
        OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
            @Override
            public void onStart(int i) {
                mDialog.show();
            }

            @Override
            public void onSucceed(int i, Response<String> response) {
                imageUrl = response.get();
            }

            @Override
            public void onFailed(int i, Response<String> response) {
                mDialog.dismiss();
            }

            @Override
            public void onFinish(int i) {
                mDialog.dismiss();
                mImageLoader.displayImage(imageUrl, village_address_map);

            }
        };
        NoHttp.newRequestQueue().add(0, req, onResponseListener);
    }
}
