package com.dongnao.butterknifeframwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.dongnao.butterknife_annotion.BindView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.app_text)
      TextView test;
    @BindView(R.id.app_text1)
    TextView textView1;

    @BindView(R.id.app_text1)
    TextView textView2;

    TextView textView3;
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toast.makeText(this,"--------------->"+test,Toast.LENGTH_LONG).show();
    }
}
