package com.xyw.smartlock.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/5/28.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;// 定义数据库版本号
    private static final String DBNAME = "smartlock.db";// 定义数据库名


    public static final String LockFile_Table = "tb_lockfile";//所档案表名称
    public static final String UnLockRecord_Table = "tb_unlockrecord";//开锁记录表名称
    public static final String UnLock_Table = "tb_unlock";//开锁表名称
    public static final String meter_Table = "tb_meter";//表箱ID


    public DbHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table tb_unlockrecord (_id integer primary key,OP_NAME varchar(50)," +
//                "LID number(16),ADDRESS varchar(100)," +
//                "L_GPS_X text,L_GPS_Y text,deviceid number(10),devicetype varchar(10), L_CREATE_DT varchar(20),"
//                + "L_OPTYPE varchar(10),L_RET varchar(10),mark varchar(200))," +
//                "areaid number(4),areaname varchar(10),lockname varchar(10),L_CREATE_OP number(11)");// 创建解锁标签


        //创建锁档案表
        db.execSQL("create table " + LockFile_Table + " (_id integer primary key,LID varchar(32)," +
                "L_NAME varchar(100),L_ADDR varchar(100)," +
                "L_GPS_X varchar(100),L_GPS_Y varchar(100),L_CREATE_DT varchar(20),L_CREATE_OP varchar(100)," +
                "L_BOX_NO varchar(20),L_BOX_TYPE varchar(2),KEY_VER varchar(2),ZONE_NO varchar(100)," +
                "ZONE_NAME varchar(100),PASSNUM varchar(2))");

        // 创建开锁记录表
        db.execSQL("create table tb_unlockrecord (_id integer primary key,OP_NAME varchar(100),"
                + "L_RET varchar(10),L_OPTYPE varchar(10),L_GPS_X varchar(100),L_GPS_Y varchar(100),L_CREATE_DT varchar(20),"
                + "LID varchar(32),L_CREATE_OP varchar(22))");

        // 创建开锁表
        db.execSQL("create table " + UnLock_Table + " (_id integer primary key,LID varchar(32),"
                + "GPS_X varchar(100),GPS_Y varchar(100),OP_NO varchar(22),OP_TYPE varchar(2),"
                + "OP_RET varchar(10),OP_DATETIME varchar(20),USER_CONTEXT varchar(16))");

        //创建注册锁表箱ID
//        db.execSQL("create table " + meter_Table + " (_id integer primary key autoincrement,LID varchar(32),"
//                + "meter_1 varchar(20), meter_2 varchar(20), meter_3 varchar(20),meter_4 varchar(20),"
//                + "meter_5 varchar(20), meter_6 varchar(20), meter_7 varchar(20),meter_8 varchar(20),"
//                + "meter_9 varchar(20), meter_10 varchar(20), meter_11 varchar(20), meter_12 varchar(20),"
//                + "meter_13 varchar(20), meter_14 varchar(20), meter_15 varchar(20))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //修改版本时执行，修改表结构
//        db.execSQL("drop table if exists " + meter_Table);
//        onCreate(db);
    }
}
