package com.example.cjb.mybutterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.annotation.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Toast.makeText(this,button.getText(),Toast.LENGTH_LONG).show();
    }
}
