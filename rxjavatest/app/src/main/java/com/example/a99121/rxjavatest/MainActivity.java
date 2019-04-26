package com.example.a99121.rxjavatest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Person<? super Fruit> person=new Person<>();
        person.setTag(new Apple());
        person.setTag(new Pear());

    }


    class Person<T>{

        public void setTag(T tag){

        }
    }

    class Fruit{

    }
    class Apple extends Fruit{

    }

    class Pear extends Fruit{

    }
}
